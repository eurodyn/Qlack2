angular.module("rules")
	.service("ProjectService", [ () ->
		categoryId = null

		getCategoryId: () ->
			categoryId

		setCategoryId: (newCategoryId) ->
			categoryId = newCategoryId
	])
