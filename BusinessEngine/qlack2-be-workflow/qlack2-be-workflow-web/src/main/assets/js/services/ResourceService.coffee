workflowManagerApp = angular.module("workflowApp")

workflowManagerApp.service "ResourceService", ['$q', ($q) ->
		categoryId = null

		getCategoryId: () ->
			categoryId

		setCategoryId: (id) ->
			categoryId = id
	]