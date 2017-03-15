workflowManagerApp = angular.module("workflowApp")
workflowManagerApp.controller 'WindowCtrl', ['$scope', '$rootScope', 'WindowService', '$timeout', ($scope, $rootScope, WindowService, $timeout) ->
		$scope.windowService = WindowService

		$scope.$watch (->
			WindowService.getWindow()
		), ((newVal, oldVal) ->
			$scope.window = newVal
			return
		)

		$scope.windowOpen = (e) ->
#			Force the window to open after DOM manipulation in order for it to be correctly
#			centered based on its content
			$timeout(() ->
				e.sender.center().open()
			)

		$scope.windowClose = (e) ->
			# Close the application in the WindowService
			WindowService.closeWindow()
			return
	]