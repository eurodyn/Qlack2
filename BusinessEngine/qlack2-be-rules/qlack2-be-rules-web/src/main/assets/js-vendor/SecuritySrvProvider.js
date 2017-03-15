/*
* Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
*
* Licensed under the EUPL, Version 1.1 only (the "License").
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
* https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and
* limitations under the Licence.
*/
/*

 * An implementation of a front-end security service for AngularJS that integrates
 * with Qlack Fuse IDM component.
 *
 * This service emits several events in order to allow your AngularJS controllers
 * to asynchronously react on different situations. Such events are:
 * SECURITYSRV_AUTH_SUCCESS:
 * 		Emitted when the .login() method succeeds. Success entails having
 * 		the user properly authenticated as well as fetching its permissions.
 * SECURITYSRV_AUTH_FAIL:
 * 		Emitted when the .login() method did not manage to authenticate the user,
 * 		or it did not manage to fetch the user permissions.
 * SECURITYSRV_AUTH_RELOAD_SUCCESS:
 * 		Emitted when the service has found a stored token and that token has
 * 		been successfully validated, thus allowing access to the user without
 * 		having to call the .login() method. This is an alternative message allowing
 * 		applications to react when the user identity has been established without
 * 		having to pass by a login screen.
 * SECURITYSRV_AUTH_RELOAD_FAIL:
 * 		Emitted when the service has found a stored token but that token could not
 * 		be validated.
 * SECURITYSRV_AUTH_LOGOUT:
 * 		Emitted when the .logout() method succeeds.
 * SECURITYSRV_HTTP_ERROR:
 * 		A generic event to allow your controllers to capture any sort of HTTP
 * 		error occurred during calls to the underlying IDM service.
 * SECURITYSRV_NOACCESS_AUTH:
 * 		Emitted when a non-public state is being accessed without the user being
 * 		authenticated.
 * SECURITYSRV_NOACCESS_PERM:
 * 		Emitted when a permission-protected state is being accessed without the
 * 		user having the necessary permission.
 */
angular.module('AngularSecurityIDM', [])
	.provider('SecuritySrv', function() {
		// Configuration properties.
		var config = {
			// The URLs for IDM proxy service.
			idm: {
				authenticate: "/authenticate",
				genericPermissions: "/generic-permissions",
				checkPermission: "/check-permission",
				validateTicket: "/validate-ticket",
				userDetails: "/user-details"
			},
			// Token-related details.
			token: {
				// The name of the token when inserted as an HTTP header.
				headerName: "X-Qlack-Fuse-IDM-Token",
				// The name of the token when stored in browser's storage.
				storageName: "X-Qlack-Fuse-IDM-Token",
				// The name of the token when stored as a Cookie. Note that the
				// Cookie token is only used to bootstrap the security service 
				// and once read it is removed and placed in the HTTP headers.
				// This is useful in order to initialise the security service
				// under an SSO scenario where authentication has been performed
				// by a third-party and a QLACK IDM Ticket has been already
				// created server-side.
				cookieName: "X-Qlack-Fuse-IDM-Token",
				// The path under which the Cookie resides which should be 
				// identical to the web context from which the cookie is set.
				// This is required when the Cookie is to be delete, as without 
				// specifying a correct path the Cookie can not be deleted.
				// This can be a comma-delimited list of paths to try to remove
				// the Cookie from them all. This is to support different reverse
				// proxy environment configurations (test, dev, prod) that may
				// set the Cookie under a differenet path.
				cookiePaths: "/"
			},
			allowCache: true,
			debug: false
		};

		this.setRestPrefix = function(prefix) {
			config.idm.authenticate = prefix + config.idm.authenticate;
			config.idm.genericPermissions = prefix + config.idm.genericPermissions;
			config.idm.checkPermission = prefix + config.idm.checkPermission;
			config.idm.validateTicket = prefix + config.idm.validateTicket;
			config.idm.userDetails = prefix + config.idm.userDetails;
		}

		this.setStorageName = function(name) {
			config.token.storageName = name;
		}

		this.setHeaderName = function(name) {
			config.token.headerName = name;
		}
		
		this.setCookieName = function(name) {
			config.token.cookieName = name;
		}
		
		this.setCookiePaths = function(paths) {
			config.token.cookiePaths = paths;
		}

		this.getConfig = function() {
			return config;
		}
		
		this.setDebug = function(debugMode) {
			config.debug = debugMode;
		}

		this.$get = function($http, $rootScope, $state, $q, $timeout) {
			return new SecurityService(config, $http, $rootScope, $state, $q, $timeout);
		}
	});


/* ****************************************************************************
 * Service definition.
 * ***************************************************************************/
function SecurityService(config, $http, $rootScope, $state, $q, $timeout, $cookies) {
	/* ************************************************************************
	 * Private properties to be used internally.
	 * ***********************************************************************/
	// Keeps initialisation state, so that init() is not called more than once.
	_initialised = undefined;
	// The user definition object.
	_user = null;

	/* ************************************************************************
	 * Private methods to be used internally.
	 * ***********************************************************************/
	// Finds common Strings between two arrays of strings. It returns
	// true, if the number of common elements is greater than or equal
	// to the minMatches parameter.
	_permissionChecker = function (array1, array2, minMatches) {
		if ((array1 !== undefined) && (array1.length > 0) &&
			(array2 !== undefined) && (array2.length > 0)) {
			return [array1, array2].shift().filter(function(v) {
				return [array1, array2].every(function(a) {
					return a.indexOf(v) !== -1;
				});
			}).length >= minMatches;
		} else {
			return false;
		}
	},
	// Convenience method to set the user object to a given value, as
	// well as updating the local storage and the default http headers.
	_setUser = function (userObj) {
		if (config.debug) {
			console.debug("Setting user: ", userObj);
		}
		_user = userObj;
		$http.defaults.headers.common[config.token.headerName] =
			JSON.stringify(userObj.ticket);
		_storeToken(userObj);
	},
	// Clears all information previously set with setUser().
	_unsetUser = function() {
		_user = null;
		if (config.debug) {
			console.debug("Unsetting user information.");
			console.debug("Removing HTTP header: " + config.token.headerName);
		}
		delete $http.defaults.headers.common[config.token.headerName];
		_removeStoredToken();
	},
	// A helper method to construct a key-name for a Permission and a (possibly
	// null) Object ID.
	_getKeyName = function(permissionName, objectID) {
		if (objectID !== undefined && objectID !== null) {
			return permissionName + "_" + objectID;
		} else {
			return permissionName;
		}
	},
	_storeToken = function(ticket) {
		if (config.debug) {
			console.debug("Storing token in sesion storage with name " +
					config.token.storageName + ".");
		}
		sessionStorage.setItem(config.token.storageName,
				JSON.stringify(ticket));
	},
	_removeStoredToken = function() {
		if (config.debug) {
			console.debug("Removing token from session storage with name: " +
					config.token.storageName + ".");
		}
		sessionStorage.removeItem(config.token.storageName);
	},
	_getStoredToken = function() {
		if (config.debug) {
			console.debug("Reading token from Cookie with name " + 
					config.token.cookieName + ".");
		}
		token = _getCookie(config.token.cookieName);
		if (config.debug) {
			console.debug("Cookie=", token);
		}
		// Jetty escapes Cookies therefore rendering JSON serialised objects
		// as invalid. Check for such escape here and remove it.
		if (token !== null && token.indexOf("\"") === 0) {
			if (config.debug) {
				console.debug("Found an escaped cookie, will unescape it.");
			}
			// Remove backslashes.
			token = token.replace(/\\/g,'')
			// Remove leading and trailing quotes.
			token = token.substring(1, token.length-1);
			if (config.debug) {
				console.debug("Cookie=", token)
			}
		}
		
		if (token !== null) {
			if (config.debug) {
				console.debug("Found a token on a Cookie, will reset this Cookie now.");
			}
			_removeCookie(config.token.cookieName);
		} else {
			if (config.debug) {
				console.debug("Reading token from session storage with name " +
						config.token.storageName + ".");
			}
			token = sessionStorage.getItem(config.token.storageName);
			if (config.debug) {
				console.debug("Token=", token);
			}
		}
		if (token === null) {
			if (config.debug) {
				console.debug("No token found.");
			}
			return null;
		} else {
			return JSON.parse(token);
		}
	},
	_getCookie = function(cookieName) {
		var name = cookieName + "=";
		var ca = document.cookie.split(';');
		for(var i = 0; i < ca.length; i++) {
			var c = ca[i].trim();
			if (c.indexOf(name) == 0) {
				return c.substring(name.length, c.length);
			}
		}
		return null;
	},
	_removeCookie = function(cookieName) {
		var paths = config.token.cookiePaths.split(",");		
		for (i = 0; i < paths.length; i++) {
			var cookieRemovalCmd = cookieName + "=;path=" + paths[i] + ";expires=Thu, 01 Jan 1970 00:00:01 GMT;"; 
			if (config.debug) {
				console.debug("Removing Cookie: " + cookieName + ", with command: " + cookieRemovalCmd);
			}
			document.cookie = cookieRemovalCmd;
			if (config.debug) {
				console.debug("New Cookies after removal: ", document.cookie);
			}	
		}		
	}

	/* ************************************************************************
	 * Public methods to be used by the callers of this service.
	 * ***********************************************************************/
	// Checks if a security token is already available in the local
	// storage to re-use it and sets up interceptors for state-change
	// in order to apply security rules.
	this.init = function() {
		var deferred = $q.defer();
		if (!_initialised) {
			_initialised = true;
			if (config.debug) {
				console.debug("Initialising SecuritySrv.");
			}
			// Setup state-change interceptors.
			$rootScope.$on('$stateChangeStart',
				function (event, to, toParams, from, fromParams) {
				// Check security of the transition only if the state is protected.
				if (to.data !== undefined && to.data.isPublic !== undefined &&
					!to.data.isPublic) {
					// Non-authenticated users can not access protected
					// states at all.
					if (_user === null) {
						event.preventDefault();
						$rootScope.$emit("SECURITYSRV_NOACCESS_AUTH", {
							to: to,
							toParams: toParams,
							from: from,
							fromParams: fromParams
						});
					} else {
						// Check if a special permission is required to access
						// this state, otherwise since the user is already
						// authenticated at this point, allow the transition.
						if (to.data.permissions !== undefined &&
							to.data.permissionMinMatches !== undefined &&
							to.data.permissionMinMatches > 0 &&
							!_permissionChecker(_user.permissions,
									to.data.permissions, to.data.permissionMinMatches)) {
							event.preventDefault();
							$rootScope.$emit("SECURITYSRV_NOACCESS_PERM", {
								to: to,
								toParams: toParams,
								from: from,
								fromParams: fromParams
							});
						}
					}
				}
			});

			// Update user token from local storage or cookie.
			token = _getStoredToken();
			if (token !== null) {
				if (token.ticket !== undefined && token.ticket !== null) {
					if (config.debug) {
						console.debug("Will now check if the ticket is valid.");
					}
					$http.post(config.idm.validateTicket, {signedTicket: token.ticket})
						.success(function(success) {
							if (success.valid) {
								if (config.debug) {
									console.debug("Ticket found valid.");
								}
								_setUser(token);
								_initialised = true;
								$rootScope.$emit('SECURITYSRV_AUTH_RELOAD_SUCCESS');
								deferred.resolve();
							} else {
								if (config.debug) {
									console.debug("Ticket found invalid.");
								}
								_unsetUser();
								_initialised = true;
								$rootScope.$emit('SECURITYSRV_AUTH_RELOAD_FAIL');
								deferred.resolve();
							}
						}).error(function(error){
							$rootScope.$emit('SECURITYSRV_AUTH_HTTP_ERROR', error);
							_initialised = true;
							deferred.reject(error);
						});
				}
			} else {
				if (config.debug) {
					console.debug("There is nothing to initialise from.")
				}
				_initialised = true;
				deferred.resolve();
			}
		} else {
			if (config.debug) {
				console.debug("SecuritySrv is already initialised.");
			}
			_initialised = true;
			deferred.resolve();
		}

		return deferred.promise;
	},
	// A helper method to expose the _user object used by the service.
	this.getUser = function() {
		return _user;
	},
	// A helper method to allow an application to authenticate
	// using IDM. In order for the application to be notified of
	// what happened during authentication, this method emits several
	// events (events are described on top of this file).
	this.login = function (username, password) {
		authenticateRequest = {
			username: username,
			password: password
		};
		return $http.post(config.idm.authenticate, authenticateRequest ).then(
			function(successAuth) {
				if (successAuth.data !== undefined && successAuth.data !== null
						&& successAuth.data.signedTicket !== undefined
						&& successAuth.data.signedTicket !== null) {
					// Authentication succeeded, so let's get the
					// permissions of this user before we create
					// the user object. Here we add manually the IDM
					// ticket header in the request to fetch
					// the permissions. As soon as the user is
					// established (i.e. setUser succeeds), this
					// header is automatically added in all future
					// HTTP requests via the common headers setup
					// of $http (taking place in setUser).
					var httpConfig = {headers:{}};
					httpConfig.headers[config.token.headerName] =
						JSON.stringify(successAuth.data.signedTicket);
					return $http.get(config.idm.genericPermissions, httpConfig)
						.success(function(successPerm) {
							_setUser({
								ticket: successAuth.data.signedTicket,
								permissions: successPerm
							});
							$rootScope.$emit('SECURITYSRV_AUTH_SUCCESS');
						}).error(function(error){
							$rootScope.$emit('SECURITYSRV_AUTH_HTTP_ERROR', error);
						});
				} else {
					$rootScope.$emit('SECURITYSRV_AUTH_FAIL');
				}
			}, function (errorAuth) {
				$rootScope.$emit('SECURITYSRV_AUTH_HTTP_ERROR', errorAuth);
			}
		);
	},
	// A helper function to logout the user by clearing all locally
	// held information (AngularJS model and local storage).
	this.logout = function() {
		_unsetUser();
		$rootScope.$emit("SECURITYSRV_AUTH_LOGOUT");
	},
	// Gets all "generic" permissions (i.e. permissions without a target objecID)
	// for the already authenticated user.
	this.getGenericPermissions = function() {
		var reply = $q.defer();
		if (_user !== null) {
			return $http.get(config.idm.genericPermissions)
				.success(function(success) {
					return success;
				}).error(function(error){
					$rootScope.$emit('SECURITYSRV_AUTH_HTTP_ERROR', error);
					return error;
				});
		} else {
			reply.resolve();
		}
		return reply.promise;
	},
	// Validates the ticket available in the HTTP Header of this request.
	this.validateTicketHeader = function() {
		var reply = $q.defer();
		if (_user !== null) {
			return $http.post(config.idm.validateTicket,
					{signedTicket: _user.ticket})
				.success(function(success) {
					return success;
				}).error(function(error){
					$rootScope.$emit('SECURITYSRV_AUTH_HTTP_ERROR', error);
					return error;
				});
		} else {
			reply.resolve();
		}
		return reply.promise;
	},
	// Checks if the owner of the ticket is the user ID passed in.
	// This is a convenience function, since IDM does not really know who is
	// the owner of the ticket. What this function merely does is to check
	// if the ticket is valid and if the the user ID associated with the ticket
	// equals to the user ID passed in.
	this.isTicketOwner = function(ticketOwnerID) {
		var reply = $q.defer();
		if (_user !== null) {
			return $http.post(config.idm.validateTicket,
					{signedTicket: _user.ticket})
				.success(function(success) {
					return _user.ticket.userID === ticketOwnerID;
				}).error(function(error){
					$rootScope.$emit('SECURITYSRV_AUTH_HTTP_ERROR', error);
					return error;
				});
		} else {
			reply.resolve();
		}
		return reply.promise;
	},
	// Returns the full user details as these are available in AAA.
	this.getUserDetails = function() {
		var reply = $q.defer();
		if (_user !== null) {
			return $http.get(config.idm.userDetails)
				.success(function(success) {
					return success;
				}).error(function(error){
					$rootScope.$emit('SECURITYSRV_AUTH_HTTP_ERROR', error);
					return error;
				});
		} else {
			reply.resolve();
		}
		return reply.promise;
	},
	this.isInit = function() {
		return _initialised;
	},
	this.resolvePermission = function(permissionName, objectID, useCache) {
		var deferred = $q.defer();
		// Check if caching should be allowed and set default value for useCache
		if (!config.allowCache) {
			useCache = false;
		}
		if (useCache === undefined) {
			useCache = true;
		}
		if (_user !== null) {
			// Check if the cache should be used, ie. if we should check if the permission
			// is already available in the permissions array before resolving it with the server.
			if (useCache && ($rootScope.permissions[_getKeyName(permissionName, objectID)] !== undefined)) {
				deferred.resolve(true);
			} else {
				var request = {
					permission: permissionName,
					objectID: objectID
				}
				$http.post(config.idm.checkPermission, request)
					.success(function(success) {
						var successObj = null;
						if (success !== "") {
							successObj = JSON.parse(success);
						}
						$rootScope.permissions[_getKeyName(permissionName, objectID)]
							= successObj;
						deferred.resolve(successObj);
					}).error(function(error) {
						$rootScope.$emit('SECURITYSRV_AUTH_HTTP_ERROR', error);
						deferred.reject(error);
					});
			}
		} else {
			deferred.resolve(false);
		}

		return deferred.promise;
	}
}
