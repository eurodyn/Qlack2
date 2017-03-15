workflowManagerApp = angular.module("workflowApp")

workflowManagerApp.service "ProjectHttpService", ["$http", "SERVICES", ($http, SERVICES) ->
		
		getProjects: () ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.PROJECTS

		getRecentProjects: () ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.RECENT_PROJECTS
				params: {
					sort: SERVICES._SORT_RECENT_PROJECTS,
					order: SERVICES._ORDER_RECENT_PROJECTS,
					start: SERVICES._START_RECENT_PROJECTS,
					size: SERVICES._SIZE_RECENT_PROJECTS
				}

		getProject: (projectId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.PROJECTS + "/" + projectId

		updateRecentProject: (projectId) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.RECENT_PROJECTS + "/" + projectId

		getProjectTreeResources: (projectId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.PROJECTS + "/" + projectId + SERVICES.CATEGORIES_WORKFLOWS_TREE
				
		getWorkflowResourcesAsTreeForAllProjects: ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.PROJECTS + SERVICES.CATEGORIES_WORKFLOWS_TREE
	
		getCategoriesByProjectId: (projectId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.PROJECTS + "/" + projectId + SERVICES.CATEGORIES
		
		getProjectWorkingSets: (projectId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.PROJECTS + "/" + projectId	+ SERVICES.WORKING_SETS

		getWorkingSetRules: (projectId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.PROJECTS + "/" + projectId	+ SERVICES.RULES
				#params:
				#	workingSetId: workingSetId
	]