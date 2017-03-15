angular
	.module("explorer", ["ui.router", "kendo.directives", "pascalprecht.translate", "ngCookies", "ui.bootstrap", "AngularSecurityIDM", "QDate", "QFormValidation"])
	.config(["$stateProvider", "$urlRouterProvider", "$translateProvider", "$translatePartialLoaderProvider", "SecuritySrvProvider", "$httpProvider", \
	($stateProvider, $urlRouterProvider, $translateProvider, $translatePartialLoaderProvider, SecuritySrvProvider, $httpProvider) ->	
		# Configure translations
		$translateProvider.fallbackLanguage('en')
		$translateProvider.preferredLanguage("en")
		$translateProvider.useLocalStorage()
		# HTML-escape all values passed to translations as arguments
		$translateProvider.useSanitizeValueStrategy('escaped')
		$translatePartialLoaderProvider.addPart('explorer')
		$translateProvider.useLoader('$translatePartialLoader',
			urlTemplate: '/api/rest-wd/i18n/translations/{part}?lang={lang}'
		)
		
		# Configure security
		SecuritySrvProvider.setRestPrefix("/api/rest-wd/security-proxy")
		
		# When the $http call returns error check if it is a validation
		# error and if it is not show a generic error messages. Validation
		# errors are handled by the controller.
		$httpProvider.interceptors.push(($q, $injector) ->
			'responseError': (rejection) ->
				$window = $injector.get '$window'
				NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')
				if (rejection.status is 401)
					NotificationSrv.add(
						title: "error_session_expired_title"
						content: "error_session_expired_body"
						audio: true
						error: true
						bubble:
							show: true
					)
				else
					if (rejection.data.validationErrors?)
						NotificationSrv.add(
							title: "error_validation_title"
							content: "error_validation_body"
							audio: true
							error: true
							bubble:
								show: true
						)
					else
						NotificationSrv.add(
							title: "error_generic_request_title"
							content: "error_generic_request_body"
							audio: true
							error: true
							bubble:
								show: true
						)
				$q.reject(rejection)
		)
		
		# Configure routes
		$urlRouterProvider
			.otherwise("/projects")
		
		$stateProvider.state "projects",
			url: "/projects"
			templateUrl: "views/projects/projectList.html"
			data:
				isPublic: false
		$stateProvider.state "projects.create",
			url: "/create"
			templateUrl: "views/projects/createProject.html"
			data:
				isPublic: false
		$stateProvider.state "projects.project",
			url: "/:projectId"
			templateUrl: "views/projects/editProject.html"
			data:
				isPublic: false
				
		# Administration	
		$stateProvider.state "access",
			url: "/access"
			templateUrl: "views/admin/access.html"
			data:
				isPublic: false
				permissions: ['EXP_CONFIGURE']
				permissionMinMatches: 1
		$stateProvider.state "access.project",
			url: "/:projectId"
			templateUrl: "views/admin/projectAccess.html"
			data:
				isPublic: false
				permissions: ['EXP_CONFIGURE']
				permissionMinMatches: 1
		$stateProvider.state "access.project.edit",
			url: "/:ownerId"
			templateUrl: "views/admin/accessDetails.html"
			data:
				isPublic: false
				permissions: ['EXP_CONFIGURE']
				permissionMinMatches: 1
	])
	.run(["$state", "$rootScope", "SecuritySrv", ($state, $rootScope, SecuritySrv) ->
#		Add a reference to $state in the $rootScope so that it can be accessed from any scope in the application
		$rootScope.$state = $state
#		Initialise permissions and security
		$rootScope.permissions = []
		$rootScope.$on "$stateChangeStart", (event, to, toParams, from, fromParams) ->
			unless SecuritySrv.isInit()
				event.preventDefault()
				SecuritySrv.init().then ->
					$state.go to, toParams
					return
			return
	])