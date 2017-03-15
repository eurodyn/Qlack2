angular
	.module("workflowApp", ["kendo.directives", "ui.router", "ui.bootstrap", "ui.ace", "pascalprecht.translate", "flow", "AngularSecurityIDM", "pascalprecht.translate", "ngCookies", "QDate", "QFormValidation"])
	.config(["$stateProvider", "$urlRouterProvider", "flowFactoryProvider", "SecuritySrvProvider", "$translateProvider", "$translatePartialLoaderProvider", "$httpProvider", ($stateProvider, $urlRouterProvider, flowFactoryProvider, SecuritySrvProvider, $translateProvider, $translatePartialLoaderProvider, $httpProvider) ->
		# Configure security
		SecuritySrvProvider.setRestPrefix("/api/qbe/security-proxy")
		
		# Configure translations
		$translateProvider.fallbackLanguage('en')
		$translateProvider.preferredLanguage("en")
		$translateProvider.useLocalStorage() 
		# HTML-escape all values passed to translations as arguments 
		$translateProvider.useSanitizeValueStrategy('escaped') 
		# Log missing translations 
		$translateProvider.useMissingTranslationHandlerLog() 
		$translatePartialLoaderProvider.addPart('workflow_ui') 
		$translateProvider.useLoader('$translatePartialLoader', urlTemplate: '/api/qbe/i18n/translations/{part}?lang={lang}')
		
		flowFactoryProvider.defaults =
			target: "/api/apps/workflow/file-upload/upload"
			permanentErrors: [404, 500, 501]
			maxChunkRetries: 1
			chunkRetryInterval: 5000
			simultaneousUploads: 4
			headers:
				"X-Qlack-Fuse-IDM-Token": JSON.stringify(JSON.parse(sessionStorage.getItem("X-Qlack-Fuse-IDM-Token")).ticket)
			generateUniqueIdentifier: () ->
				"xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace /[xy]/g, (c) ->
					r = Math.random() * 16 | 0
					v = (if c is "x" then r else (r & 0x3 | 0x8))
					v.toString 16
		
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
			.otherwise("/welcome")

		$stateProvider.state "welcome",
			url: "/welcome"
			templateUrl: "views/welcome.html"
			data:
				isPublic: false

		$stateProvider.state "resources",
			url: "/projects/:projectId"
			templateUrl: "views/resources.html"
			data:
				isPublic: false
				
		$stateProvider.state "runtime",
			url: "/runtime/:projectId"
			templateUrl: "views/runtime/workflowRuntime.html"
			data:
				isPublic: false
				
		$stateProvider.state "runtimeerror",
			url: "/runtimeerror/:projectId"
			templateUrl: "views/runtime/workflowLogRuntime.html"
			data:
				isPublic: false
				
		# workflow pages
		$stateProvider.state "resources.workflow",
			url: "/workflow/:resourceId"
			templateUrl: "views/workflow/workflow.html"
			data:
				isPublic: false
				
		$stateProvider.state "resources.newWorkflow",
			url: "/workflow"
			templateUrl: "views/workflow/createWorkflow.html"
			data:
				isPublic: false
		
		$stateProvider.state "resources.workflow.version",
			url: "/version/:versionId"
			templateUrl: "views/workflow/workflowVersion.html"
			data:
				isPublic: false
				
		# category pages
		$stateProvider.state "resources.category",
			url: "/category/:resourceId"
			templateUrl: "views/category/category.html"
			data:
				isPublic: false
				
		$stateProvider.state "resources.newCategory",
			url: "/category"
			templateUrl: "views/category/createCategory.html"
			data:
				isPublic: false
				
		# Administration	
		$stateProvider.state "access",
			url: "/access"
			templateUrl: "views/admin/access.html"
			data:
				isPublic: false
				permissions: ['WFL_CONFIGURE']
				permissionMinMatches: 1
		$stateProvider.state "access.resource",
			url: "/:resourceId"
			templateUrl: "views/admin/resourceAccess.html"
			data:
				isPublic: false
				permissions: ['WFL_CONFIGURE']
				permissionMinMatches: 1
		$stateProvider.state "access.resource.edit",
			url: "/:ownerId"
			templateUrl: "views/admin/accessDetails.html"
			data:
				isPublic: false
				permissions: ['WFL_CONFIGURE']
				permissionMinMatches: 1
	])
	.run(["$rootScope", "SecuritySrv", "$state", ($rootScope, SecuritySrv, $state) ->
		# Add a reference to $state in the $rootScope so that it can be accessed from any scope in the application
		$rootScope.$state = $state
		
		$rootScope.permissions = []

		$rootScope.$on('$stateChangeStart', (event, to, toParams, from, fromParams) ->
			if (!SecuritySrv.isInit())
				event.preventDefault()
				SecuritySrv.init().then(() ->
					$state.go(to, toParams);
				)
		)
		#Set configuration path for ace editor. The js files for the modes can be found under js path
		ace.config.set("modePath", "js")
		#ace.config.set("themePath", "js")
	])