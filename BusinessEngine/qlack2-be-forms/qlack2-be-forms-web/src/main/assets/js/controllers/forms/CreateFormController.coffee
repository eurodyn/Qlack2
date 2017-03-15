angular
	.module("formsManagerApp")
	.controller('CreateFormCtrl', ['$scope', '$state', '$stateParams', '$window', 'FormHttpService', 'ProjectHttpService', 'QFormValidation', \
				($scope, $state, $stateParams, $window, FormHttpService, ProjectHttpService, QFormValidation) ->
		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		$scope.form =
			active: true

		$scope.categoriesDataSource = new kendo.data.DataSource(
			transport:
				read: (options) ->
					ProjectHttpService.getProjectCategories($stateParams.projectId).then(
						success = (result) ->
							options.success(result.data)
					)
		)

		locales = []
		$scope.languageDataSource = new kendo.data.DataSource(
			data: locales
		)
		ProjectHttpService.getLanguages().then(
			success = (response) ->
				Array.prototype.push.apply(locales, response.data)
				return
		)

		$scope.save = () ->
			#Pass the selected projectId to the data sent to the server
			$scope.form.projectId = $stateParams.projectId

			FormHttpService.createForm($scope.form).then(
				success = (result) ->
					NotificationSrv.add(
						title: "forms_ui.form_created_title"
						content: "forms_ui.form_created_content"
						content_data:
							form: $scope.form.name
						bubble:
							show: true
					)

					$state.go "resources.form",
						{ resourceId: result.data },
						reload: true
					return
				error = (result) ->
					QFormValidation.renderFormErrors($scope, $scope.createFormForm, result)
			)

		$scope.cancel = () ->
			$state.go("resources", {projectId: $stateParams.projectId})
	])