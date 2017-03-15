angular.module("appManagement")
.controller("ApplicationCtrl", ["$scope", "ApplicationHttpService", 
"$stateParams", "SecuritySrv", "$translate", "$window", "QNgPubSubService", 
"SERVICES",
function($scope, ApplicationHttpService, $stateParams, SecuritySrv, $translate, 
$window, QNgPubSubService, SERVICES) {
	SecuritySrv.resolvePermission('WD_UPDATE_APPLICATION');
	
	$scope.application = {};
	
	ApplicationHttpService.getApplicationById($stateParams.applicationId).then(function(result) {
		$scope.application = result.data;
	});

	$scope.save = function() {
		ApplicationHttpService.saveApplication($scope.application).then(function(response) {
			QNgPubSubService.publish(SERVICES.WD_SERVICE_TOPICS.NOTIFICATION_SERVICE, {
				title: $translate.instant("appmanagement.application_updated_title"),
				content: $translate.instant("appmanagement.application_updated_content"),
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
