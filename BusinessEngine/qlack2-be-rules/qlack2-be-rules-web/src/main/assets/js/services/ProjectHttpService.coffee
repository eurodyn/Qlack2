angular.module("rules")
	.service("ProjectHttpService", ["$http", "SERVICES", ($http, SERVICES) ->

		getAll: () ->
			$http.get(SERVICES._PREFIX + SERVICES.PROJECTS)

		getRecent: () ->
			$http.get(SERVICES._PREFIX + SERVICES.PROJECTS_RECENT,
				params:
					sort: SERVICES._PROJECTS_SORT
					order: SERVICES._PROJECTS_ORDER
					start: SERVICES._PROJECTS_START
					size: SERVICES._PROJECTS_SIZE
			)

		getTree: (projectId) ->
			$http.get(SERVICES._PREFIX + SERVICES.PROJECTS + "/" + projectId + SERVICES.RESOURCES)
			
		getTreeForAllProjects: () ->
			$http.get(SERVICES._PREFIX + SERVICES.PROJECTS + SERVICES.RESOURCES)
	])
