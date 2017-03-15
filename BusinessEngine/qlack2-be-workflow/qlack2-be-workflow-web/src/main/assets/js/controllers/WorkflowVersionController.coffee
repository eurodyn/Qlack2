workflowManagerApp = angular.module("workflowApp")

workflowManagerApp.controller('WorkflowVersionCtrl',['$scope', '$compile', '$state', '$stateParams', '$q', 'WorkflowHttpService', 'WorkflowVersionHttpService', 'ProjectHttpService', 'QFormValidation', 'UtilService', 'WindowService', 'QDateSrv', 'WorkflowService', '$window', ($scope, $compile, $state, $stateParams, $q, WorkflowHttpService, WorkflowVersionHttpService, ProjectHttpService, QFormValidation, UtilService, WindowService, QDateSrv, WorkflowService, $window) ->

	NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

	workingSetsMap = []
	rulesMap = []
	conditionTypesMap = []

	$scope.conditionsHaveError = false

	conditionTypesPromise = WorkflowVersionHttpService.getConditionTypes()
	conditionTypesPromise.then(
		success = (result) ->
			conditionTypes = []
			for conditionTypeNum in result.data
				conditionType = {}
				conditionType.id = conditionTypeNum.toString()
				conditionType.name = "condition_type_" + conditionTypeNum
				conditionTypes.push(conditionType)
			$scope.conditionTypeOptions.dataSource.data(conditionTypes)
			conditionTypesMap[id] = name for {id, name} in conditionTypes
		error = () ->
			console.log "Error while getting condition types..." + result
			[]
	)

	workingSetsPromise = ProjectHttpService.getProjectWorkingSets($state.params.projectId)
	workingSetsPromise.then(
		success = (result) ->
			$scope.workingSetsOptions.dataSource.data(result.data)
			workingSetsMap[id] = (workingSetName + " / " + name) for {id, name, workingSetName} in result.data
		error = () ->
			console.log "Error while getting working sets..." + result
			[]
	)

	rulesPromise = ProjectHttpService.getWorkingSetRules($state.params.projectId)
	rulesPromise.then(
		success = (result) ->
			$scope.rulesOptions.dataSource.data(result.data)
			rulesMap[id] = (ruleName + " / " + name) for {id, name, ruleName} in result.data
		error = () ->
			console.log "Error while getting rules..." + result
			[]
	)

	$q.all([conditionTypesPromise, workingSetsPromise, rulesPromise]).then((results) ->
		#Ensure that the workflow is loaded if this state is accessed directly
		WorkflowService.getWorkflowById($stateParams.resourceId, $state.params.projectId, false).then(
			success = (result) ->
				$scope.workflowVersions.dataSource.data(WorkflowService.getWorkflow().versions)
				WorkflowService.getWorkflowVersionById($stateParams.versionId).then(
					success = (result) ->
						WorkflowService.setActions()

						$scope.workflowVersion = WorkflowService.getWorkflowVersion()
						$scope.workflowSelectedVersionId = $scope.workflowVersion.id
						$scope.workflowVersion.lockedOnDate = QDateSrv.localise($scope.workflowVersion.lockedOn, 'lll')

						# set 'id' field for new rows so that kendo does not remove them on edit->cancel
						$scope.workflowVersion.conditions.forEach((item) ->
							item.kendoId = UtilService.createUUID()
							item.conditionId = item.id
						)

						setVersionConditions($scope.workflowVersion.conditions)

						setParentOptions($scope.workflowVersion.conditions)

						$scope.canManageWorkflow = WorkflowService.getCanManageWorkflow()
						$scope.canEditVersion = WorkflowService.getCanEditVersion()
						$scope.canExecuteVersion = WorkflowService.getCanExecuteVersion()
						$scope.rebindVal = WorkflowService.getRebindVal()
						return
				)
		)
	)

	$scope.conditionTypeOptions = {
		dataSource: new kendo.data.DataSource(
			data: []
		),
		dataTextField:"name",
		dataValueField:"id"
	}

	$scope.workingSetsOptions = {
		dataSource: new kendo.data.DataSource(
			data: []
		),
		dataValueField:"id",
		template: "<span> #if (data.id != null) {# #=data.workingSetName# / #=data.name# #} else {# #=data# #}# </span>",
		valueTemplate: "<span> #if (data.id != null) {# #=data.workingSetName# / #=data.name# #} else {# #=data# #}# </span>"
	}

	$scope.rulesOptions = {
		dataSource: new kendo.data.DataSource(
			data: []
		),
		cascadeFrom: "workingSetsDropDownListId",
		cascadeFromField: "workingSetId",
		dataValueField: "id",
		template: "<span> #if (data.id != null) {# #=data.ruleName# / #=data.name# #} else {# #=data# #}# </span>",
		valueTemplate: "<span> #if (data.id != null) {# #=data.ruleName# / #=data.name# #} else {# #=data# #}# </span>"
	}

	$scope.workflowVersions = {
		dataSource: new kendo.data.DataSource(
			data: []
		),
		dataTextField:"name",
		dataValueField:"id",
		template: kendo.template($("#versionsListTemplate").html()),
		change: (e) ->
			selectWorkflowVersion(e.sender.value())
	}

	$scope.parentOptions = {
		dataSource: new kendo.data.DataSource(
			data: [],
			schema:
				model:
					id: "id",
					fields:
						id: { type: "string" }
						name: { type: "string" }
						conditionType: { type: "number" }
		),
		dataTextField: "name",
		dataValueField: "id"
	}

	$scope.setConditionsToolbar = () ->
		toolbar = []
		if $scope.canManageWorkflow && $scope.canEditVersion
			toolbar.push(
				{
					name: "create",
					template: '<button class=\'k-button k-button-icontext k-grid-add\'><span class=\'k-icon k-add\'></span><span translate>add_condition</span></button>'
				}
			)

		toolbar

	conditions = []
	$scope.workflowVersionConditions = {
		dataSource: new kendo.data.DataSource(
			data: conditions,
			schema:
				model:
					id: "kendoId",
					fields:
						kendoId: { editable: false, nullable: false }
						conditionId: { editable: false, nullable: false }
						name: { }
						conditionType: { }
						workingSetId: { }
						ruleId: { }
						parentCondition: { defaultValue: null }
		)
		sortable: true,
		resizable: true,
		selectable: true,
		editable:
			mode: "inline"

		edit: (e) ->
			console.log "Grid -> edit"

			$scope.filterParentOptionsDataSource(e)

		save: (e) ->
			console.log "Grid -> save"
			selectedData = e.sender.dataItem(e.sender.select())

			# set 'id' field for new rows so that kendo does not remove them on edit->cancel
			if (!selectedData.kendoId)
				selectedData.kendoId = UtilService.createUUID()

			if !selectedData.conditionId
				selectedData.conditionId = UtilService.createUUID()

			setParentOptions($scope.workflowVersionConditions.dataSource.data())

			#Update the version conditions
			$scope.workflowVersion = WorkflowService.getWorkflowVersion()
			$scope.workflowVersion.conditions = $scope.workflowVersionConditions.dataSource.data()

		cancel: (e) ->
			console.log "Grid -> cancel"

		remove: (e) ->
			console.log "Grid -> remove"

			setParentOptions($scope.workflowVersionConditions.dataSource.data())

			#Update the version conditions
			$scope.workflowVersion = WorkflowService.getWorkflowVersion()
			$scope.workflowVersion.conditions = $scope.workflowVersionConditions.dataSource.data()
	}

	$scope.setConditionsColumns = () ->
		columns = [
			{
				field: "name",
				headerTemplate: "<span translate>condition_name</span>",
				template: "<span data-field-name='dataItem.name'>#=name#</span>",
				width: 80
			},
			{
				field: "conditionType",
				headerTemplate: "<span translate>condition_type</span>",
				width: 60,
				template: "<span data-field-name='dataItem.conditionType' translate>condition_type_#=conditionType#</span>",
				editor: (container, options) ->
					html = "<select kendo-drop-down-list " +
								   "k-options='conditionTypeOptions' " +
								   "k-data-text-field=\"'name'\" " +
								   "k-data-value-field=\"'id'\" " +
								   "k-template=\"'<span translate>#=name#</span>'\" " +
								   "k-value-template=\"'<span translate>#=name#</span>'\" " +
								   "k-option-label=\"'{{'select_condition_type' | translate}}'\" " +
								   "k-on-change='filterParentOptionsDataSource(kendoEvent)' " +
								   "data-bind='value:" +  options.field + "'>" +
							"</select>"

					$(html).appendTo(container)
			},
			{
				field: "workingSetId",
				headerTemplate: "<span translate>condition_working_set</span>",
				width: 60,
				template: "<span data-field-name='dataItem.workingSetId'>{{ getWorkingSetName(dataItem.workingSetId) }}</span>",
				editor: (container, options) ->
					html = "<select id='workingSetsDropDownListId' " +
								   "kendo-drop-down-list='workingSetsDropDownList' " +
								   "k-options='workingSetsOptions' " +
								   "k-option-label=\"'{{'select_working_set' | translate}}'\" " +
								   "data-bind='value:" +  options.field + "'>" +
							"</select>"

					$(html).appendTo(container)
			},
			{
				field: "ruleId",
				headerTemplate: "<span translate>condition_rule</span>",
				width: 60,
				template: "<span data-field-name='dataItem.ruleId'>{{ getRuleName(dataItem.ruleId) }}</span>",
				editor: (container, options) ->
					html = "<select kendo-drop-down-list " +
								   "k-options='rulesOptions' " +
								   "k-option-label=\"'{{'select_rule' | translate}}'\" " +
								   "data-bind='value:" +  options.field + "'>" +
							"</select>"

					$(html).appendTo(container)
			},
			{
				field: "parentCondition",
				headerTemplate: "<span translate>condition_parent</span>",
				width: 80,
				template: "<span data-field-name='dataItem.parentCondition'>{{ getParentName(dataItem.parentCondition) }}</span>"
				editor: (container, options) ->
					html = "<select kendo-drop-down-list " +
								   "k-options='parentOptions' " +
								   "k-option-label=\"'{{'select_parent' | translate}}'\" " +
								   "data-bind='value:" +  options.field + "'>" +
							"</select>"

					$(html).appendTo(container)
			}
		]

		if $scope.canManageWorkflow && $scope.canEditVersion
			columns.push(
				{
					command: [
						{ name: "edit", text: { edit: "", cancel: "", update: "" } },
						{ name: "destroy", text: "" }
					],
					width: 60
				}
			)

		columns

	$scope.filterParentOptionsDataSource = (e) ->
		senderItem = e.sender

		selectedDataItem = null
		selectedConditionType = null

		if senderItem instanceof kendo.ui.Grid
			selectedDataItem = e.model
			if selectedDataItem? and selectedDataItem.conditionType isnt ''
				selectedConditionType = selectedDataItem.conditionType
		else if senderItem instanceof kendo.ui.DropDownList
			if senderItem.value()? and senderItem.value() isnt ''
				selectedConditionType = parseInt(senderItem.value())

		if selectedConditionType?
			if selectedDataItem?
				$scope.parentOptions.dataSource.filter(
					{
						logic: "and"
						filters: [
							{ field: "conditionType", operator: "eq", value: selectedConditionType },
							{ field: "id", operator: "neq", value: selectedDataItem.conditionId }
						]
					},
					{ field: "name", operator: "neq", value: selectedDataItem.name }
				)
			else
				$scope.parentOptions.dataSource.filter({ field: "conditionType", operator: "eq", value: selectedConditionType })
		else
			$scope.parentOptions.dataSource.filter({ field: "conditionType", operator: "gte", value: 0 })

	#The ui-ace option
	$scope.aceOption = {
		useWrapMode: true,
		showGutter: false,
		mode: 'xml'
	}

	setParentOptions = (versionConditions) ->
		parentConditions = []
		for condition in versionConditions
			parentCondition = {}
			parentCondition.id = condition.conditionId
			parentCondition.name = condition.name
			parentCondition.conditionType = condition.conditionType
			parentConditions.push(parentCondition)

		$scope.parentOptions.dataSource.data(parentConditions)

	setVersionConditions = (versionConditions) ->
		conditions.pop() while conditions.length > 0
		Array.prototype.push.apply(conditions, versionConditions)
		$scope.workflowVersionConditions.dataSource.read()

	$scope.getWorkingSetName = (workingSetId) ->
		if workingSetsMap?
			workingSetName = workingSetsMap[workingSetId]
		workingSetName

	$scope.getRuleName = (ruleId) ->
		if rulesMap?
			ruleName = rulesMap[ruleId]
		ruleName

	$scope.getConditionTypeName = (conditionType) ->
		if conditionTypesMap?
			conditionTypeName = conditionTypesMap[conditionType]
		conditionTypeName

	$scope.getParentName = (parent) ->
		if parent?
			parentName = parent.name
		parentName

	$scope.$on 'VALIDATION_ERROR_versionDetails.versionConditions', (event, data) ->
		$scope.conditionsHaveError = true
		uid = $scope.workflowVersionConditions.dataSource.at(data.fieldIndex)['uid']
		cell = $('tr[data-uid="' + uid + '"] td span[data-field-name="dataItem.' + data.propertyName + '"]').parent()
		existingErrors = cell.find('.form-control-feedback').remove()
		error = "<i popover='" + data.translation + "' popover-placement='left' popover-trigger='mouseenter' class='fa fa-exclamation-triangle form-control-feedback pull-right'></i>"
		cell.prepend($compile(error)($scope))
		$('#conditionsTab a').addClass('has-error')
		return

	#when the user selects a version in the dropdown list, this function is called
	selectWorkflowVersion = (id) ->
		console.log "Selected workflowSelectedVersionId " + id
		$state.go "resources.workflow.version",
			resourceId: $stateParams.resourceId
			versionId: id

])

workflowManagerApp.controller('WorkflowVersionCreateCtrl',['$scope', '$state', '$stateParams', 'WorkflowHttpService', 'QFormValidation', 'WorkflowService', 'WindowService', '$window', ($scope, $state, $stateParams, WorkflowHttpService, QFormValidation, WorkflowService, WindowService, $window) ->

	NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')
	workflowId = $stateParams.resourceId

	$scope.basedOnOptions  = {
		dataSource: WorkflowService.getWorkflow().versions,
		dataTextField: "name",
		dataValueField: "id",
		template: kendo.template($("#basedOnListTemplate").html())
	}

	$scope.createWorkflowVersion = () ->
		if $scope.basedOnDropDownList.value()? and $scope.basedOnDropDownList.value() isnt ''
			$scope.newWorkflowVersion.basedOn = $scope.basedOnDropDownList.value()
		else
			$scope.newWorkflowVersion.basedOn = null
		WorkflowHttpService.createWorkflowVersion(workflowId, $scope.newWorkflowVersion).then(
			success = (result) ->
				console.log "creation of workflow version success"
				versionId = result.data
				WindowService.closeWindow()
				NotificationSrv.add(
					title: "workflow_ui.workflow_version_created_title"
					content: "workflow_ui.workflow_version_created_content"
					content_data:
						workflow_version: $scope.newWorkflowVersion.name
					bubble:
						show: true
				)
				WorkflowService.getWorkflowVersionsByWorkflowId($stateParams.resourceId).then(
					success = (result) ->
						$state.go "resources.workflow.version",
							{ resourceId: $stateParams.resourceId, versionId: versionId },
							reload: true
						return
				)
			error = (result) ->
				console.log "creation of workflow version error"
				QFormValidation.renderFormErrors($scope, $scope.workflowVersionForm, result)
		)

	$scope.cancel = () ->
		WindowService.closeWindow()
])

workflowManagerApp.controller('WorkflowVersionFinalizeCtrl', ['$scope', '$state', '$stateParams', 'WorkflowVersionHttpService', 'WorkflowService', 'WindowService', '$window', \
				($scope, $state, $stateParams, WorkflowVersionHttpService, WorkflowService, WindowService, $window) ->

	NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

	$scope.finaliseWorkflowVersion = () ->
		WorkflowVersionHttpService.finalise($stateParams.versionId).then(
			success = (result) ->
				WindowService.closeWindow()
				NotificationSrv.add(
					title: "workflow_ui.workflow_version_finalised_title"
					content: "workflow_ui.workflow_version_finalised_content"
					bubble:
						show: true
				)
				WorkflowService.getWorkflowVersionsByWorkflowId($stateParams.resourceId).then(
						success = (result) ->
							$state.go "resources.workflow.version",
								{ resourceId: $stateParams.resourceId, versionId: $stateParams.versionId },
								reload: true
							return
					)
			error = (result) ->
				WindowService.closeWindow()
				console.log "There was an error while finalising the workflow version"
		)

	$scope.cancel = () ->
		WindowService.closeWindow()
])

workflowManagerApp.controller('WorkflowVersionDeleteCtrl', ['$scope', '$state', '$stateParams', 'WorkflowVersionHttpService', 'WindowService', '$window', \
				($scope, $state, $stateParams, WorkflowVersionHttpService, WindowService, $window) ->

	NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

	$scope.deleteWorkflowVersion = () ->
		WorkflowVersionHttpService.delete($stateParams.versionId).then(
			success = (result) ->
				WindowService.closeWindow()
				NotificationSrv.add(
					title: "workflow_ui.workflow_version_deleted_title"
					content: "workflow_ui.workflow_version_deleted_content"
					bubble:
						show: true
				)
				$state.go "resources.workflow",
						{ projectId: $stateParams.projectId, resourceId: $stateParams.resourceId },
						reload: true
			error = (result) ->
				WindowService.closeWindow()
				console.log "There was an error while deleting the workflow version"
		)

	$scope.cancel = () ->
		WindowService.closeWindow()
])
