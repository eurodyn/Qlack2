angular.module("rules")
	.service("CategoryHttpService", ["$http", "SERVICES", ($http, SERVICES) ->

		getByProjectId: (projectId) ->
			$http.get(SERVICES._PREFIX + SERVICES.PROJECTS + "/" + projectId + SERVICES.CATEGORIES)

		getById: (categoryId) ->
			$http.get(SERVICES._PREFIX + SERVICES.CATEGORIES + "/" + categoryId)

		create: (projectId, category) ->
			category.projectId = projectId
			$http.post(SERVICES._PREFIX + SERVICES.CATEGORIES, category)

		update: (categoryId, category) ->
			$http.put(SERVICES._PREFIX + SERVICES.CATEGORIES + "/" + categoryId, category)

		deleteCategory: (categoryId) ->
			$http.delete(SERVICES._PREFIX + SERVICES.CATEGORIES + "/" + categoryId)

		canDeleteCategory: (categoryId) ->
			$http.get(SERVICES._PREFIX + SERVICES.CATEGORIES + "/" + categoryId + SERVICES.CAN_DELETE)

	])

