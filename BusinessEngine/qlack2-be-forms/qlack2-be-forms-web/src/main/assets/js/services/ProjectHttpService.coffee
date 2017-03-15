angular
	.module("formsManagerApp")
	.service "ProjectHttpService", ["$http", "SERVICES", ($http, SERVICES) ->
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
				
		getFormResourcesAsTreeForAllProjects: ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.PROJECTS + SERVICES.FORM_RESOURCES

		getFormResourcesAsTree: (projectId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.PROJECTS + "/" + projectId + SERVICES.FORM_RESOURCES

		getProjectCategories: (projectId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.PROJECTS + "/" + projectId + SERVICES.CATEGORY

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

		getLanguages: () ->
			$http
				method: "GET"
				url: SERVICES._WD_PREFIX + SERVICES.LANGUAGES
	]