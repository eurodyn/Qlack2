angular
	.module("formsManagerApp")
	.controller('CreateCategoryCtrl', ['$scope', '$state', '$stateParams', '$window', 'CategoryHttpService', 'QFormValidation', \
			($scope, $state, $stateParams, $window, CategoryHttpService, QFormValidation) ->
		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		$scope.category = {}

		$scope.save = () ->
			#Pass the selected projectId to the data sent to the server
			$scope.category.projectId = $stateParams.projectId

			CategoryHttpService.createCategory($scope.category).then(
				success = (result) ->
					NotificationSrv.add(
						title: "forms_ui.category_created_title"
						content: "forms_ui.category_created_content"
						content_data:
							category: $scope.category.name
						bubble:
							show: true
					)
					$state.go "resources.category",
						{resourceId: result.data},
						reload: true
					return
				error = (result) ->
					QFormValidation.renderFormErrors($scope, $scope.createCategoryForm, result)
			)

		$scope.cancel = () ->
			$state.go("resources", {projectId: $stateParams.projectId})
	])