workflowManagerApp = angular.module("workflowApp")

workflowManagerApp.service "RuntimeHttpService", ["$http", "SERVICES", ($http, SERVICES) ->
		runWorkflowInstance: (versionId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.RUNTIME + "/" + versionId
		
		runWorkflowInstanceWithParameters: (versionId, parameters) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.RUNTIME + "/" + versionId
				data: parameters
				
		getWorkflowInstances: (projectId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.RUNTIME + "/" + projectId	+ SERVICES.WORKFLOW_INSTANCES
				
		stopWorkflowInstance: (versionId, processInstanceId) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.RUNTIME + "/" + versionId + SERVICES.STOP_WORKFLOW_INSTANCE + "/" + processInstanceId
				
		suspendWorkflowInstance: (versionId, processInstanceId) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.RUNTIME + "/" + versionId + SERVICES.SUSPEND_WORKFLOW_INSTANCE + "/" + processInstanceId
		
		resumeWorkflowInstance: (versionId, processInstanceId) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.RUNTIME + "/" + versionId + SERVICES.RESUME_WORKFLOW_INSTANCE + "/" + processInstanceId
		
		deleteWorkflowInstance: (versionId, processInstanceId) ->
			$http
				method: "DELETE"
				url: SERVICES._PREFIX + SERVICES.RUNTIME + "/" + versionId + "/" + processInstanceId
		
		getWorkflowErrorAuditLogs: (projectId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.RUNTIME + "/" + projectId	+ SERVICES.WORKFLOW_RUNTIME_AUDIT_LOGS
	]