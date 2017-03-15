angular
	.module('explorer')
	.controller 'CreateProjectCtrl', ['$scope', '$state', '$stateParams', 'ProjectHttpService', 'ProjectService', 'QFormValidation', '$window', \
	($scope, $state, $stateParams, ProjectHttpService, ProjectService, QFormValidation, $window) ->
		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')
		
		$scope.newProject = true
		ProjectService.setProjectPromise(null)
		$scope.project = 
			active: true
			rules: true
			workflows: true
			forms: true
			
		$scope.cancel = () ->
			$state.go "projects", {}, 
				reload: true
				
		$scope.save = () ->
			ProjectHttpService.createProject($scope.project).then(
				success = (response) ->
					NotificationSrv.add(
						title: "explorer.project_created_title"
						content: "explorer.project_created_content"
						content_data: 
							project: $scope.project.name
						bubble:
							show: true
					)
					$state.go "projects.project",
						projectId: response.data
					,
						reload: true
				error = (response) ->
					QFormValidation.renderFormErrors($scope, $scope.projectForm, response)
			)
	]

