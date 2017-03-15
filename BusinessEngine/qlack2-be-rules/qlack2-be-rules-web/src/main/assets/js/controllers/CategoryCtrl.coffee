angular.module("rules")
	.controller("CategoryEditCtrl", \
		["$window", "$scope", "$state", "$stateParams", "CategoryHttpService", "SecuritySrv", "QFormValidation", "WindowService", \
		 ($window,   $scope,   $state,   $stateParams,   CategoryHttpService,   SecuritySrv,   QFormValidation,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		projectId = $stateParams.projectId
		categoryId = $stateParams.categoryId

		$scope.category = {}

		$scope.canManage = null

		SecuritySrv.resolvePermission('RUL_MANAGE_CATEGORY', projectId).then(
			success = (canManage) ->
				$scope.canManage = canManage

				actions = []
				if (canManage)
					actions.push(
						name: "action.delete"
						icon: "fa fa-trash-o"
						onSelect: () ->
							deleteCategory()
					)

				$scope.actionsDataSource.data(actions)

				CategoryHttpService.getById(categoryId).then(
					success = (response) ->
						category = response.data
						$scope.category = category
					error = (response) ->
						throw new Error("Cannot fetch category")
				)
		)

		# Update
		$scope.update = () ->
			CategoryHttpService.update(categoryId, $scope.category).then(
				success = (response) ->
					NotificationSrv.add(
						title: "rules.category_updated_title"
						content: "rules.category_updated_content"
						content_data:
							category: $scope.category.name
						bubble:
							show: true
					)
					$state.go("tree.categories-edit", {categoryId: categoryId}, {reload: true})
				error = (response) ->
					if (response.status is 400)
						QFormValidation.renderFormErrors($scope, $scope.categoryForm, response)
					else
						throw new Error("Cannot update category")
			)

		$scope.cancel = () ->
			$state.go("tree.categories-edit", {categoryId: categoryId}, {reload: true})

		# Actions
		$scope.actionItemTemplate = kendo.template($("#actionItemTemplate").html())

		$scope.actionsDataSource = new kendo.data.DataSource(
			data: []
		)

		$scope.selectAction = (e) ->
			console.log(e)
			e.preventDefault()
			item = e.sender.dataItem(e.item.index())
			if (item.onSelect?)
				item.onSelect()

		# Delete action
		deleteCategory = () ->
			CategoryHttpService.canDeleteCategory(categoryId).then(
				success = (response) ->
					result = response.data
					if (result.result)
						WindowService.openWindow("title.delete_category", "views/categories/delete-confirm.html")
					else
						WindowService.openWindow("title.delete_category", "views/categories/delete-not-allowed.html", result)
			)
	])
	.controller("CategoryCreateCtrl", \
		["$window", "$scope", "$state", "$stateParams", "CategoryHttpService", "QFormValidation", \
		 ($window,   $scope,   $state,   $stateParams,   CategoryHttpService,   QFormValidation) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		projectId = $stateParams.projectId

		$scope.category = {}

		$scope.create = () ->
			CategoryHttpService.create(projectId, $scope.category).then(
				success = (response) ->
					NotificationSrv.add(
						title: "rules.category_created_title"
						content: "rules.category_created_content"
						content_data:
							category: $scope.category.name
						bubble:
							show: true
					)
					categoryId = response.data
					$state.go("tree.categories-edit", {categoryId: categoryId}, {reload: true})
				error = (response) ->
					if (response.status is 400)
						QFormValidation.renderFormErrors($scope, $scope.categoryForm, response)
					else
						throw new Error("Cannot create category")
			)

		$scope.cancel = () ->
			$state.go("tree", {projectId: projectId})

		return this
	])
	.controller("CategoryDeleteCtrl", \
		["$window", "$scope", "$state", "$stateParams", "CategoryHttpService", "WindowService", \
		 ($window,   $scope,   $state,   $stateParams,   CategoryHttpService,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		projectId = $stateParams.projectId
		categoryId = $stateParams.categoryId

		$scope.getData = () ->
			return WindowService.getWindow().data

		$scope.delete = () ->
			CategoryHttpService.deleteCategory(categoryId).then(
				success = (response) ->
					WindowService.closeWindow()
					NotificationSrv.add(
						title: "rules.category_deleted_title"
						content: "rules.category_deleted_content"
						bubble:
							show: true
					)
					$state.go("tree", {projectId: projectId}, {reload: true})
				error = (response) ->
					console.log("Cannot delete category")
			)

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
