angular
	.module("formsManagerApp")
	.controller('FormCtrl', ['$scope', '$state', '$stateParams', '$q', '$window', 'SERVICES', 'FormHttpService', 'FormService', 'FormVersionHttpService', \
			'QFormValidation', 'ProjectHttpService', 'WindowService', 'ResourcesService', 'SecuritySrv',\
			($scope, $state, $stateParams, $q, $window, SERVICES, FormHttpService, FormService, FormVersionHttpService, \
			QFormValidation, ProjectHttpService, WindowService, ResourcesService, SecuritySrv) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		########################### INITIALISATIONS ###########################

		$scope.formService = FormService

		formId = $stateParams.resourceId
		projectId = $stateParams.projectId

		$scope.form = {}

		categories = []

		locales = []

		# The security token in a string format in order to insert it as a query parameter.
		$scope.ticket = JSON.stringify(SecuritySrv.getUser().ticket)

		projectDeferred = ProjectHttpService.getProjectCategories(projectId)
		languagesDeferred = ProjectHttpService.getLanguages()
		formDeferred = FormService.getFormById(formId, projectId, true)

		$q.all([projectDeferred, languagesDeferred, formDeferred]).then(
			success = (results) ->
				Array.prototype.push.apply(categories, results[0].data)
				Array.prototype.push.apply(locales, results[1].data)

				#Load form
				#TODO comment
				form = FormService.getForm()
				$scope.form = form

				$scope.canManageForm = FormService.getCanManageForm()


				if (form.formVersions? && form.formVersions[0]?)
					# If a version is accessed directly, then select this version otherwise select the first version in the list
					versionId = if $stateParams.versionId? then $stateParams.versionId else form.formVersions[0].id

					$state.go "resources.form.version",
						resourceId: form.id
						versionId: versionId
				else
					FormService.setActions(formId)
				return
		)

		########################### DATASOURCES AND KENDO OPTIONS ###########################

		#Actions that can be performed on a form
		$scope.actionsDataSource = new kendo.data.DataSource(
			data: FormService.getActions(),
			sort: { field: "order", dir: "asc" }
		)

		$scope.actionsListTemplate = kendo.template($("#actionsListTemplate").html())

		#Watch changes made to the actions list and update the datasource accordingly
		$scope.$watchCollection('formService.getActions()', (newVal) ->
			$scope.actionsDataSource.read()
		)

		$scope.categoriesDataSource = new kendo.data.DataSource(
			data: categories
		)

		$scope.languageDataSource = new kendo.data.DataSource(
			data: locales
		)

		$scope.$watch('formService.getCanEditVersion()', (newVal) ->
			$scope.canEditVersion = FormService.getCanEditVersion()
			console.log "$scope.canEditVersion: " + $scope.canEditVersion
		)

		########################### FUNCTIONS ###########################
		#Function for updating the metadata of a form
		$scope.save = () ->
			#Pass the selected projectId to the data sent to the server
			$scope.form.projectId = $stateParams.projectId

			formVersion = FormService.getFormVersion()

			if formVersion? && formVersion.state != 1
				# set conditions 'id' which was renamed for kendo grid (rows disappear on edit/cancel)
				# must copy because cannot set the 'id' directly in the datasource
				formVersionConditions = []
				for condition in formVersion.conditions
					versionCondition =
						id: condition.conditionId
						name: condition.name
						conditionType: condition.conditionType
						workingSetId: condition.workingSetId
						ruleId: condition.ruleId
						parentCondition: condition.parentCondition

					formVersionConditions.push(versionCondition)

				$scope.form.versionId = formVersion.id
				$scope.form.versionDescription = formVersion.description
				$scope.form.versionContent = formVersion.content
				$scope.form.versionConditions = formVersionConditions
				$scope.form.versionTranslations = formVersion.translations

			FormHttpService.updateForm(formId, $scope.form).then(
				success = (result) ->
					NotificationSrv.add(
						title: "forms_ui.form_updated_title"
						content: "forms_ui.form_updated_content"
						content_data:
							form: $scope.form.name
						bubble:
							show: true
					)

					#In case the category, under which the form has been selected in the tree,
					#has been removed from the multiselect, then the categoryId in the service should
					#be set to null, to allow the updated form to be selected under another node in the tree
					if !$scope.form.categories or (ResourcesService.getCategoryId() not in $scope.form.categories)
						ResourcesService.setCategoryId(null)

					# If there exist a selected version then go to this version
					if $stateParams.versionId?
						$state.go "resources.form.version",
							{resourceId: $stateParams.resourceId, versionId: $stateParams.versionId},
							reload: true
					else
						$state.go "resources.form",
							{resourceId: $stateParams.resourceId},
							reload: true
					return
				error = (result) ->
					QFormValidation.renderFormErrors($scope, $scope.formForm, result)
			)

		$scope.cancel = (e) ->
			# If there exist a selected version then go to this version
			if $stateParams.versionId?
				$state.go "resources.form.version",
					{resourceId: $stateParams.resourceId, versionId: $stateParams.versionId},
					reload: true
			else
				$state.go "resources.form",
					{resourceId: $stateParams.resourceId},
					reload: true
			return

		$scope.openOrbeonBuilder = (e) ->
			versionId = FormService.getFormVersion().id
			taskbarService = $window.parent.WDUtil.service('TaskbarService')
			orbeonApplication =
				instanceUuid: $window.parent.WDUtil.createUUID()
				applicationUuid: '8db8818b-a8f9-4573-b238-2edbbccc9293'
				title: "forms_ui.orbeon_title"
				content: "apps/forms/views/forms/orbeon.html"
				urlParams: "formVersionId=" + versionId
				icon: "apps/forms/img/orbeon-navbar-logo.png"
				draggable: true
				resizable: true
				width: 1200
				minWidth: 860
				maxWidth: undefined
				height: 400
				minHeight: 300
				maxHeight: undefined
				multipleInstances: true
				actions: (->
					["Maximize", "Minimize", "Close"]
				)()
			taskbarService.openCustomApplication(orbeonApplication)
			$window.parent.WDUtil.scope().$apply()

		#Function for executing method bound to the selected option in the actions dropdown
		$scope.executeFormAction = (e) ->
			e.preventDefault()
			item = e.sender.dataItem(e.item.index())
			eval(item.value + "()")
			return

		#Function for deciding whether to display a confirmation window for the deletion of a form
		#or to inform the user that the form has form versions locked by another user and cannot be deleted
		deleteForm = (e) ->
			FormHttpService.countFormVersionsLockedByOtherUser(formId).then(
				success = (result) ->
					if result.data? and result.data > 0
						WindowService.openWindow("delete_form", "views/forms/errorDeleteForm.html")
					else
						WindowService.openWindow("delete_form", "views/forms/confirmDeleteForm.html")
			)

		createFormVersion = (e) ->
			WindowService.openWindow("new_version", "views/forms/createFormVersion.html")

		lockFormVersion = (e) ->
			versionId = FormService.getFormVersion().id
			if versionId?
				FormVersionHttpService.lockFormVersion(versionId).then(
					success = (result) ->
						NotificationSrv.add(
							title: "forms_ui.form_version_locked_title"
							content: "forms_ui.form_version_locked_content"
							content_data:
								form_version: FormService.getFormVersion().name
							bubble:
								show: true
						)
						FormService.getFormVersionsByFormId($stateParams.resourceId).then(
							success = (result) ->
								$state.go "resources.form.version",
									{resourceId: $stateParams.resourceId, versionId: versionId},
									reload: true
								return
						)
						return
				)

		unlockFormVersion = (e) ->
			versionId = FormService.getFormVersion().id
			if versionId?
				FormVersionHttpService.unlockFormVersion(versionId).then(
					success = (result) ->
						NotificationSrv.add(
							title: "forms_ui.form_version_unlocked_title"
							content: "forms_ui.form_version_unlocked_content"
							content_data:
								form_version: FormService.getFormVersion().name
							bubble:
								show: true
						)
						FormService.getFormVersionsByFormId($stateParams.resourceId).then(
							success = (result) ->
								$state.go "resources.form.version",
									{resourceId: $stateParams.resourceId, versionId: versionId},
									reload: true
								return
						)
						return
				)

		deleteFormVersion = (e) ->
			WindowService.openWindow("delete_version", "views/forms/confirmDeleteFormVersion.html")

		finaliseFormVersion = (e) ->
			versionId = FormService.getFormVersion().id
			if versionId?
				FormVersionHttpService.canFinaliseFormVersion(versionId).then(
					success = (result) ->
						if result.data? and JSON.parse(result.data)
							WindowService.openWindow("finalise_version", "views/forms/confirmFinaliseFormVersion.html")
						else
							WindowService.openWindow("finalise_version", "views/forms/errorFinaliseFormVersion.html")
				)

		enableTestingForFormVersion = (e) ->
			versionId = FormService.getFormVersion().id
			if versionId?
				FormVersionHttpService.enableTestingForFormVersion(versionId).then(
					success = (result) ->
						NotificationSrv.add(
							title: "forms_ui.form_version_enabled_testing_title"
							content: "forms_ui.form_version_enabled_testing_content"
							content_data:
								form_version: FormService.getFormVersion().name
							bubble:
								show: true
						)
						FormService.getFormVersionsByFormId($stateParams.resourceId).then(
							success = (result) ->
								$state.go "resources.form.version",
									{resourceId: $stateParams.resourceId, versionId: versionId},
									reload: true
								return
						)
						return
				)

		disableTestingForFormVersion = (e) ->
			versionId = FormService.getFormVersion().id
			if versionId?
				FormVersionHttpService.disableTestingForFormVersion(versionId).then(
					success = (result) ->
						NotificationSrv.add(
							title: "forms_ui.form_version_disabled_testing_title"
							content: "forms_ui.form_version_disabled_testing_content"
							content_data:
								form_version: FormService.getFormVersion().name
							bubble:
								show: true
						)
						FormService.getFormVersionsByFormId($stateParams.resourceId).then(
							success = (result) ->
								$state.go "resources.form.version",
									{resourceId: $stateParams.resourceId, versionId: versionId},
									reload: true
								return
						)
						return
				)

		importFormVersion = (e) ->
			WindowService.openWindow("import_version", "views/forms/importFormVersion.html")

		exportFormVersion = (e) ->
			versionId = FormService.getFormVersion().id
			if versionId?
				$window.location = SERVICES._PREFIX + SERVICES.FORM_VERSION + "/" + versionId + SERVICES.FORM_VERSION_EXPORT\
				+ "?ticket=" + encodeURIComponent($scope.ticket)
				return
	])
