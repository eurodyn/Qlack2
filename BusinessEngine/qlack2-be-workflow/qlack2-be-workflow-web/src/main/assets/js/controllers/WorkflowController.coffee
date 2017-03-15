workflowManagerApp = angular.module("workflowApp")

workflowManagerApp.controller('WorkflowCtrl',['$scope', '$state', '$stateParams', '$q', 'WorkflowHttpService', 'RuntimeHttpService', 'WorkflowVersionHttpService', 'QFormValidation', 'UtilService', 'SecuritySrv', 'QDateSrv', 'WorkflowService', 'ProjectHttpService', 'WindowService', 'ResourceService', '$window', 'SERVICES', ($scope, $state, $stateParams, $q, WorkflowHttpService, RuntimeHttpService, WorkflowVersionHttpService, QFormValidation, UtilService, SecuritySrv, QDateSrv, WorkflowService, ProjectHttpService, WindowService, ResourceService, $window, SERVICES) ->
	NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')
	workflowId = $stateParams.resourceId
	console.log "selectedWorkflowId: " + workflowId

	$scope.workflowService = WorkflowService
	$scope.ticket = JSON.stringify(SecuritySrv.getUser().ticket)

	categories = []
	executeParameters = {}

	projectDeferred = ProjectHttpService.getCategoriesByProjectId($stateParams.projectId)
	workflowDeferred = WorkflowService.getWorkflowById(workflowId, $state.params.projectId, true)

	$q.all([projectDeferred, workflowDeferred]).then(
		success = (results) ->
			Array.prototype.push.apply(categories, results[0].data)

			workflow = WorkflowService.getWorkflow()
			$scope.workflow = workflow

			$scope.canManageWorkflow = WorkflowService.getCanManageWorkflow()
			$scope.canExecuteWorkflow = WorkflowService.getCanExecuteWorkflow()

			if workflow.versions? && workflow.versions[0]?
				# If a version is accessed directly, then select this version otherwise select the first version in the list
				versionId = if $stateParams.versionId? then $stateParams.versionId else workflow.versions[0].id

				$state.go "resources.workflow.version",
					resourceId: workflow.id
					versionId: versionId
			else
				WorkflowService.setActions()
			return
		)

	$scope.workflowCategories = {
		dataSource: new kendo.data.DataSource(
			data: categories
		),
		dataTextField:"name",
		dataValueField:"id"
	}

	$scope.wflActions = new kendo.data.DataSource(
		data: WorkflowService.getActions(),
		sort: { field: "order", dir: "asc" }
	)
	$scope.actionsListTemplate = kendo.template($("#actionsListTemplate").html())

	#Function for updating the metadata of the workflow
	$scope.save = () ->
		#Pass the selected projectId to the data sent to the server
		$scope.workflow.projectId = $state.params.projectId

		workflowVersion = WorkflowService.getWorkflowVersion()

		if workflowVersion? && workflowVersion.state != 1
			# set conditions 'id' which was renamed for kendo grid (rows disappear on edit/cancel)
			# must copy because cannot set the 'id' directly in the datasource
			workflowVersionConditions = []
			if (workflowVersion.conditions)
				for condition in workflowVersion.conditions
					versionCondition =
						id: condition.conditionId
						name: condition.name
						conditionType: condition.conditionType
						workingSetId: condition.workingSetId
						ruleId: condition.ruleId
						parentCondition: condition.parentCondition

					workflowVersionConditions.push(versionCondition)

			$scope.workflow.versionDetails = {}
			$scope.workflow.versionDetails.id = workflowVersion.id
			$scope.workflow.versionDetails.name = workflowVersion.name
			$scope.workflow.versionDetails.description = workflowVersion.description
			$scope.workflow.versionDetails.content = workflowVersion.content
			$scope.workflow.versionDetails.versionConditions = workflowVersionConditions
		else
			$scope.workflow.versionDetails = null

		WorkflowHttpService.update(workflowId, $scope.workflow).then(
			success = (result) ->
				console.log "success in workflow update"

				NotificationSrv.add(
						title: "workflow_ui.workflow_updated_title"
						content: "workflow_ui.workflow_updated_content"
						content_data:
							workflow: $scope.workflow.name
						bubble:
							show: true
					)

				#If there exist a selected version then go to this version
				if !$scope.workflow.categories or (ResourceService.getCategoryId() not in $scope.workflow.categories)
					ResourceService.setCategoryId(null)

				if $stateParams.versionId?
					$state.go "resources.workflow.version",
						{resourceId: $stateParams.resourceId, versionId: $stateParams.versionId},
						reload: true
				else
					$state.go "resources.workflow",
						{resourceId: $stateParams.resourceId},
						reload: true
				return
			error = (result) ->
				console.log "error in workflow update"
				QFormValidation.renderFormErrors($scope, $scope.workflowForm, result)
		)

	$scope.$watchCollection('workflowService.getActions()', (newVal) ->
		$scope.wflActions.read()
	)

	$scope.$watch('workflowService.getCanEditVersion()', (newVal) ->
		$scope.canEditVersion = WorkflowService.getCanEditVersion()
		console.log "$scope.canEditVersion: " + $scope.canEditVersion
	)

	$scope.$watch('workflowService.getCanExecuteVersion()', (newVal) ->
		$scope.canExecuteVersion = WorkflowService.getCanExecuteVersion()
		console.log "$scope.canExecuteVersion: " + $scope.canExecuteVersion
	)

	$scope.selectWorkflowAction = (e) ->
		e.preventDefault()
		item = e.sender.dataItem(e.item.index())
		eval(item.value + "()")
		return

	createVersionAction = (e) ->
		WindowService.openWindow("create_version", "views/workflow/createWorkflowVersion.html")

	deleteWorkflowAction = (e) ->
		WorkflowHttpService.countWorkflowVersionsLockedByOtherUser(workflowId).then(
			success = (result) ->
				if result.data? and result.data > 0
					WindowService.openWindow("delete_workflow", "views/workflow/errorDeleteWorkflow.html")
				else
					WindowService.openWindow("delete_workflow", "views/workflow/confirmDeleteWorkflow.html")
			error = (result) ->
				console.log "error in calling count resources of workflow..."
		)


	deleteVersionAction = (e) ->
		WindowService.openWindow("delete_version", "views/workflow/confirmDeleteWorkflowVersion.html")

	finaliseVersionAction = (e) ->
		WorkflowVersionHttpService.checkWorkflowVersionCanFinalise(WorkflowService.getWorkflowVersion().id).then(
			success = (result) ->
				if result.data? and result.data == 'true'
					WindowService.openWindow("finalize", "views/workflow/confirmFinaliseWorkflowVersion.html")
				else
					WindowService.openWindow("finalize", "views/workflow/errorFinaliseWorkflowVersion.html")
			error = (result) ->
				console.log "error in calling checkWorkflowVersionCanFinalise..."
		)


	lockAction = (e) ->
		versionId = WorkflowService.getWorkflowVersion().id
		if versionId? and versionId isnt ''
			WorkflowVersionHttpService.lock(versionId).then(
				success = (result) ->
					WorkflowService.getWorkflowVersionsByWorkflowId($stateParams.resourceId).then(
						success = (result) ->
							NotificationSrv.add(
								title: "workflow_ui.workflow_version_locked_title"
								content: "workflow_ui.workflow_version_locked_content"
								content_data:
									workflow_version: WorkflowService.getWorkflowVersion().name
								bubble:
									show: true
							)

							$state.go "resources.workflow.version",
								{resourceId: $stateParams.resourceId, versionId: versionId},
								reload: true
							return
						)
				error = (result) ->
					console.log "There was an error while locking the workflow version"
			)

	unlockAction = (e) ->
		versionId = WorkflowService.getWorkflowVersion().id
		if versionId? and versionId isnt ''
			WorkflowVersionHttpService.unlock(versionId).then(
				success = (result) ->
					WorkflowService.getWorkflowVersionsByWorkflowId($stateParams.resourceId).then(
						success = (result) ->
							NotificationSrv.add(
								title: "workflow_ui.workflow_version_unlocked_title"
								content: "workflow_ui.workflow_version_unlocked_content"
								content_data:
									workflow_version: WorkflowService.getWorkflowVersion().name
								bubble:
									show: true
							)

							$state.go "resources.workflow.version",
								{resourceId: $stateParams.resourceId, versionId: versionId},
								reload: true
							return
						)
				error = (result) ->
					console.log "There was an error while unlocking the workflow version"
			)

	enableTestingVersionAction = (e) ->
		versionId = WorkflowService.getWorkflowVersion().id
		if versionId? and versionId isnt ''
			WorkflowVersionHttpService.enableTestingWorkflowVersion(versionId).then(
				success = (result) ->
					WorkflowService.getWorkflowVersionsByWorkflowId($stateParams.resourceId).then(
						success = (result) ->
							NotificationSrv.add(
								title: "workflow_ui.workflow_version_enabled_testing_title"
								content: "workflow_ui.workflow_version_enabled_testing_content"
								content_data:
									workflow_version: WorkflowService.getWorkflowVersion().name
								bubble:
									show: true
							)
							$state.go "resources.workflow.version",
								{resourceId: $stateParams.resourceId, versionId: versionId},
								reload: true
							return
						)
				error = (result) ->
					console.log "There was an error while enable testing of workflow version"
			)

	disableTestingVersionAction = (e) ->
		versionId = WorkflowService.getWorkflowVersion().id
		if versionId? and versionId isnt ''
			WorkflowVersionHttpService.disableTestingWorkflowVersion(versionId).then(
				success = (result) ->
					WorkflowService.getWorkflowVersionsByWorkflowId($stateParams.resourceId).then(
						success = (result) ->
							NotificationSrv.add(
								title: "workflow_ui.workflow_version_disabled_testing_title"
								content: "workflow_ui.workflow_version_disabled_testing_content"
								content_data:
									workflow_version: WorkflowService.getWorkflowVersion().name
								bubble:
									show: true
							)
							$state.go "resources.workflow.version",
								{resourceId: $stateParams.resourceId, versionId: versionId},
								reload: true
							return
						)
				error = (result) ->
					console.log "There was an error while disable testing of workflow version"
			)

	exportAction = (e) ->
		versionId = WorkflowService.getWorkflowVersion().id
		if versionId? and versionId isnt ''
			$window.location = SERVICES._PREFIX + SERVICES.WORKFLOW_VERSION + "/" + versionId + SERVICES.WORKFLOW_VERSION_EXPORT + "?ticket=" + encodeURIComponent($scope.ticket)
			return

	importAction = (e) ->
		WindowService.openWindow("import_version", "views/workflow/importVersion.html")

	$scope.cancel = (e) ->
		if $stateParams.versionId?
			$state.go "resources.workflow.version",
				{resourceId: $stateParams.resourceId, versionId: $stateParams.versionId},
				reload: true
		else
			$state.go "resources.workflow",
				{resourceId: $stateParams.resourceId},
				reload: true
		return

	$scope.runWorkflow = (e) ->
		versionId = WorkflowService.getWorkflowVersion().id
		if versionId? and versionId isnt ''
			#RuntimeHttpService.runWorkflowInstance(versionId).then(
			RuntimeHttpService.runWorkflowInstanceWithParameters(versionId, executeParameters).then(
				success = (result) ->
					console.log "The process instance id: " + result.data
					NotificationSrv.add(
						title: "workflow_ui.workflow_version_executed_title"
						content: "workflow_ui.workflow_version_executed_content"
						content_data:
							workflow_instance: result.data
						bubble:
							show: true
					)
					$state.go "runtime", {projectId: $stateParams.projectId}
					return
				error = (result) ->
					console.log "There was an error during the execution of the workflow version"
			)
])

workflowManagerApp.controller('WorkflowCreateCtrl',['$scope', '$http', '$location', '$state', '$stateParams', '$q', 'WorkflowHttpService', 'QFormValidation', 'ProjectHttpService', '$window', ($scope, $http, $location, $state, $stateParams, $q, WorkflowHttpService, QFormValidation, ProjectHttpService, $window) ->
	NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')
	$scope.workflow =
		active: true

	categories = []

	projectDeferred = ProjectHttpService.getCategoriesByProjectId($stateParams.projectId)

	$q.all([projectDeferred]).then(
		success = (results) ->
			Array.prototype.push.apply(categories, results[0].data)
			return
	)

	$scope.workflowCategories = {
		dataSource: new kendo.data.DataSource(
			data: categories
		),
		dataTextField:"name",
		dataValueField:"id"
	}

	$scope.save = () ->
		$scope.workflow.projectId = $state.params.projectId
		WorkflowHttpService.create($scope.workflow).then(
			success = (result) ->
				$stateParams.resourceId = result.data
				NotificationSrv.add(
					title: "workflow_ui.workflow_created_title"
					content: "workflow_ui.workflow_created_content"
					content_data:
						workflow: $scope.workflow.name
					bubble:
						show: true
				)
				$state.go("resources.workflow", {resourceId: result.data}, reload: true)
			error = (result) ->
				console.log "error in workflow creation"
				QFormValidation.renderFormErrors($scope, $scope.newWorkflowForm, result)
		)

	$scope.cancel = (e) ->
		$state.go("resources", {projectId: $state.params.projectId})
])

workflowManagerApp.controller('WorkflowDeleteCtrl', ['$scope', '$state', '$stateParams', 'WorkflowHttpService', 'WindowService', '$window', \
				($scope, $state, $stateParams, WorkflowHttpService, WindowService, $window) ->

	NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

	$scope.deleteWorkflow = () ->
		WorkflowHttpService.delete($stateParams.resourceId).then(
			success = (result) ->
				WindowService.closeWindow()
				NotificationSrv.add(
					title: "workflow_ui.workflow_deleted_title"
					content: "workflow_ui.workflow_deleted_content"
					bubble:
						show: true
				)
				$state.go("resources", {projectId: $stateParams.projectId}, reload: true)
			error = (result) ->
				console.log "error in delete of workflow..."
		)

	$scope.cancel = () ->
		WindowService.closeWindow()
])

workflowManagerApp.controller('WorkflowImportVersionCtrl', ['$scope', '$state', '$stateParams', 'WorkflowHttpService', 'WindowService', 'SecuritySrv', '$window', \
				($scope, $state, $stateParams, WorkflowHttpService, WindowService, SecuritySrv, $window) ->

	NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')
	$scope.getTicket = () ->
		JSON.stringify(SecuritySrv.getUser().ticket)

	$scope.attachment = {}
	$scope.importVersion = {}
	workflowId = $stateParams.resourceId

	$scope.save = () ->
		if $scope.attachment.flow.files[0]?
			$scope.importVersion.contentVersion = $scope.attachment.flow.files[0].uniqueIdentifier
		else
			$scope.importVersion.contentVersion = null
		WorkflowHttpService.importWorkflowVersion(workflowId, $scope.importVersion).then(
			success = (result) ->
				WindowService.closeWindow()
				NotificationSrv.add(
					title: "workflow_ui.workflow_import_version_title"
					content: "workflow_ui.workflow_import_version_content"
					bubble:
						show: true
				)
				$state.go("resources.workflow", {resourceId: workflowId}, reload: true)
			error = (result) ->
				console.log "error in importing version..."
		)

	$scope.cancel = () ->
		WindowService.closeWindow()
])
