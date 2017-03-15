angular
	.module("formsManagerApp")
	.controller('FinaliseFormVersionCtrl', ['$scope', '$state', '$stateParams', '$window', 'FormVersionHttpService', 'FormService', 'WindowService', \
				($scope, $state, $stateParams, $window, FormVersionHttpService, FormService, WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		$scope.finaliseFormVersion = () ->
			FormVersionHttpService.finaliseFormVersion($stateParams.versionId).then(
				success = (result) ->
					WindowService.closeWindow()

					NotificationSrv.add(
						title: "forms_ui.form_version_finalised_title"
						content: "forms_ui.form_version_finalised_content"
						content_data:
							form_version: FormService.getFormVersion().name
						bubble:
							show: true
					)

					FormService.getFormVersionsByFormId($stateParams.resourceId).then(
						success = (result) ->
							$state.go "resources.form.version",
								{ resourceId: $stateParams.resourceId, versionId: $stateParams.versionId },
								reload: true
							return
					)
					return
			)

		$scope.cancel = () ->
			WindowService.closeWindow()
	])