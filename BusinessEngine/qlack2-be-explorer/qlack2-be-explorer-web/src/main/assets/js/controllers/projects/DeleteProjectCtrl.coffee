angular
	.module('explorer')
	.controller 'DeleteProjectCtrl', ['$scope', '$state', '$stateParams', 'ProjectService', 'ProjectHttpService', 'WindowService', '$window',\
	($scope, $state, $stateParams, ProjectService, ProjectHttpService, WindowService, $window) ->
		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')
		
		$scope.cancel = () ->
			WindowService.closeWindow()
			
		$scope.delete = () ->
			ProjectService.getProjectPromise().then(
				success = (project) ->
					ProjectHttpService.deleteProject(project.id).then(
						success = (result) ->
							NotificationSrv.add(
								title: "explorer.project_deleted_title"
								content: "explorer.project_deleted_content"
								content_data: 
									project: project.name
								bubble:
									show: true
							)
					
							WindowService.closeWindow()
							$state.go "projects", {}, 
								reload: true
						)
			)
			return
			
		return
	]
			