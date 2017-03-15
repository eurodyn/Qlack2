angular
	.module('explorer')
	.controller 'RemoveAccessSubjectCtrl', ['$scope', '$state', '$stateParams', 'ConfigHttpService', 'WindowService',\
	($scope, $state, $stateParams, ConfigHttpService, WindowService) ->
		$scope.cancel = () ->
			WindowService.closeWindow()
			
		$scope.remove = () ->
			ConfigHttpService.unmanageSubject($stateParams.projectId, WindowService.getWindow().data.subjectId).then(
				success = (response) ->
					WindowService.closeWindow()
					$state.go "access.project", $stateParams.projectId,
						reload: true
			)
			
		return
	]
			