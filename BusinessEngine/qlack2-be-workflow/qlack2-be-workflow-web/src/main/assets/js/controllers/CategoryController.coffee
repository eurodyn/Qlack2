workflowManagerApp = angular.module("workflowApp")

workflowManagerApp.controller('CategoryCtrl',['$scope', '$http', '$location', '$state', '$stateParams', '$compile', '$q', 'CategoryHttpService', 'QFormValidation', 'CategoryService', 'WindowService', '$window', ($scope, $http, $location, $state, $stateParams, $compile, $q, CategoryHttpService, QFormValidation, CategoryService, WindowService, $window) ->
	NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')
	categoryId = $stateParams.resourceId
	console.log "selectedCategoryId: " + categoryId

	$scope.category = {}
	
	#Load category
	CategoryService.getCategoryById(categoryId, $state.params.projectId).then(
		success = (result) ->
			category = CategoryService.getCategory()
			$scope.category = category

			operations = []
			$scope.canManageCategory = CategoryService.getCanManageCategory()
		
			if $scope.canManageCategory
				operations.push({ name: "delete", value: "deleteCategoryAction", icon: "fa fa-trash-o" })
				$scope.actions.dataSource.data(operations)
			return
	)

	#Actions that can be performed on a category
	$scope.actions = {
		dataSource: new kendo.data.DataSource(
			data: []
		),
		dataTextField:"name",
		dataValueField:"value",
		template: kendo.template($("#actionsListTemplate").html()),
		change: (e) ->
			selectCategoryAction(e)
	}

	#Function for updating the metadata of a category
	$scope.save = () ->
		#Pass the selected projectId to the data sent to the server
		$scope.category.projectId = $state.params.projectId
		CategoryHttpService.update(categoryId, $scope.category).then(
			success = (result) ->
				console.log "success in category update"
				NotificationSrv.add(
					title: "workflow_ui.category_updated_title"
					content: "workflow_ui.category_updated_content"
					content_data: 
						category: $scope.category.name
					bubble:
						show: true
				)
				$state.go "resources.category",
						{resourceId: categoryId},
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
		return

	#Function for executing method bound to the selected option in the actions dropdown
	selectCategoryAction = (e) ->
		item = e.sender.dataItem(e.sender.selectedIndex)
		if item.value? and item.value isnt ''
			eval(item.value + "()")
		e.preventDefault()

	deleteCategoryAction = (e) ->
		CategoryHttpService.countResources(categoryId).then(
			success = (result) ->
				if result.data? and result.data > 0
					WindowService.openWindow("delete_category", "views/category/errorDeleteCategory.html")
				else
					WindowService.openWindow("delete_category", "views/category/confirmDeleteCategory.html")
			error = (result) ->
				console.log "count resources of category error"
		)
])

workflowManagerApp.controller('CreateCategoryCtrl',['$scope', '$http', '$location', '$state', '$stateParams', 'CategoryHttpService', 'QFormValidation', '$window', ($scope, $http, $location, $state, $stateParams, CategoryHttpService, QFormValidation, $window) ->
	NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')
	
	$scope.category = {}
	
	$scope.save = () ->
		#Pass the selected projectId to the data sent to the server
		$scope.category.projectId = $state.params.projectId
		CategoryHttpService.create($scope.category).then(
			success = (result) ->
				console.log "creation of category success"
				NotificationSrv.add(
					title: "workflow_ui.category_created_title"
					content: "workflow_ui.category_created_content"
					content_data: 
						category: $scope.category.name
					bubble:
						show: true
				)
				$state.go("resources.category", {resourceId: result.data}, reload: true)
			error = (result) ->
				console.log "creation of category error"
				QFormValidation.renderFormErrors($scope, $scope.newCategoryForm, result)
		)

	$scope.cancel = (e) ->
		e.preventDefault()
		$state.go("resources", {projectId: $state.params.projectId})
])

workflowManagerApp.controller('DeleteCategoryCtrl', ['$scope', '$state', '$stateParams', 'CategoryHttpService', 'WindowService', '$window', \
				($scope, $state, $stateParams, CategoryHttpService, WindowService, $window) ->
	NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')
	
	$scope.deleteCategory = () ->
		CategoryHttpService.delete($stateParams.resourceId).then(
			success = (result) ->
				WindowService.closeWindow()
				NotificationSrv.add(
					title: "workflow_ui.category_deleted_title"
					content: "workflow_ui.category_deleted_content"
					bubble:
						show: true
				)
				$state.go("resources", {projectId: $stateParams.projectId}, reload: true)
			error = (result) ->
				console.log "error in delete of workflow..."
		)
		
	$scope.cancel = () ->
		WindowService.closeWindow()
])