angular.module("userManagement").controller("MenuCtrl", 
["$scope", "$state", "SecuritySrv", "UserHttpService", "$window", "$rootScope", 
 "$window", "SERVICES", "QNgPubSubService", "SERVICES",
function($scope, $state, SecuritySrv, UserHttpService, $window, $rootScope, 
$window, SERVICES, QNgPubSubService, SERVICES) {
	
	$scope.$watch(function() {
		return SecuritySrv.getUser();
	}, function(newVal, oldVal) {
		SecuritySrv.resolvePermission('WD_MANAGE_USERS');
		SecuritySrv.resolvePermission('WD_MANAGE_GROUPS');
		return SecuritySrv.resolvePermission('USERMANAGEMENT_CONFIGURE');
	});

	$scope.$watch(function() {
		return SecuritySrv.getUser();
	}, function(newVal, oldVal) {
		var secutityUser, success, userId;
		secutityUser = SecuritySrv.getUser();
		if (secutityUser) {
			userId = secutityUser.ticket.userID;
			return UserHttpService.getUser(userId).then(success = function(response) {
				var user;
				user = response.data;
				return $scope.userIsSuperadmin = user.superadmin;
			});
		}
	});

	$scope.userIsSuperadmin = false;

	$scope.groupIsSelected = function() {
		return $state.current.name.indexOf('groups.group') === 0;
	};

	$scope.exit = function() {
		// Find application and instance ID of this window.
		var appID = $($window.frameElement).attr("data-app-id");
		var instanceID = $($window.frameElement).attr("data-app-instance-id");
		// TODO move in constants.
		QNgPubSubService.publish(SERVICES.WD_SERVICE_TOPICS.DESKTOP_SERVICE_CLOSE, {
			appID: appID,
			instanceID: instanceID
		});
	};
}]);
