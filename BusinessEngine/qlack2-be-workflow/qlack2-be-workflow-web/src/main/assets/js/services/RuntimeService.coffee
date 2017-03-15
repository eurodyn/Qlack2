workflowManagerApp = angular.module("workflowApp")

workflowManagerApp.service "RuntimeService", ["$q", "RuntimeHttpService", "SecuritySrv", ($q, RuntimeHttpService, SecuritySrv) ->
		
		workflowInstances = []
		
		workflowErrorLogs = []
		
		canManageWorkflow = null
		canExecuteWorkflow = null

		getWorkflowInstances: () ->
			workflowInstances
			
		getWorkflowErrorLogs: () ->
			workflowErrorLogs

		getWorkflowInstancesByProjectId: (projectId) ->
			deferred = $q.defer()
			workflowInstances.pop() while workflowInstances.length > 0	
			# SECURITY PROMISES
			canExecuteWorkflowDeferred = SecuritySrv.resolvePermission('WFL_EXECUTE_WORKFLOW', projectId)
			canManageWorkflowDeferred = SecuritySrv.resolvePermission('WFL_MANAGE_WORKFLOW', projectId)
			$q.all([canExecuteWorkflowDeferred, canManageWorkflowDeferred]).then((results) ->
				canExecuteWorkflow = results[0]
				canManageWorkflow = results[1]
				RuntimeHttpService.getWorkflowInstances(projectId).then(
					success = (result) ->
						for myInstance in result.data
							myInstance.cantDelete = false
							myInstance.cantStop = true
							myInstance.cantPause = true
							myInstance.cantResume = true
							#active
							if canExecuteWorkflow && myInstance.status == 1 
								myInstance.cantStop = false
								myInstance.cantPause = false
							#suspend
							if canExecuteWorkflow && myInstance.status == 4 
								myInstance.cantStop = false
								myInstance.cantResume = false
							workflowInstances.push(myInstance)
						deferred.resolve(workflowInstances)
						return
					error = (result) ->
						deferred.resolve("error while fetching workflow instances")
						return
					)
			)
			deferred.promise
		
		getWorkflowErrorAuditLogsByProjectId: (projectId) ->
			deferred = $q.defer()
			workflowErrorLogs.pop() while workflowErrorLogs.length > 0	
			# SECURITY PROMISES
			canExecuteWorkflowDeferred = SecuritySrv.resolvePermission('WFL_EXECUTE_WORKFLOW', projectId)
			canManageWorkflowDeferred = SecuritySrv.resolvePermission('WFL_MANAGE_WORKFLOW', projectId)
			$q.all([canExecuteWorkflowDeferred, canManageWorkflowDeferred]).then((results) ->
				canExecuteWorkflow = results[0]
				canManageWorkflow = results[1]
				RuntimeHttpService.getWorkflowErrorAuditLogs(projectId).then(
					success = (result) ->
						for myLog in result.data
							workflowErrorLogs.push(myLog)
						deferred.resolve(workflowErrorLogs)
						return
					error = (result) ->
						deferred.resolve("error while fetching workflow error logs")
						return
					)
			)
			deferred.promise

		getCanManageWorkflow: () ->
			canManageWorkflow
	]