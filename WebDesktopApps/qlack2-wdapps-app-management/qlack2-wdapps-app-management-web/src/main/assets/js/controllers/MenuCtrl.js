angular.module('appManagement')
.controller('MenuCtrl', ['$scope', 'SecuritySrv', '$window', 
function($scope, SecuritySrv, $window) {
	$scope.$watch(function() {
		return SecuritySrv.getUser();
	}, function(newVal, oldVal) {
		return SecuritySrv.resolvePermission('APPMANAGEMENT_CONFIGURE');
	});

	$scope.exit = function() {
		var taskbarService;
		taskbarService = $window.parent.WDUtil.service('TaskbarService');
		taskbarService.closeWindow($window);
		$window.parent.WDUtil.service('$timeout')(function() {
			$window.parent.WDUtil.scope().$apply();
		});
	};
}]);
