angular
	.module("formsManagerApp")
	.controller('DeleteFormVersionCtrl', ['$scope', '$state', '$stateParams', '$window', 'FormVersionHttpService', 'WindowService', \
				($scope, $state, $stateParams, $window, FormVersionHttpService, WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		#Function for deleting a form version
		$scope.deleteFormVersion = () ->
			FormVersionHttpService.deleteFormVersion($stateParams.versionId).then(
				success = (result) ->
					WindowService.closeWindow()

					NotificationSrv.add(
						title: "forms_ui.form_version_deleted_title"
						content: "forms_ui.form_version_deleted_content"
						bubble:
							show: true
					)

					$state.go "resources.form",
						{ projectId: $stateParams.projectId, resourceId: $stateParams.resourceId },
						reload: true
			)

		$scope.cancel = () ->
			WindowService.closeWindow()
	])