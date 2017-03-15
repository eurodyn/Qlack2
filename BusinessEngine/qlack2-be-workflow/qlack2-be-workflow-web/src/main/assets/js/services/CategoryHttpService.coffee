workflowManagerApp = angular.module("workflowApp")

workflowManagerApp.service "CategoryHttpService", ["$http", "SERVICES", ($http, SERVICES) ->
		get: (categoryId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.CATEGORIES + "/" + categoryId

		create: (categoryObj) ->
			$http
				method: "POST"
				url: SERVICES._PREFIX + SERVICES.CATEGORIES
				data: categoryObj

		update: (categoryId, categoryObj) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.CATEGORIES + "/" + categoryId
				data: categoryObj

		countResources: (categoryId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.CATEGORIES + "/" + categoryId + SERVICES.CATEGORIES_RESOURCES_COUNT

		delete: (categoryId) ->
			$http
				method: "DELETE"
				url: SERVICES._PREFIX + SERVICES.CATEGORIES + "/" + categoryId
	]