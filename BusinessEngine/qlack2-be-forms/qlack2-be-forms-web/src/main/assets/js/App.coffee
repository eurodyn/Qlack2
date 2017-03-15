angular
	.module("formsManagerApp", ["kendo.directives", "ui.router", "ui.bootstrap", "ui.ace", "AngularSecurityIDM", "pascalprecht.translate", "ngCookies", "QDate", "QFormValidation", "flow"])
	.config(["$stateProvider", "$urlRouterProvider", "SecuritySrvProvider", "$translateProvider", "$translatePartialLoaderProvider", "$tooltipProvider", "$httpProvider", "flowFactoryProvider", \
			($stateProvider, $urlRouterProvider, SecuritySrvProvider, $translateProvider, $translatePartialLoaderProvider, $tooltipProvider, $httpProvider, flowFactoryProvider) ->
		# Configure security
		SecuritySrvProvider.setRestPrefix("/api/qbe/security-proxy")
		SecuritySrvProvider.getConfig().allowCache = false

		# Configure translations
		$translateProvider.fallbackLanguage('en')
		$translateProvider.preferredLanguage("en")
		$translateProvider.useLocalStorage()
		# HTML-escape all values passed to translations as arguments
		$translateProvider.useSanitizeValueStrategy('escaped')

		# Log missing translations
#		$translateProvider.useMissingTranslationHandlerLog()

		$translatePartialLoaderProvider.addPart('forms_ui')
		$translateProvider.useLoader('$translatePartialLoader',
			urlTemplate: '/api/qbe/i18n/translations/{part}?lang={lang}'
		)

		$tooltipProvider.options(animation: false)

		flowFactoryProvider.defaults =
			target: "/api/apps/forms/file-upload/upload"
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
			.otherwise("/home")

		$stateProvider.state "home",
			url: "/home"
			templateUrl: "views/home.html"
			data:
				isPublic: false

		$stateProvider.state "resources",
			url: "/projects/:projectId"
			templateUrl: "views/resources.html"
			data:
				isPublic: false

		$stateProvider.state "resources.form",
			url: "/forms/:resourceId"
			templateUrl: "views/forms/form.html"
			data:
				isPublic: false

		$stateProvider.state "resources.createForm",
			url: "/forms"
			templateUrl: "views/forms/createForm.html"
			data:
				isPublic: false

		$stateProvider.state "resources.form.version",
			url: "/version/:versionId"
			templateUrl: "views/forms/formVersion.html"
			data:
				isPublic: false

		$stateProvider.state "resources.category",
			url: "/categories/:resourceId"
			templateUrl: "views/categories/category.html"
			data:
				isPublic: false

		$stateProvider.state "resources.createCategory",
			url: "/categories"
			templateUrl: "views/categories/createCategory.html"
			data:
				isPublic: false

		# Administration
		$stateProvider.state "access",
			url: "/access"
			templateUrl: "views/admin/access.html"
			data:
				isPublic: false
				permissions: ['FRM_CONFIGURE']
				permissionMinMatches: 1
		$stateProvider.state "access.resource",
			url: "/:resourceId"
			templateUrl: "views/admin/resourceAccess.html"
			data:
				isPublic: false
				permissions: ['FRM_CONFIGURE']
				permissionMinMatches: 1
		$stateProvider.state "access.resource.edit",
			url: "/:ownerId"
			templateUrl: "views/admin/accessDetails.html"
			data:
				isPublic: false
				permissions: ['FRM_CONFIGURE']
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