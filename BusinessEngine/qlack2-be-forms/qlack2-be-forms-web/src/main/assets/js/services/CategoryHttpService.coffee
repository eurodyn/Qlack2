angular
	.module("formsManagerApp")
	.service "CategoryHttpService", ["$http", "SERVICES", ($http, SERVICES) ->
		getCategory: (categoryId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.CATEGORY + "/" + categoryId

		createCategory: (categoryObj) ->
			$http
				method: "POST"
				url: SERVICES._PREFIX + SERVICES.CATEGORY
				data: categoryObj

		updateCategory: (categoryId, categoryObj) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.CATEGORY + "/" + categoryId
				data: categoryObj

		countCategoryResources: (categoryId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.CATEGORY + "/" + categoryId + SERVICES.CATEGORY_RESOURCES_COUNT

		deleteCategory: (categoryId) ->
			$http
				method: "DELETE"
				url: SERVICES._PREFIX + SERVICES.CATEGORY + "/" + categoryId
	]