workflowManagerApp = angular.module("workflowApp")

workflowManagerApp.service "WorkflowVersionHttpService", ["$http", "SERVICES", ($http, SERVICES) ->
		getWorkflowVersion: (myObj) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.WORKFLOW_VERSION + "/" + myObj
		
		update: (workflowVersionId, workflowVersion) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.WORKFLOW_VERSION + "/" + workflowVersionId
				data: workflowVersion
		
		delete: (workflowVersionId) ->
			$http
				method: "DELETE"
				url: SERVICES._PREFIX + SERVICES.WORKFLOW_VERSION + "/" + workflowVersionId		
		
		lock: (workflowVersionId) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.WORKFLOW_VERSION + "/" + workflowVersionId + SERVICES.WORKFLOW_VERSION_LOCK

		unlock: (workflowVersionId) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.WORKFLOW_VERSION + "/" + workflowVersionId + SERVICES.WORKFLOW_VERSION_UNLOCK

		finalise: (workflowVersionId) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.WORKFLOW_VERSION + "/" + workflowVersionId + SERVICES.WORKFLOW_VERSION_FINALISE
	
		enableTestingWorkflowVersion: (workflowVersionId) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.WORKFLOW_VERSION + "/" + workflowVersionId + SERVICES.WORKFLOW_VERSION_ENABLE_TESTING

		disableTestingWorkflowVersion: (workflowVersionId) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.WORKFLOW_VERSION + "/" + workflowVersionId + SERVICES.WORKFLOW_VERSION_DISABLE_TESTING
		
		getConditionTypes: () ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.WORKFLOW_VERSION + SERVICES.CONDITION_TYPES
		
		checkWorkflowVersionCanFinalise: (workflowVersionId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.WORKFLOW_VERSION + "/" + workflowVersionId + SERVICES.WORKFLOW_VERSION_CAN_FINALISE	
	]