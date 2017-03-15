angular
	.module("formsManagerApp")
	.service "ResourcesService", ['$q', ($q) ->
		categoryId = null

		getCategoryId: () ->
			categoryId

		setCategoryId: (id) ->
			categoryId = id
	]