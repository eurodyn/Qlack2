angular
	.module('workflowApp')
	.controller 'AccessDetailsCtrl', ['$scope', '$state', '$stateParams', 'ConfigHttpService', '$window', \
	($scope, $state, $stateParams, ConfigHttpService, $window) ->
		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')
	
		ConfigHttpService.getOperations($stateParams.resourceId, $stateParams.ownerId).then(
			success = (response) ->
				$scope.accessData = response.data
		)
		
		$scope.accessTypes = new kendo.data.DataSource(
			data: [
				{name: 'default', value: null}
				{name: 'allowed', value: 'true'}
				{name: 'denied', value: 'false'}
			]
		)
		$scope.accessTemplate = kendo.template($("#accessTemplate").html())		
			
		$scope.save = () ->
#			Convert "null" returned by the html select to null before submitting the data
			for operation in $scope.accessData
				if operation.access is "null"
					operation.access = null
			ConfigHttpService.saveOperations($stateParams.resourceId, $stateParams.ownerId, $scope.accessData).then(
				success = (response) ->
					NotificationSrv.add(
						title: "workflow_ui.operations_saved_title"
						content: "workflow_ui.operations_saved_content"
						content_data: 
							subject: $scope.selectedSubject.name
						bubble:
							show: true
					)
			)
		
		$scope.cancel = () ->
			$state.go $state.current, $stateParams,
				reload: true
	]