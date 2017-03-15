workflowManagerApp = angular.module("workflowApp")

workflowManagerApp.service "CategoryService", ["$q", "CategoryHttpService", "SecuritySrv", ($q, CategoryHttpService, SecuritySrv) ->
	category = {}

	canManageCategory = null

	getCategory: () ->
		category

	#Function for retrieving the metadata of a category
	getCategoryById: (categoryId, projectId) ->
		deferred = $q.defer()
		SecuritySrv.resolvePermission('WFL_MANAGE_CATEGORY', projectId).then((permitted) ->
			canManageCategory = permitted
			CategoryHttpService.get(categoryId).then(
				success = (result) ->
					category = result.data
					deferred.resolve(result.data)
					return
			)
		)
		deferred.promise

	getCanManageCategory: () ->
		canManageCategory
]