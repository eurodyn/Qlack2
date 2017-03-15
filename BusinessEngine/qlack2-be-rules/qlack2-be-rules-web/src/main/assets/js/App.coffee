angular.module("rules", [
		"ui.router"
		"ui.bootstrap"
		"kendo.directives"
		"flow"
		"AngularSecurityIDM"
		"ngCookies"
		"pascalprecht.translate"
		"QDate"
		"QFormValidation"
	])
	.config(["$stateProvider", "$urlRouterProvider", "SecuritySrvProvider", "$translateProvider", "$translatePartialLoaderProvider", "flowFactoryProvider", "$tooltipProvider", "$httpProvider", \
			 ($stateProvider,   $urlRouterProvider,   SecuritySrvProvider,   $translateProvider,   $translatePartialLoaderProvider,   flowFactoryProvider,   $tooltipProvider, $httpProvider) ->

		# Configure security
		SecuritySrvProvider.setRestPrefix("/api/qbe/security-proxy")
		SecuritySrvProvider.getConfig().allowCache = false # XXX enable on production ?

		# Configure translations
		$translateProvider.fallbackLanguage('en')
		$translateProvider.preferredLanguage('en')
		$translateProvider.useLocalStorage()
		# HTML-escape all values passed to translations as arguments
		$translateProvider.useSanitizeValueStrategy('escaped')
		$translatePartialLoaderProvider.addPart('rules')
		$translateProvider.useLoader('$translatePartialLoader',
			urlTemplate: '/api/qbe/i18n/translations/{part}?lang={lang}'
		)

		# Configure upload
		flowFactoryProvider.defaults =
			target: "/api/apps/rules/file-upload/upload"
			permanentErrors: [404, 500, 501]
			maxChunkRetries: 1
			chunkRetryInterval: 5000
			simultaneousUploads: 4
			generateUniqueIdentifier: () ->
				"xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace /[xy]/g, (c) ->
					r = Math.random() * 16 | 0
					v = (if c is "x" then r else (r & 0x3 | 0x8))
					v.toString 16

		flowFactoryProvider.factory = fustyFlowFactory
		
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

		# Disable tooltip animations for chrome
		$tooltipProvider.options(animation: false)

		$urlRouterProvider
			.otherwise("/home")

		$stateProvider
			.state "home",
				url: "/home"
				templateUrl: "views/home.html"
				data:
					isPublic: false

			.state "tree",
				url: "/projects/:projectId"
				templateUrl: "views/tree.html"
				data:
					isPublic: false

			.state "tree.categories-new",
				url: "/categories/new"
				templateUrl: "views/categories/create.html"
				data:
					isPublic: false

			.state "tree.categories-edit",
				url: "/categories/:categoryId"
				templateUrl: "views/categories/edit.html"
				data:
					isPublic: false

			.state "tree.rules-new",
				url: "/rules/new"
				templateUrl: "views/rules/create.html"
				data:
					isPublic: false

			.state "tree.rules-edit",
				url: "/rules/:ruleId"
				templateUrl: "views/rules/edit.html"
				data:
					isPublic: false

			.state "tree.rules-edit.versions",
				url: "/versions/:versionId"
				templateUrl: "views/rules/version.html"
				data:
					isPublic: false

			.state "tree.models-new",
				url: "/models/new"
				templateUrl: "views/models/create.html"
				data:
					isPublic: false

			.state "tree.models-edit",
				url: "/models/:modelId"
				templateUrl: "views/models/edit.html"
				data:
					isPublic: false

			.state "tree.models-edit.versions",
				url: "/versions/:versionId"
				templateUrl: "views/models/version.html"
				data:
					isPublic: false

			.state "tree.libraries-new",
				url: "/libraries/new"
				templateUrl: "views/libraries/create.html"
				data:
					isPublic: false

			.state "tree.libraries-edit",
				url: "/libraries/:libraryId"
				templateUrl: "views/libraries/edit.html"
				data:
					isPublic: false

			.state "tree.libraries-edit.versions",
				url: "/versions/:versionId"
				templateUrl: "views/libraries/version.html"
				data:
					isPublic: false

			.state "tree.working-sets-new",
				url: "/working-sets/new"
				templateUrl: "views/working-sets/create.html"
				data:
					isPublic: false

			.state "tree.working-sets-edit",
				url: "/working-sets/:workingSetId"
				templateUrl: "views/working-sets/edit.html"
				data:
					isPublic: false

			.state "tree.working-sets-edit.versions",
				url: "/versions/:versionId"
				templateUrl: "views/working-sets/version.html"
				data:
					isPublic: false
					
			# Administration	
			$stateProvider.state "access",
				url: "/access"
				templateUrl: "views/admin/access.html"
				data:
					isPublic: false
					permissions: ['RUL_CONFIGURE']
					permissionMinMatches: 1
			$stateProvider.state "access.resource",
				url: "/:resourceId"
				templateUrl: "views/admin/resourceAccess.html"
				data:
					isPublic: false
					permissions: ['RUL_CONFIGURE']
					permissionMinMatches: 1
			$stateProvider.state "access.resource.edit",
				url: "/:ownerId"
				templateUrl: "views/admin/accessDetails.html"
				data:
					isPublic: false
					permissions: ['RUL_CONFIGURE']
					permissionMinMatches: 1
	])
	.run(["$rootScope", "$state", "SecuritySrv", ($rootScope, $state, SecuritySrv) ->
		$rootScope.$state = $state

		$rootScope.permissions = {}

		$rootScope.$on('$stateChangeStart', (event, to, toParams, from, fromParams) ->
			if (!SecuritySrv.isInit())
				event.preventDefault()
				SecuritySrv.init().then(() ->
					$state.go(to, toParams)
				)
		)
	])
