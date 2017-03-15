angular.module('appManagement')
.controller('AccessDetailsCtrl', ['$scope', '$state', '$stateParams', 
'ConfigHttpService', '$window', "QNgPubSubService", 
function($scope, $state, $stateParams, ConfigHttpService, $window, QNgPubSubService) {
	ConfigHttpService.getOperations($stateParams.appId, $stateParams.ownerId).then(function(response) {
		$scope.accessData = response.data;
	});
	
	$scope.accessTypes = new kendo.data.DataSource({
		data: [{
			name: 'default',
			value: null
		}, {
			name: 'allowed',
			value: 'true'
		}, {
			name: 'denied',
			value: 'false'
		}]
	});
	
	$scope.accessTemplate = kendo.template($("#accessTemplate").html());
	
	$scope.save = function() {
		var len, operation, ref;
		ref = $scope.accessData;
		for (var i = 0, len = ref.length; i < len; i++) {
			operation = ref[i];
			if (operation.access === "null") {
				operation.access = null;
			}
		}
		
		return ConfigHttpService.saveOperations($stateParams.appId, $stateParams.ownerId, $scope.accessData).then(function(response) {
			QNgPubSubService.publish(SERVICES.WD_SERVICE_TOPICS.NOTIFICATION_SERVICE, {
				title: $translate.instant("appmanagement.operations_saved_title"),
				content: $translate.instant("appmanagement.operations_saved_content"),
				audio: true,
				type: "error"
			});
		});
	};
	
	$scope.cancel = function() {
		$state.go($state.current, $stateParams, {
			reload: true
		});
	};
}]);
