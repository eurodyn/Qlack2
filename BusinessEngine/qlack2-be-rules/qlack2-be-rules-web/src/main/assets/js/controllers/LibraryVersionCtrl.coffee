angular.module("rules")
	.controller('LibraryVersionCtrl', \
		['$scope', '$state', '$stateParams', 'LibraryService', 'LibraryVersionHttpService', "QDateSrv", \
		 ($scope,   $state,   $stateParams,   LibraryService,   LibraryVersionHttpService,   QDateSrv) ->

		libraryId = $stateParams.libraryId
		versionId = $stateParams.versionId

		$scope.versionItemTemplate = kendo.template($("#versionItemTemplate").html())

		$scope.versionsDataSource = new kendo.data.DataSource(
			data: []
		)

		$scope.selectVersion = (e) ->
			console.log(e)
			version = e.sender.dataItem(e.item.index())
			$state.go("tree.libraries-edit.versions", {libraryId: libraryId, versionId: version.id})

		$scope.libraryVersion = {}

		# resolve after security and main resource
		libraryVersionPromise = LibraryService.getLibraryPromise().then(
			success = (library) ->
				$scope.versionsDataSource.data(library.versions)
				return LibraryService.initLibraryVersion(versionId)
		)

		libraryVersionPromise.then(
			success = (libraryVersion) ->
				LibraryService.initActions()

				$scope.canManage = LibraryService.getCanManage()
				$scope.canEditVersion = LibraryService.getCanEditVersion()

				$scope.libraryVersion = libraryVersion
				$scope.classesDataSource.data(parseContentText(libraryVersion.contentText))
		)

		libraryVersionPromise.then(
			success = (libraryVersion) ->
				if (libraryVersion.lockedBy)
					$scope.lockedByTooltip =
						lockedby: libraryVersion.lockedBy.firstName + ' ' + libraryVersion.lockedBy.lastName
						lockedon: QDateSrv.localise(libraryVersion.lockedOn, 'lll')
		)

		# classes
		$scope.classesDataSource = new kendo.data.DataSource(
			data: []
		)

		$scope.classesColumns = [
			{
				field: "package"
				headerTemplate: "<span translate>header.package</span>"
				width: 110
			}, {
				field: "name"
				headerTemplate: "<span translate>header.name</span>"
				width: 90
			}
		]

		parseContentText = (contentText) ->
			classes = []

			if (!!contentText)
				classStrings = contentText.split(',')
				for classString in classStrings
					dot = classString.lastIndexOf('.')
					classes.push(
						package: classString.substring(0, dot)
						name: classString.substring(dot + 1)
					)

			return classes

		return this
	])
	.controller("LibraryVersionCreateCtrl", \
		["$window", "$scope", "$compile", "$state", "$stateParams", "SecuritySrv", "LibraryHttpService", "QFormValidation", "WindowService", \
		 ($window,   $scope,   $compile,   $state,   $stateParams,   SecuritySrv,   LibraryHttpService,   QFormValidation,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		libraryId = $stateParams.libraryId
		versionId = $stateParams.versionId

		$scope.libraryVersion = {}

		$scope.attachment = {}

		$scope.getTicket = () ->
			JSON.stringify(SecuritySrv.getUser().ticket)

		$scope.create = () ->
			prepareVersionForCreate()
			LibraryHttpService.createLibraryVersion(libraryId, $scope.libraryVersion).then(
				success = (response) ->
					WindowService.closeWindow()
					NotificationSrv.add(
						title: "rules.library_version_created_title"
						content: "rules.library_version_created_content"
						content_data:
							version: $scope.libraryVersion.name
						bubble:
							show: true
					)

					versionId = response.data
					$state.go("tree.libraries-edit.versions", {libraryId: libraryId, versionId: versionId}, {reload: true})
				error = (response) ->
					if (response.status is 400)
						QFormValidation.renderFormErrors($scope, $scope.libraryVersionForm, response)
					else
						throw new Error("Cannot create library version")
			)

		$scope.cancel = () ->
			WindowService.closeWindow()

		prepareVersionForCreate = () ->
			if $scope.attachment.flow.files[0]?
				$scope.libraryVersion.contentJAR = $scope.attachment.flow.files[0].uniqueIdentifier
			else
				$scope.libraryVersion.contentJAR = null

		$scope.contentJarHasError = false

		$scope.$on('VALIDATION_ERROR_contentJar', (event, data) ->
			$scope.contentJarHasError = true
			div = $('div#contentJar')
			error = "<i popover='" + data.translation + "' popover-placement='left' popover-trigger='mouseenter' class='fa fa-exclamation-triangle form-control-feedback pull-right'></i>"
			div.prepend($compile(error)($scope))
			return
		)

		return this
	])
	.controller("LibraryVersionUpdateCtrl", \
		["$window", "$scope", "$compile", "$state", "$stateParams", "SecuritySrv", "LibraryVersionHttpService", "QFormValidation", "WindowService", \
		 ($window,   $scope,   $compile,   $state,   $stateParams,   SecuritySrv,   LibraryVersionHttpService,   QFormValidation,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		libraryId = $stateParams.libraryId
		versionId = $stateParams.versionId

		$scope.libraryVersion =
			id: versionId

		$scope.attachment = {}

		$scope.getTicket = () ->
			JSON.stringify(SecuritySrv.getUser().ticket)

		$scope.update = () ->
			prepareVersionForUpdate()
			LibraryVersionHttpService.update(versionId, $scope.libraryVersion).then(
				success = (response) ->
					WindowService.closeWindow()
					NotificationSrv.add(
						title: "rules.library_version_content_updated_title"
						content: "rules.library_version_content_updated_content"
						bubble:
							show: true
					)

					$state.go("tree.libraries-edit.versions", {libraryId: libraryId, versionId: versionId}, {reload: true})
				error = (response) ->
					if (response.status is 400)
						QFormValidation.renderFormErrors($scope, $scope.libraryVersionForm, response)
					else
						throw new Error("Cannot update library version")
			)

		$scope.cancel = () ->
			WindowService.closeWindow()

		prepareVersionForUpdate = () ->
			if $scope.attachment.flow.files[0]?
				$scope.libraryVersion.contentJAR = $scope.attachment.flow.files[0].uniqueIdentifier
			else
				$scope.libraryVersion.contentJAR = null

		$scope.contentJarHasError = false

		$scope.$on('VALIDATION_ERROR_contentJar', (event, data) ->
			$scope.contentJarHasError = true
			div = $('div#contentJar')
			error = "<i popover='" + data.translation + "' popover-placement='left' popover-trigger='mouseenter' class='fa fa-exclamation-triangle form-control-feedback pull-right'></i>"
			div.prepend($compile(error)($scope))
			return
		)

		return this
	])
	.controller("LibraryVersionDeleteCtrl", \
		["$window", "$scope", "$state", "$stateParams", "LibraryVersionHttpService", "WindowService", \
		 ($window,   $scope,   $state,   $stateParams,   LibraryVersionHttpService,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		libraryId = $stateParams.libraryId
		versionId = $stateParams.versionId

		$scope.getData = () ->
			return WindowService.getWindow().data

		$scope.delete = () ->
			LibraryVersionHttpService.delete(versionId).then(
				success = (response) ->
					WindowService.closeWindow()
					NotificationSrv.add(
						title: "rules.library_version_deleted_title"
						content: "rules.library_version_deleted_content"
						bubble:
							show: true
					)

					$state.go("tree.libraries-edit", {libraryId: libraryId}, {reload: true})
				error = (response) ->
					throw new Error("Cannot delete library version")
			)

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
	.controller("LibraryVersionDisableTestingCtrl", \
		["$scope", "WindowService", \
		 ($scope,   WindowService) ->

		$scope.getData = () ->
			return WindowService.getWindow().data

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
	.controller("LibraryVersionFinalizeCtrl", \
		["$window", "$scope", "$state", "$stateParams", "LibraryVersionHttpService", "WindowService", \
		 ($window,   $scope,   $state,   $stateParams,   LibraryVersionHttpService,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		libraryId = $stateParams.libraryId
		versionId = $stateParams.versionId

		$scope.finalize = () ->
			LibraryVersionHttpService.finalise(versionId).then(
				success = (response) ->
					WindowService.closeWindow()
					NotificationSrv.add(
						title: "rules.library_version_finalised_title"
						content: "rules.library_version_finalised_content"
						bubble:
							show: true
					)

					$state.go("tree.libraries-edit.versions", {libraryId: libraryId, versionId: versionId}, {reload: true})
				error = (response) ->
					throw new Error("Cannot finalize library version")
			)

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
	.controller("LibraryVersionImportCtrl", \
		["$window", "$scope", "$compile", "$state", "$stateParams", "SecuritySrv", "LibraryHttpService", "QFormValidation", "WindowService", \
		 ($window,   $scope,   $compile,   $state,   $stateParams,   SecuritySrv,   LibraryHttpService,   QFormValidation,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		libraryId = $stateParams.libraryId

		$scope.libraryVersion = {}

		$scope.attachment = {}

		$scope.getTicket = () ->
			JSON.stringify(SecuritySrv.getUser().ticket)

		prepareVersionForImport = () ->
			if $scope.attachment.flow.files[0]?
				$scope.libraryVersion.file = $scope.attachment.flow.files[0].uniqueIdentifier
			else
				$scope.libraryVersion.file = null

		$scope.import = () ->
			prepareVersionForImport()
			LibraryHttpService.importVersion(libraryId, $scope.libraryVersion).then(
				success = (response) ->
					versionId = response.data

					WindowService.closeWindow()
					NotificationSrv.add(
						title: "rules.library_version_imported_title"
						content: "rules.library_version_imported_content"
						bubble:
							show: true
					)

					$state.go("tree.libraries-edit.versions", {libraryId: libraryId, versionId: versionId}, {reload: true})
				error = (response) ->
					if (response.status is 400)
						QFormValidation.renderFormErrors($scope, $scope.libraryVersionForm, response)
					else
						throw new Error("Cannot import library version")
			)

		$scope.cancel = () ->
			WindowService.closeWindow()

		$scope.fileHasError = false

		$scope.$on('VALIDATION_ERROR_file', (event, data) ->
			$scope.fileHasError = true
			div = $('div#file')
			error = "<i popover='" + data.translation + "' popover-placement='left' popover-trigger='mouseenter' class='fa fa-exclamation-triangle form-control-feedback pull-right'></i>"
			div.prepend($compile(error)($scope))
			return
		)

		return this
	])
