angular
	.module("formsManagerApp")
	.controller('CreateFormVersionCtrl', ['$scope', '$state', '$stateParams', '$window', 'FormHttpService', 'FormService', 'QFormValidation', 'WindowService', \
				($scope, $state, $stateParams, $window, FormHttpService, FormService, QFormValidation, WindowService) ->
		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		$scope.formVersion = {}

		$scope.basedOnDataSource  = new kendo.data.DataSource(
			data: FormService.getForm().formVersions
		)

		$scope.basedOnListTemplate = kendo.template($("#basedOnListTemplate").html())

		$scope.createFormVersion = () ->
			FormHttpService.createFormVersion($stateParams.resourceId, $scope.formVersion).then(
				success = (result) ->
					versionId = result.data
					WindowService.closeWindow()

					NotificationSrv.add(
						title: "forms_ui.form_version_created_title"
						content: "forms_ui.form_version_created_content"
						content_data:
							form_version: $scope.formVersion.name
						bubble:
							show: true
					)

					FormService.getFormVersionsByFormId($stateParams.resourceId).then(
						success = (result) ->
							$state.go "resources.form.version",
								{ resourceId: $stateParams.resourceId, versionId: versionId },
								reload: true
							return
					)
					return
				error = (result) ->
					QFormValidation.renderFormErrors($scope, $scope.createFormVersionForm, result)
			)

		$scope.cancel = () ->
			WindowService.closeWindow()
	])