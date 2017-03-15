angular
	.module("formsManagerApp")
	.controller('CreateFormVersionTranslationsCtrl', ['$scope', '$stateParams', 'FormService', 'UtilService', 'WindowService', \
			($scope, $stateParams, FormService, UtilService, WindowService) ->
		$scope.newTranslations = [{}]

		$scope.popupLanguagesDataSource = new kendo.data.DataSource(
			data: FormService.getFormLanguages()
		)

		$scope.addTranslation = () ->
			$scope.newTranslations.push({})
			return

		$scope.removeTranslation = (index) ->
			$scope.newTranslations.splice(index, 1)
			return

		$scope.createFormVersionTranslations = () ->
			keyId = UtilService.createUUID()

			for translation in $scope.newTranslations
				translation.keyId = keyId
				translation.key = $scope.newTranslationKey

			#Add translations for missing languages
			for lang in FormService.getFormLanguages()
				found = false
				for translation in $scope.newTranslations
					if translation.language == lang.id
						found = true

				if !found
					translation = {
						keyId: keyId
						key: $scope.newTranslationKey
						language: lang.id
						value: $scope.newTranslationKey
					}

					# set 'id' field for new rows so that kendo does not remove them on edit->cancel
					translation.kendoId = UtilService.createUUID()

					$scope.newTranslations.push translation

			FormService.createFormVersionTranslations($scope.newTranslations)

			WindowService.closeWindow()

		$scope.cancel = () ->
			WindowService.closeWindow()
	])
