workflowManagerApp = angular.module("workflowApp")

workflowManagerApp.service "WindowService", ["$rootScope", ($rootScope) ->
		window = null

		getWindow: () ->
			window

		openWindow: (titleKey, content) ->
			window =
				titleKey: titleKey
				content: content
			return

		closeWindow: () ->
			window = null
			$rootScope.$emit('WINDOW_CLOSED')
	]