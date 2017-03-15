angular
	.module("formsManagerApp")
	.controller('DeleteCategoryCtrl', ['$scope', '$state', '$stateParams', '$window', 'CategoryHttpService', 'WindowService', \
				($scope, $state, $stateParams, $window, CategoryHttpService, WindowService) ->
		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		#Function for deleting a category
		$scope.deleteCategory = () ->
			CategoryHttpService.deleteCategory($stateParams.resourceId).then(
				success = (result) ->
					WindowService.closeWindow()

					NotificationSrv.add(
						title: "forms_ui.category_deleted_title"
						content: "forms_ui.category_deleted_content"
						bubble:
							show: true
					)

					$state.go("resources", {projectId: $stateParams.projectId}, reload: true)
			)

		$scope.cancel = () ->
			WindowService.closeWindow()
	])