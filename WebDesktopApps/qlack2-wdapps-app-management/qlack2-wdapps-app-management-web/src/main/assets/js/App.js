angular.module("appManagement", ["ui.router", "kendo.directives", "pascalprecht.translate", 
"ngCookies", "ui.bootstrap", "AngularSecurityIDM", "QNgPubSub"])
.config(["$stateProvider", "$urlRouterProvider", "$translateProvider", 
"$translatePartialLoaderProvider", "SecuritySrvProvider", "$httpProvider", "SERVICES", 
function($stateProvider, $urlRouterProvider, $translateProvider, 
$translatePartialLoaderProvider, SecuritySrvProvider, $httpProvider, SERVICES) {
    $translateProvider.fallbackLanguage('en');
    $translateProvider.preferredLanguage("en");
    $translateProvider.useLocalStorage();
    $translateProvider.useSanitizeValueStrategy('escaped');
    $translatePartialLoaderProvider.addPart('appmanagement');
    $translatePartialLoaderProvider.addPart('wd');
    $translatePartialLoaderProvider.addPart('wd_apps');
    $translateProvider.useLoader('$translatePartialLoader', {
		urlTemplate: '/api/rest-wd/i18n/translations/{part}?lang={lang}'
	});

    SecuritySrvProvider.setRestPrefix("/api/rest-wd/security-proxy");
	SecuritySrvProvider.setStorageName(window.parent.QLACK_WD_TOKEN_HEADER_NAME);
	SecuritySrvProvider.setHeaderName(window.parent.QLACK_WD_TOKEN_HEADER_NAME);
	SecuritySrvProvider.setCookieName(window.parent.QLACK_WD_TOKEN_HEADER_NAME);
    
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
    
    $urlRouterProvider.otherwise("/apps");
    $stateProvider.state("apps", {
      url: "/apps",
      templateUrl: "views/apps.html",
      data: {
        isPublic: false
      }
    });
    $stateProvider.state("apps.application", {
      url: "/:applicationId",
      templateUrl: "views/application.html",
      data: {
        isPublic: false
      }
    });
    $stateProvider.state("access", {
      url: "/access",
      templateUrl: "views/admin/access.html",
      data: {
        isPublic: false,
        permissions: ['APPMANAGEMENT_CONFIGURE'],
        permissionMinMatches: 1
      }
    });
    $stateProvider.state("access.application", {
      url: "/:appId",
      templateUrl: "views/admin/applicationAccess.html",
      data: {
        isPublic: false,
        permissions: ['APPMANAGEMENT_CONFIGURE'],
        permissionMinMatches: 1
      }
    });
    return $stateProvider.state("access.application.edit", {
      url: "/:ownerId",
      templateUrl: "views/admin/accessDetails.html",
      data: {
        isPublic: false,
        permissions: ['APPMANAGEMENT_CONFIGURE'],
        permissionMinMatches: 1
      }
    });
  }
]).run(["$state", "$rootScope", "SecuritySrv", "QNgPubSubService", 
function($state, $rootScope, SecuritySrv, QNgPubSubService) {
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
	QNgPubSubService.init("wdapps-app-management", false);
  }
]);
