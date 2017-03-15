workflowManagerApp = angular.module("workflowApp")

workflowManagerApp.service "WorkflowHttpService", ["$http", "SERVICES", ($http, SERVICES) ->
		getWorkflow: (myObj) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.WORKFLOWS + "/" + myObj
				
		getById: (workflowId) ->
			$http.get(SERVICES._PREFIX + SERVICES.WORKFLOWS + workflowId)
			.then(
				(result) ->
					result.data
				,
				(result) ->
					throw new Error("Cannot fetch workflow")
			)
		
		create: (workflowObj) ->
			$http
				method: "POST"
				url: SERVICES._PREFIX + SERVICES.WORKFLOWS
				data: workflowObj
		
		update: (workflowId, workflow) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.WORKFLOWS + "/" + workflowId
				data: workflow
					
		delete: (workflowId) ->
			$http
				method: "DELETE"
				url: SERVICES._PREFIX + SERVICES.WORKFLOWS + "/" + workflowId
		
		getWorkflowVersions: (workflowId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.WORKFLOWS + "/" + workflowId + SERVICES.WORKFLOW_VERSIONS
			
		countWorkflowVersionsLockedByOtherUser: (workflowId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.WORKFLOWS + "/" + workflowId + SERVICES.WORKFLOW_VERSIONS_LOCKED_COUNT
		
		createWorkflowVersion: (workflowId, workflowVersionObj) ->
			$http
				method: "POST"
				url: SERVICES._PREFIX + SERVICES.WORKFLOWS + "/" + workflowId + SERVICES.WORKFLOW_VERSIONS
				data: workflowVersionObj
		
		importWorkflowVersion: (workflowId, importObj) ->
			$http
				method: "POST"
				url: SERVICES._PREFIX + SERVICES.WORKFLOWS + "/" + workflowId + SERVICES.WORKFLOW_VERSION_IMPORT
				data: importObj
	]