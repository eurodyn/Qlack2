angular
	.module("formsManagerApp")
	.service "WindowService", ["$rootScope", ($rootScope) ->
		window = null

		getWindow: () ->
			window

		openWindow: (titleKey, content, data) ->
			window =
				titleKey: titleKey
				content: content
				data: data
			return

		closeWindow: () ->
			window = null
			$rootScope.$emit('WINDOW_CLOSED')
	]