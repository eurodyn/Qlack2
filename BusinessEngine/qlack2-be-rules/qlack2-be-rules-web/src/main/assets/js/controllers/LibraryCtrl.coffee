angular.module("rules")
	.controller('LibraryCtrl', \
		['$window', '$scope', '$state', '$stateParams', '$q', 'SecuritySrv', 'LibraryService', 'LibraryHttpService', 'LibraryVersionHttpService', 'CategoryHttpService', 'QFormValidation', 'WindowService', \
		 ($window,   $scope,   $state,   $stateParams,   $q,   SecuritySrv,   LibraryService,   LibraryHttpService,   LibraryVersionHttpService,   CategoryHttpService,   QFormValidation,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		projectId = $stateParams.projectId
		libraryId = $stateParams.libraryId

		LibraryService.init()

		$scope.libraryService = LibraryService # for watch

		# $scope.canManage = null
		# $scope.canEditVersion = null

		$scope.library = {}

		categories = []
		$scope.categoriesDataSource = new kendo.data.DataSource(
			data: categories
		)

		libraryPromise = LibraryService.initLibraryPromise(libraryId, projectId)

		categoriesPromise = CategoryHttpService.getByProjectId(projectId).then(
			success = (response) ->
				response.data
			error = (response) ->
				throw new Error("Cannot fetch categories")
		)

		$q.all([libraryPromise, categoriesPromise]).then(
			success = (results) ->
				$scope.canManage = LibraryService.getCanManage()

				Array.prototype.push.apply(categories, results[1])

				library = results[0]
				$scope.library = library

				if ($state.current.name is "tree.libraries-edit")
					if (library.versions.length > 0)
						version = library.versions[0]
						$state.go("tree.libraries-edit.versions", {libraryId: libraryId, versionId: version.id})
					else
						LibraryService.initActionsNoVersion()
		)

		$scope.$watch('libraryService.getCanEditVersion()', (canEditVersion) ->
			$scope.canEditVersion = canEditVersion
		)

		# Update
		$scope.update = () ->
			prepareLibraryForUpdate()

			LibraryHttpService.update(libraryId, $scope.library).then(
				success = (response) ->
					NotificationSrv.add(
						title: "rules.library_updated_title"
						content: "rules.library_updated_content"
						content_data:
							library: $scope.library.name
						bubble:
							show: true
					)

					if ($state.current.name is "tree.libraries-edit")
						$state.go("tree.libraries-edit", {libraryId: libraryId}, {reload: true})
					else
						versionId = $stateParams.versionId
						$state.go("tree.libraries-edit.versions", {libraryId: libraryId, versionId: versionId}, {reload: true})
					return
				error = (response) ->
					if (response.status is 400)
						QFormValidation.renderFormErrors($scope, $scope.libraryForm, response)
					else
						throw new Error("Cannot update library")
			)

		$scope.cancel = () ->
			$state.go("tree.libraries-edit", {libraryId: libraryId}, {reload: true})

		prepareLibraryForUpdate = () ->
			$scope.library.projectId = projectId

			# scope-crossing
			$scope.library.version = LibraryService.getLibraryVersion()

		# Actions
		$scope.actionItemTemplate = kendo.template($("#actionItemTemplate").html())

		$scope.actionsDataSource = new kendo.data.DataSource(
			data: []
			sort:
				field: "order"
				dir: "asc"
		)

		$scope.$watchCollection('libraryService.getActions()', (actions) ->
			$scope.actionsDataSource.data(actions)
		)

		actionCallbacks =
			deleteLibrary: () ->
				deleteLibrary()
			createVersion: () ->
				createVersion()
			updateVersionContent: () ->
				updateVersionContent()
			deleteVersion: () ->
				deleteVersion()
			lockVersion: () ->
				lockVersion()
			unlockVersion: () ->
				unlockVersion()
			importVersion: () ->
				importVersion()
			exportVersion: () ->
				exportVersion()
			enableTestingVersion: () ->
				enableTestingVersion()
			disableTestingVersion: () ->
				disableTestingVersion()
			finalizeVersion: () ->
				finalizeVersion()

		$scope.selectAction = (e) ->
			console.log(e)
			e.preventDefault()
			item = e.sender.dataItem(e.item.index())
			action = actionCallbacks[item.value]
			if (action?)
				action()

		# Delete action
		deleteLibrary = () ->
			LibraryHttpService.canDeleteLibrary(libraryId).then(
				success = (response) ->
					result = response.data
					if (result.result)
						WindowService.openWindow("title.delete_library", "views/libraries/confirmDeleteLibrary.html")
					else
						WindowService.openWindow("title.delete_library", "views/libraries/errorDeleteLibrary.html", result)
			)

		# Version actions
		createVersion = () ->
			WindowService.openWindow("action.create_version", "views/libraries/createLibraryVersion.html")

		deleteVersion = () ->
			libraryVersion = LibraryService.getLibraryVersion()
			if (!!libraryVersion)
				versionId = libraryVersion.id
				LibraryVersionHttpService.canDeleteVersion(versionId).then(
					success = (response) ->
						result = response.data
						if (result.result)
							WindowService.openWindow("action.delete_version", "views/libraries/confirmDeleteLibraryVersion.html")
						else
							WindowService.openWindow("action.delete_version", "views/libraries/errorDeleteLibraryVersion.html", result)
				)

		updateVersionContent = () ->
			WindowService.openWindow("action.update_version_content", "views/libraries/updateLibraryVersion.html")

		lockVersion = () ->
			libraryVersion = LibraryService.getLibraryVersion()
			if (!!libraryVersion)
				versionId = libraryVersion.id
				LibraryVersionHttpService.lock(versionId).then(
					success = (response) ->
						NotificationSrv.add(
							title: "rules.library_version_locked_title"
							content: "rules.library_version_locked_content"
							content_data:
								version: libraryVersion.name
							bubble:
								show: true
						)

						$state.go("tree.libraries-edit.versions", {libraryId: libraryId, versionId: versionId}, {reload: true})
					error = (response) ->
						throw new Error("Cannot lock library version")
				)

		unlockVersion = () ->
			libraryVersion = LibraryService.getLibraryVersion()
			if (!!libraryVersion)
				versionId = libraryVersion.id
				LibraryVersionHttpService.unlock(versionId).then(
					success = (response) ->
						NotificationSrv.add(
							title: "rules.library_version_unlocked_title"
							content: "rules.library_version_unlocked_content"
							content_data:
								version: libraryVersion.name
							bubble:
								show: true
						)

						$state.go("tree.libraries-edit.versions", {libraryId: libraryId, versionId: versionId}, {reload: true})
					error = (response) ->
						throw new Error("Cannot unlock library version")
				)

		enableTestingVersion = () ->
			libraryVersion = LibraryService.getLibraryVersion()
			if (!!libraryVersion)
				versionId = libraryVersion.id
				LibraryVersionHttpService.enableTestingVersion(versionId).then(
					success = (response) ->
						NotificationSrv.add(
							title: "rules.library_version_enabled_testing_title"
							content: "rules.library_version_enabled_testing_content"
							content_data:
								version: libraryVersion.name
							bubble:
								show: true
						)

						$state.go("tree.libraries-edit.versions", {libraryId: libraryId, versionId: versionId}, {reload: true})
					error = (response) ->
						throw new Error("Cannot enable testing library version")
				)

		disableTestingVersion = () ->
			libraryVersion = LibraryService.getLibraryVersion()
			if (!!libraryVersion)
				versionId = libraryVersion.id
				LibraryVersionHttpService.canDisableTestingVersion(versionId).then(
					success = (response) ->
						result = response.data
						if (result.result)
							LibraryVersionHttpService.disableTestingVersion(versionId).then(
								success = (response) ->
									NotificationSrv.add(
										title: "rules.library_version_disabled_testing_title"
										content: "rules.library_version_disabled_testing_content"
										content_data:
											version: libraryVersion.name
										bubble:
											show: true
									)
			
									$state.go("tree.libraries-edit.versions", {libraryId: libraryId, versionId: versionId}, {reload: true})
								error = (response) ->
									throw new Error("Cannot disable testing library version")
							)
						else
							WindowService.openWindow("action.disable_testing", "views/libraries/errorDisableTestingLibraryVersion.html", result)
				)

		finalizeVersion = () ->
			WindowService.openWindow("action.finalize_version", "views/libraries/confirmFinaliseLibraryVersion.html")

		exportVersion = () ->
			libraryVersion = LibraryService.getLibraryVersion()
			if (!!libraryVersion)
				versionId = libraryVersion.id
				xmlUrl = LibraryVersionHttpService.getExportVersionUrl(versionId)
				ticket = JSON.stringify(SecuritySrv.getUser().ticket)
				$window.location = xmlUrl + "?ticket=" + encodeURIComponent(ticket)
				return

		importVersion = () ->
			WindowService.openWindow("action.import_version", "views/libraries/importLibraryVersion.html")

		return this
	])
	.controller('LibraryCreateCtrl', \
		['$window', '$scope', '$state', '$stateParams', 'LibraryHttpService', 'CategoryHttpService', 'QFormValidation', \
		 ($window,   $scope,   $state,   $stateParams,   LibraryHttpService,   CategoryHttpService,   QFormValidation) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		projectId = $stateParams.projectId

		$scope.library =
			active: true

		categories = []
		$scope.categoriesDataSource = new kendo.data.DataSource(
			data: categories
		)

		CategoryHttpService.getByProjectId(projectId).then(
			success = (response) ->
				Array.prototype.push.apply(categories, response.data)
			error = (response) ->
				throw new Error("Cannot fetch categories")
		)

		$scope.create = () ->
			$scope.library.projectId = projectId
			LibraryHttpService.create($scope.library).then(
				success = (result) ->
					NotificationSrv.add(
						title: "rules.library_created_title"
						content: "rules.library_created_content"
						content_data:
							library: $scope.library.name
						bubble:
							show: true
					)

					libraryId = result.data
					$state.go("tree.libraries-edit", {libraryId: libraryId}, {reload: true})
				error = (result) ->
					if (response.status is 400)
						QFormValidation.renderFormErrors($scope, $scope.libraryForm, result)
					else
						throw new Error("Cannot create library")
			)

		$scope.cancel = () ->
			$state.go("tree", {projectId: projectId})

		return this
	])
	.controller('LibraryDeleteCtrl', \
		['$window', '$scope', '$state', '$stateParams', 'LibraryHttpService', 'WindowService', \
		 ($window,   $scope,   $state,   $stateParams,   LibraryHttpService,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		projectId = $stateParams.projectId
		libraryId = $stateParams.libraryId

		$scope.getData = () ->
			return WindowService.getWindow().data

		$scope.delete = () ->
			LibraryHttpService.deleteLibrary(libraryId).then(
				success = (response) ->
					WindowService.closeWindow()
					NotificationSrv.add(
						title: "rules.library_deleted_title"
						content: "rules.library_deleted_content"
						bubble:
							show: true
					)

					$state.go("tree", {projectId: projectId}, {reload: true})
				error = (response) ->
					throw new Error("Cannot delete library")
			)

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
