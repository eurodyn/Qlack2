angular
	.module("formsManagerApp")
	.controller('DeleteFormCtrl', ['$scope', '$state', '$stateParams', '$window', 'FormHttpService', 'WindowService', \
				($scope, $state, $stateParams, $window, FormHttpService, WindowService) ->
		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		#Function for deleting a form
		$scope.deleteForm = () ->
			FormHttpService.deleteForm($stateParams.resourceId).then(
				success = (result) ->
					WindowService.closeWindow()
					NotificationSrv.add(
						title: "forms_ui.form_deleted_title"
						content: "forms_ui.form_deleted_content"
						bubble:
							show: true
					)
					$state.go("resources", {projectId: $stateParams.projectId}, reload: true)
			)

		$scope.cancel = () ->
			WindowService.closeWindow()
	])