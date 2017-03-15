angular
	.module("formsManagerApp")
	.service "CategoryService", ["$q", "CategoryHttpService", "SecuritySrv", ($q, CategoryHttpService, SecuritySrv) ->
		category = {}

		actions = []

		canManageCategory = null

		getCategory: () ->
			category

		#Function for retrieving the metadata of a category
		getCategoryById: (categoryId, projectId) ->
			deferred = $q.defer()
			SecuritySrv.resolvePermission('FRM_MANAGE_CATEGORY', projectId).then((permitted) ->
				canManageCategory = permitted

				actions.pop() while actions.length > 0

				if canManageCategory
					actions.push({ name: "delete", value: "deleteCategory", icon: "fa fa-trash-o" })

				CategoryHttpService.getCategory(categoryId).then(
					success = (result) ->
						category = result.data
						deferred.resolve(result.data)
						return
				)
			)
			deferred.promise

		getActions: () ->
			actions

		getCanManageCategory: () ->
			canManageCategory
	]