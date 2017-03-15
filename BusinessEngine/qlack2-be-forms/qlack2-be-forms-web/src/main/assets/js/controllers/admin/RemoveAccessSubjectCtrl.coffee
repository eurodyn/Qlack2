angular
	.module('formsManagerApp')
	.controller 'RemoveAccessSubjectCtrl', ['$scope', '$state', '$stateParams', 'ConfigHttpService', 'WindowService',\
	($scope, $state, $stateParams, ConfigHttpService, WindowService) ->
		$scope.cancel = () ->
			WindowService.closeWindow()
			
		$scope.remove = () ->
			ConfigHttpService.unmanageSubject($stateParams.resourceId, WindowService.getWindow().data.subjectId).then(
				success = (response) ->
					WindowService.closeWindow()
					$state.go "access.resource", $stateParams.resourceId,
						reload: true
			)
			
		return
	]
			