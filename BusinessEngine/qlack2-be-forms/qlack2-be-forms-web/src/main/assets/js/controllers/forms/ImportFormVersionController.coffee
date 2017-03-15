angular
	.module("formsManagerApp")
	.controller('ImportFormVersionCtrl', ['$scope', '$state', '$stateParams', '$window', '$compile', 'FormHttpService', 'FormService', 'QFormValidation', 'WindowService', 'SecuritySrv', \
				($scope, $state, $stateParams, $window, $compile, FormHttpService, FormService, QFormValidation, WindowService, SecuritySrv) ->
		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		$scope.getTicket = () ->
			JSON.stringify(SecuritySrv.getUser().ticket)

		$scope.attachment = {}
		$scope.importVersion = {}
		$scope.fileHasError = false

		$scope.save = () ->
			if $scope.attachment.flow.files[0]?
				$scope.importVersion.file = $scope.attachment.flow.files[0].uniqueIdentifier
			else
				$scope.importVersion.file = null

			FormHttpService.importFormVersion($stateParams.resourceId, $scope.importVersion).then(
				success = (result) ->
					versionId = result.data

					WindowService.closeWindow()

					NotificationSrv.add(
						title: "forms_ui.form_version_imported_title"
						content: "forms_ui.form_version_imported_content"
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
					QFormValidation.renderFormErrors($scope, $scope.importFormVersionForm, result)
			)

		$scope.$on('VALIDATION_ERROR_file', (event, data) ->
			$scope.fileHasError = true
			div = $('div#file')
			error = "<i popover='" + data.translation + "' popover-placement='left' popover-trigger='mouseenter' class='fa fa-exclamation-triangle form-control-feedback pull-right'></i>"
			div.prepend($compile(error)($scope))
			return
		)

		$scope.cancel = () ->
			WindowService.closeWindow()

	])