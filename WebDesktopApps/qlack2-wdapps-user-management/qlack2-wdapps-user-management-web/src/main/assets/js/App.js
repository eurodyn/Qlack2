angular.module("userManagement", ["ui.router", "kendo.directives", 
"pascalprecht.translate", "ngCookies","ui.bootstrap", "AngularSecurityIDM", 
"QFormValidation", "QNgPubSub"])
.config(["$stateProvider", "$urlRouterProvider", "$translateProvider", 
"$translatePartialLoaderProvider", "SecuritySrvProvider", "$tooltipProvider", 
"$httpProvider", "$provide", "SERVICES",
function($stateProvider, $urlRouterProvider, $translateProvider, 
$translatePartialLoaderProvider, SecuritySrvProvider, $tooltipProvider, 
$httpProvider, $provide, SERVICES) {
	console.debug("Starting application configuration.");
	$translateProvider.fallbackLanguage("en");
	$translateProvider.preferredLanguage("en");
	$translateProvider.useLocalStorage();
	$translateProvider.useSanitizeValueStrategy('escaped');
	$translatePartialLoaderProvider.addPart('usermanagement');
	$translateProvider.useLoader('$translatePartialLoader', {
		urlTemplate: '/api/rest-wd/i18n/translations/{part}?lang={lang}'
	});

	SecuritySrvProvider.setRestPrefix("/api/rest-wd/security-proxy");
    SecuritySrvProvider.setStorageName(window.parent.QLACK_WD_TOKEN_HEADER_NAME);
    SecuritySrvProvider.setHeaderName(window.parent.QLACK_WD_TOKEN_HEADER_NAME);
    SecuritySrvProvider.setCookieName(window.parent.QLACK_WD_TOKEN_HEADER_NAME);

	$tooltipProvider.options({
		animation: false
	});
	
	// Setup default HTTP interceptors.
	$httpProvider.interceptors.push(function($q, $injector) {
      return {
        'responseError': function(rejection) {
          var QNgPubSubService = $injector.get("QNgPubSubService");
          if (rejection.status === 401) {
        	  QNgPubSubService.publish(SERVICES.WD_SERVICE_TOPICS.NOTIFICATION_SERVICE, {
	              title: "error_session_expired_title",
	              content: "error_session_expired_body",
	              audio: true,
	              type: "error"
	            });
          } else {
            if ((rejection.data.validationErrors != null)) {
            	QNgPubSubService.publish(SERVICES.WD_SERVICE_TOPICS.NOTIFICATION_SERVICE, {
	                title: "error_validation_title",
	                content: "error_validation_body",
	                audio: true,
	                type: "error"
	              });
            } else {
            	QNgPubSubService.publish(SERVICES.WD_SERVICE_TOPICS.NOTIFICATION_SERVICE, {
	                title: "error_generic_request_title",
	                content: "error_generic_request_body",
	                audio: true,
	                type: "error"
	              });
            }
          }
          return $q.reject(rejection);
        }
      };
    });
	
    $urlRouterProvider.otherwise("/users");
    $stateProvider.state("users", {
      url: "/users",
      templateUrl: "views/users/userList.html",
      data: {
        isPublic: false
      }
    });
    $stateProvider.state("users.create", {
      url: "/create",
      templateUrl: "views/users/createUser.html",
      data: {
        isPublic: false,
        permissions: ['WD_MANAGE_USERS'],
        permissionMinMatches: 1
      }
    });
    $stateProvider.state("users.user", {
      url: "/:userId",
      templateUrl: "views/users/editUser.html",
      data: {
        isPublic: false
      }
    });
    $stateProvider.state("groups", {
      url: "/groups",
      templateUrl: "views/groups/groupList.html",
      data: {
        isPublic: false
      }
    });
    $stateProvider.state("groups.group", {
      url: "/:groupId?domain",
      templateUrl: "views/groups/editGroup.html",
      data: {
        isPublic: false
      }
    });
    $stateProvider.state("groups.create", {
      url: "/:groupId/create",
      templateUrl: "views/groups/createGroup.html",
      data: {
        isPublic: false,
        permissions: ['WD_MANAGE_GROUPS'],
        permissionMinMatches: 1
      }
    });
    $stateProvider.state("access", {
      url: "/access",
      templateUrl: "views/admin/access.html",
      data: {
        isPublic: false,
        permissions: ['USERMANAGEMENT_CONFIGURE'],
        permissionMinMatches: 1
      }
    });
    $stateProvider.state("access.edit", {
      url: "/:ownerId",
      templateUrl: "views/admin/accessDetails.html",
      data: {
        isPublic: false,
        permissions: ['USERMANAGEMENT_CONFIGURE'],
        permissionMinMatches: 1
      }
    });
    
	console.debug("Completed application configuration.");
  }
]).run(["$state", "$rootScope", "SecuritySrv", "QNgPubSubService",
function($state, $rootScope, SecuritySrv, QNgPubSubService) {
	console.debug("Starting application run-block.");
	$rootScope.$state = $state;
	$rootScope.permissions = [];
	
	// Setup security interceptor.
	$rootScope.$on("$stateChangeStart", function(event, to, toParams, from, fromParams) {
		if (!SecuritySrv.isInit()) {
			console.debug("Initialising security.");
			event.preventDefault();
			SecuritySrv.init().then(function() {
				console.debug("Security initialised.");
				$state.go(to, toParams);
			});
		}
	});
	
	// Setup QPubSub.
	QNgPubSubService.init("wdapps-user-management", false);
	
	console.debug("Completed application run-block.");
}]);