angular
	.module('explorer')
	.controller 'EditProjectCtrl', ['$scope', '$state', '$stateParams', '$q', 'ProjectHttpService', 'ProjectService', 'WindowService', 'QDateSrv', 'QFormValidation', 'SecuritySrv', '$window', '$translate', \
	($scope, $state, $stateParams, $q, ProjectHttpService, ProjectService, WindowService, QDateSrv, QFormValidation, SecuritySrv, $window, $translate) ->
		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		$scope.newProject = false
		projectPromise = ProjectHttpService.getProject($stateParams.projectId).then(
			success = (response) ->
				$scope.project = response.data
				return response.data
		)
		ProjectService.setProjectPromise(projectPromise)

		projectPromise.then(
			success = (response) ->
				SecuritySrv.resolvePermission('EXP_MANAGE_PROJECT', response.id)
		)

		$scope.actionsDataSource = new kendo.data.DataSource(
			data: [{
				key: "delete_project"
				icon: "fa-times"
				onSelect: () ->
					deleteProject()
			}]
		)
		$scope.actionsListTemplate = kendo.template($("#actionsListTemplate").html())
		$scope.executeAction = (e) ->
			e.preventDefault()
			item = e.sender.dataItem(e.item.index())
			if (item.onSelect?)
				item.onSelect()

		deleteProject = () ->
			WindowService.openWindow("delete_project", "views/projects/deleteProject.html")
			return

		$scope.cancel = () ->
			$state.go $state.current, $stateParams,
				reload: true

		$scope.save = () ->
			ProjectHttpService.updateProject($scope.project).then(
				success = (response) ->
					NotificationSrv.add(
						title: "explorer.project_updated_title"
						content: "explorer.project_updated_content"
						content_data:
							project: $scope.project.name
						bubble:
							show: true
					)
					$state.go "projects.project",
						projectId: $stateParams.projectId
					,
						reload: true
				error = (response) ->
					QFormValidation.renderFormErrors($scope, $scope.projectForm, response)
			)
	]

