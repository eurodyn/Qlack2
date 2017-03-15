workflowManagerApp = angular.module("workflowApp")

workflowManagerApp.controller('WorkflowRuntimeCtrl',['$scope', '$state', '$stateParams', '$q', 'RuntimeService', 'RuntimeHttpService', 'QDateSrv', '$window', ($scope, $state, $stateParams, $q, RuntimeService, RuntimeHttpService, QDateSrv, $window) ->

	NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')
	
	workflowInstancesDeferred = RuntimeService.getWorkflowInstancesByProjectId($stateParams.projectId)
	
	workflowInstancesDeferred.then(
		success = (result) ->
			workflowInstances = RuntimeService.getWorkflowInstances()
			setGridData(workflowInstances)
			return
		)
	
	workflowInstances = []	
	$scope.workflowInstancesGrid = {
		dataSource: new kendo.data.DataSource(
			data: workflowInstances,
			schema:
				model:
					id: "id",
					fields:
						processInstanceId: { },
						workflowName: { },
						versionName: { },
						processId: { },
						startDate: { },
						endDate: { }
						statusDesc: { }
		),
		toolbar: [{
        	name: "refresh", template: "<button  class='k-button k-button-icontext' data-ng-click='refreshTableOfWorkflowInstances($event)'><span class='k-icon k-i-refresh'></span><span translate>refresh</span></button>", width: 50
        }],							
		sortable: true,
		selectable: false,
		editable: false,
		filterable: true 
	}
	
	$scope.setGridColumns = () ->
		columns = [
			{
				field: "processInstanceId", 
				headerTemplate: "<span translate>processInstanceId</span>",
				title: "Process Instance Id", 
				width: 40
			},
			{ 
				field: "workflowName",
				headerTemplate: "<span translate>workflow</span>", 
				title: "Workflow", 
				width: 40
			},
			{
				field: "versionName",
				headerTemplate: "<span translate>versionName</span>",  
				title: "Version", 
				width: 40
			},
			{
				field: "processId", 
				headerTemplate: "<span translate>processId</span>", 
				width: 40
			},
			{
				field: "startDate", 
				headerTemplate: "<span translate>startDate</span>", 
				template: "<span data-field-name='dataItem.startDate'>{{ dataItem.startDate | qDate:'lll' }}</span>", 
				width: 70
			},
			{
				field: "endDate", 
				headerTemplate: "<span translate>endDate</span>", 
				template: "<span data-field-name='dataItem.endDate' ng-show='{{ dataItem.endDate}} != 0'>{{ dataItem.endDate | qDate:'lll' }}</span>", 
				width: 70
			},
			{field: "statusDesc", headerTemplate: "<span translate>status</span>", width: 30},
		]
		columns.push(
			{
				command: [
					{
						name: "delete",
						template: "<button title='Delete' data-ng-click='removeWorkflowInstance($event)' class='k-button' ng-disabled='{{ dataItem.cantDelete }}'><span class='k-icon k-delete'></span></button>"
					},
					{
						name: "stop",
						template: "<button title='Stop' data-ng-click='stopWorkflowInstance($event)' class='k-button' ng-disabled='{{ dataItem.cantStop }}'><span class='k-icon k-warning'></span></button>"
					}
					#suspend & resume not supported by jBPM6
					#{
					#	name: "pause",
					#	template: "<button data-ng-click='suspendWorkflowInstance($event)' class='k-button' ng-disabled='{{ dataItem.cantPause }}'><span class='k-icon k-i-folder-up'></span></button>"
					#},
					#{
					#	name: "resume",
					#	template: "<button data-ng-click='resumeWorkflowInstance($event)' class='k-button' ng-disabled='{{ dataItem.cantResume }}'><span class='k-icon k-i-folder-add'></span></button>"
					#}
				],
				width: 40
			}
		)
		columns
		
	setGridData = (data) ->
		workflowInstances.pop() while workflowInstances.length > 0
		Array.prototype.push.apply(workflowInstances, data)
		$scope.workflowInstancesGrid.dataSource.read()
		
	$scope.removeWorkflowInstance = (e) ->
		console.log "removeWorkflowInstance"
		e.preventDefault()
		dataItem = angular.element("#instancesGrid").data("kendoGrid").dataItem(angular.element(e.target).closest("tr"))
		if dataItem?
			versionId = dataItem.versionId
			processInstanceId = dataItem.processInstanceId
			RuntimeHttpService.deleteWorkflowInstance(versionId, processInstanceId).then(
					success = (result) ->
						NotificationSrv.add(
							title: "workflow_ui.workflow_instance_deleted_title"
							content: "workflow_ui.workflow_instance_deleted_content"
							content_data: 
								workflow_instance: processInstanceId
							bubble:
								show: true
						)
						$scope.refreshTableOfWorkflowInstances()
						return
					error = (result) ->
						console.log "There was an error while deleting workflow instance"
				)
		
	$scope.suspendWorkflowInstance = (e) ->
		console.log "suspendWorkflowInstance"
		e.preventDefault()
		dataItem = angular.element("#instancesGrid").data("kendoGrid").dataItem(angular.element(e.target).closest("tr"))
		if dataItem?
			RuntimeHttpService.suspendWorkflowInstance(dataItem.versionId, dataItem.processInstanceId).then(
					success = (result) ->
						NotificationSrv.add(
							title: "workflow_ui.workflow_instance_suspended_title"
							content: "workflow_ui.workflow_instance_suspended_content"
							content_data: 
								workflow_instance: dataItem.processInstanceId
							bubble:
								show: true
						)
						$state.go "runtime", {projectId: $stateParams.projectId}
						return
					error = (result) ->
						console.log "There was an error while suspending workflow instance"
				)
	
	$scope.resumeWorkflowInstance = (e) ->
		console.log "resumeWorkflowInstance"
		e.preventDefault()
		dataItem = angular.element("#instancesGrid").data("kendoGrid").dataItem(angular.element(e.target).closest("tr"))
		if dataItem?
			RuntimeHttpService.resumeWorkflowInstance(dataItem.versionId, dataItem.processInstanceId).then(
					success = (result) ->
						NotificationSrv.add(
							title: "workflow_ui.workflow_instance_resumed_title"
							content: "workflow_ui.workflow_instance_resumed_content"
							content_data: 
								workflow_instance: dataItem.processInstanceId
							bubble:
								show: true
						)
						$state.go "runtime", {projectId: $stateParams.projectId}
						return
					error = (result) ->
						console.log "There was an error while resuming workflow instance"
				)
		
	$scope.stopWorkflowInstance = (e) ->
		console.log "stopWorkflowInstance"
		e.preventDefault()
		dataItem = angular.element("#instancesGrid").data("kendoGrid").dataItem(angular.element(e.target).closest("tr"))
		if dataItem?
			RuntimeHttpService.stopWorkflowInstance(dataItem.versionId, dataItem.processInstanceId).then(
					success = (result) ->
						NotificationSrv.add(
							title: "workflow_ui.workflow_instance_stopped_title"
							content: "workflow_ui.workflow_instance_stopped_content"
							content_data: 
								workflow_instance: dataItem.processInstanceId
							bubble:
								show: true
						)
						$state.go "runtime", {projectId: $stateParams.projectId}
						return
					error = (result) ->
						console.log "There was an error while stopping workflow instance"
				)
	
	$scope.refreshTableOfWorkflowInstances = (e) ->
		console.log "refreshTableOfWorkflowInstances"
		RuntimeService.getWorkflowInstancesByProjectId($stateParams.projectId).then(
			success = (result) ->
				setGridData(RuntimeService.getWorkflowInstances())
				return
		)
		
])

workflowManagerApp.controller('WorkflowRuntimeErrorCtrl',['$scope', '$state', '$stateParams', '$q', 'RuntimeService', 'RuntimeHttpService', 'QDateSrv', '$window', ($scope, $state, $stateParams, $q, RuntimeService, RuntimeHttpService, QDateSrv, $window) ->

	workflowErrorLogsDeferred = RuntimeService.getWorkflowErrorAuditLogsByProjectId($stateParams.projectId)
	
	workflowErrorLogsDeferred.then(
		success = (result) ->
			workflowErrorLogs = RuntimeService.getWorkflowErrorLogs()
			setGridData(workflowErrorLogs)
			return
		)
	
	workflowErrorLogs = []	
	$scope.workflowErrorLogsGrid = {
		dataSource: new kendo.data.DataSource(
			data: workflowErrorLogs,
			schema:
				model:
					id: "id",
					fields:
						workflowName: { },
						versionName: { },
						processId: { },
						logDate: { },
						processInstanceId: { },
						traceData: { }
		),
		toolbar: [{
        	name: "refresh", template: "<button  class='k-button k-button-icontext' data-ng-click='refreshTableOfWorkflowErrorLogs($event)'><span class='k-icon k-i-refresh'></span><span translate>refresh</span></button>", width: 50
        }],							
		sortable: true,
		selectable: false,
		editable: false,
		filterable: true,
		detailTemplate: kendo.template($("#detailsTemplate").html()) 
	}
	
	$scope.setGridColumns = () ->
		columns = [
			{ 
				field: "workflowName",
				headerTemplate: "<span translate>workflow</span>", 
				title: "Workflow", 
				width: 40
			},
			{
				field: "versionName",
				headerTemplate: "<span translate>versionName</span>",  
				title: "Version", 
				width: 40
			},
			{
				field: "processId", 
				headerTemplate: "<span translate>processId</span>", 
				width: 40
			},
			{
				field: "logDate", 
				headerTemplate: "<span translate>startDate</span>", 
				template: "<span data-field-name='dataItem.logDate'>{{ dataItem.logDate | qDate:'lll' }}</span>", 
				width: 70
			},
			{
				field: "processInstanceId", 
				headerTemplate: "<span translate>processInstanceId</span>",
				title: "Process Instance Id", 
				width: 40
			}
		]
		
	setGridData = (data) ->
		workflowErrorLogs.pop() while workflowErrorLogs.length > 0
		Array.prototype.push.apply(workflowErrorLogs, data)
		$scope.workflowErrorLogsGrid.dataSource.read()
	
	$scope.refreshTableOfWorkflowErrorLogs = (e) ->
		console.log "refreshTableOfWorkflowErrorLogs"
		RuntimeService.getWorkflowErrorAuditLogsByProjectId($stateParams.projectId).then(
			success = (result) ->
				setGridData(RuntimeService.getWorkflowErrorLogs())
				return
		)
		
])