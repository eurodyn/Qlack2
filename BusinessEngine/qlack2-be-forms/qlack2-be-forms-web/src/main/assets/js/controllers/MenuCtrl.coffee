angular
	.module('formsManagerApp')
	.controller 'MenuCtrl', ['$scope', 'SecuritySrv', '$window', \
	($scope, SecuritySrv, $window) ->
#		Watching user to resolve permissions since we do not pass
#		through UI router to get to this controller and therefore it 
#		might be executed before having a chance to initialise the security service
		$scope.$watch(() ->
			SecuritySrv.getUser()
		, (newVal, oldVal) ->
			SecuritySrv.resolvePermission('FRM_CONFIGURE')
		)
		
		$scope.exit = () ->
			taskbarService = $window.parent.WDUtil.service('TaskbarService')
			taskbarService.closeWindow($window)
			$window.parent.WDUtil.service('$timeout')(->
				$window.parent.WDUtil.scope().$apply()
			)
	]