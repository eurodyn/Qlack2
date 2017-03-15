angular
	.module("formsManagerApp")
	.controller('CategoryCtrl', ['$scope', '$state', '$stateParams', '$window', 'CategoryService', 'CategoryHttpService', 'QFormValidation', 'WindowService', \
			($scope, $state, $stateParams, $window, CategoryService, CategoryHttpService, QFormValidation, WindowService) ->
		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		$scope.categoryService = CategoryService

		projectId = $stateParams.projectId
		categoryId = $stateParams.resourceId

		$scope.category = {}

		CategoryService.getCategoryById(categoryId, projectId).then(
			success = (result) ->
				category = CategoryService.getCategory()
				$scope.category = category

				$scope.canManageCategory = CategoryService.getCanManageCategory()
		)

		#Actions that can be performed on a category
		$scope.actionsDataSource = new kendo.data.DataSource(
			data: CategoryService.getActions()
		)

		$scope.actionsListTemplate = kendo.template($("#actionsListTemplate").html())

		#Watch changes made to the actions list and update the datasource accordingly
		$scope.$watchCollection('categoryService.getActions()', (newVal) ->
			$scope.actionsDataSource.data(CategoryService.getActions())
		)

		#Function for updating the metadata of a category
		$scope.save = () ->
			#Pass the selected projectId to the data sent to the server
			$scope.category.projectId = $stateParams.projectId

			CategoryHttpService.updateCategory(categoryId, $scope.category).then(
				success = (result) ->
					NotificationSrv.add(
						title: "forms_ui.category_updated_title"
						content: "forms_ui.category_updated_content"
						content_data:
							category: $scope.category.name
						bubble:
							show: true
					)
					$state.go "resources.category",
						{resourceId: $stateParams.resourceId},
						reload: true
					return
				error = (result) ->
					QFormValidation.renderFormErrors($scope, $scope.categoryForm, result)
			)

		#Cancel changes made to the category
		$scope.cancel = (e) ->
			$state.go "resources.category",
				{resourceId: $stateParams.resourceId},
				reload: true

		#Function for executing method bound to the selected option in the actions dropdown
		$scope.executeFormAction = (e) ->
			e.preventDefault()
			item = e.sender.dataItem(e.item.index())
			eval(item.value + "()")
			return

		#Function for deciding whether to display a confirmation window for the deletion of a category
		#or to inform the user that the category is associated with other resources and cannot be deleted
		deleteCategory = (e) ->
			CategoryHttpService.countCategoryResources(categoryId).then(
				success = (result) ->
					if result.data? and result.data > 0
						WindowService.openWindow("delete_category", "views/categories/errorDeleteCategory.html")
					else
						WindowService.openWindow("delete_category", "views/categories/confirmDeleteCategory.html")
			)
	])
