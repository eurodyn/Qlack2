angular
	.module("formsManagerApp")
	.controller('DeleteFormVersionTranslationsCtrl', ['$scope', 'FormService', 'WindowService', \
			($scope, FormService, WindowService) ->

		$scope.deleteFormVersionTranslations = () ->
			keyId = WindowService.getWindow().data.keyId
			FormService.deleteFormVersionTranslations(keyId)

			WindowService.closeWindow()

		$scope.cancel = () ->
			WindowService.closeWindow()
	])
