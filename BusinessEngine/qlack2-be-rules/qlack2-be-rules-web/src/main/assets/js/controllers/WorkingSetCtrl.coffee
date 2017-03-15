angular.module("rules")
	.controller("WorkingSetEditCtrl", \
		["$window", "$scope", "$state", "$stateParams", "$q", "SecuritySrv", "WorkingSetService", "WorkingSetHttpService", "CategoryHttpService", "QFormValidation", "WindowService", \
		 ($window,   $scope,   $state,   $stateParams,   $q,   SecuritySrv,   WorkingSetService,   WorkingSetHttpService,   CategoryHttpService,   QFormValidation,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		projectId = $stateParams.projectId
		workingSetId = $stateParams.workingSetId

		WorkingSetService.init()

		$scope.workingSetService = WorkingSetService # for watch

		# $scope.canManage = null
		# $scope.canEditVersion = null

		$scope.workingSet = {}

		categories = []
		$scope.categoriesDataSource = new kendo.data.DataSource(
			data: categories
		)

		workingSetPromise = WorkingSetService.initWorkingSetPromise(workingSetId, projectId)

		categoriesPromise = CategoryHttpService.getByProjectId(projectId).then(
			success = (response) ->
				response.data
			error = (response) ->
				throw new Error("Cannot fetch categories")
		)

		$q.all([workingSetPromise, categoriesPromise]).then(
			success = (results) ->
				$scope.canManage = WorkingSetService.getCanManage()

				Array.prototype.push.apply(categories, results[1])

				workingSet = results[0]
				$scope.workingSet = workingSet

				if ($state.current.name is "tree.working-sets-edit")
					if (workingSet.versions.length > 0)
						version = workingSet.versions[0]
						$state.go("tree.working-sets-edit.versions", {workingSetId: workingSetId, versionId: version.id})
					else
						WorkingSetService.initActionsNoVersion()
		)

		$scope.$watch('workingSetService.getCanEditVersion()', (canEditVersion) ->
			$scope.canEditVersion = canEditVersion
		)

		# Update
		$scope.update = () ->
			workingSet = createWorkingSetForUpdate()
			if (workingSet.version?)
				WorkingSetHttpService.canUpdateEnabledForTestingVersion(workingSet.version).then(
					success = (response) ->
						result = response.data
						if (result.result)
							doUpdate(workingSetId, workingSet)
						else
							WindowService.openWindow("action.update", "views/working-sets/version-update-not-allowed.html", result)
				)
			else
				doUpdate(workingSetId, workingSet)

		doUpdate = (workingSetId, workingSet) ->
			WorkingSetHttpService.update(workingSetId, workingSet).then(
				success = (response) ->
					NotificationSrv.add(
						title: "rules.working_set_updated_title"
						content: "rules.working_set_updated_content"
						content_data:
							set: $scope.workingSet.name
						bubble:
							show: true
					)

					if ($state.current.name is "tree.working-sets-edit")
						$state.go("tree.working-sets-edit", {workingSetId: workingSetId}, {reload: true})
					else
						versionId = $stateParams.versionId
						$state.go("tree.working-sets-edit.versions", {workingSetId: workingSetId, versionId: versionId}, {reload: true})
					return
				error = (response) ->
					if (response.status is 400)
						QFormValidation.renderFormErrors($scope, $scope.workingSetForm, response)
					else
						throw new Error("Cannot update working set")
			)

		$scope.cancel = () ->
			$state.go("tree.working-sets-edit", {workingSetId: workingSetId}, {reload: true})

		createWorkingSetForUpdate = () ->
			workingSet =
				projectId: projectId # ignored
				name: $scope.workingSet.name
				description: $scope.workingSet.description
				active: $scope.workingSet.active
				categoryIds: $scope.workingSet.categoryIds

			# scope-crossing
			workingSetVersion = WorkingSetService.getWorkingSetVersion()

			if (workingSetVersion? && workingSetVersion.state != "FINAL")
				workingSet.version =
					id: workingSetVersion.id
					description: workingSetVersion.description
					ruleVersionIds: []
					dataModelVersionIds: []
					libraryVersionIds: []

				for ruleVersion in workingSetVersion.rules
					workingSet.version.ruleVersionIds.push(ruleVersion.versionId)

				for dataModelVersion in workingSetVersion.dataModels
					workingSet.version.dataModelVersionIds.push(dataModelVersion.versionId)

				for libraryVersion in workingSetVersion.libraries
					workingSet.version.libraryVersionIds.push(libraryVersion.versionId)

			return workingSet

		# Actions
		$scope.actionItemTemplate = kendo.template($("#actionItemTemplate").html())

		$scope.actionsDataSource = new kendo.data.DataSource(
			data: []
			sort:
				field: "order"
				dir: "asc"
		)

		$scope.$watchCollection('workingSetService.getActions()', (actions) ->
			$scope.actionsDataSource.data(actions)
		)

		actionCallbacks =
			deleteWorkingSet: () ->
				deleteWorkingSet()
			createVersion: () ->
				createVersion()
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
			downloadModelsJar: () ->
				downloadModelsJar()

		$scope.selectAction = (e) ->
			console.log(e)
			e.preventDefault()
			item = e.sender.dataItem(e.item.index())
			action = actionCallbacks[item.value]
			if (action?)
				action()

		# Delete action
		deleteWorkingSet = () ->
			WorkingSetHttpService.canDeleteWorkingSet(workingSetId).then(
				success = (response) ->
					result = response.data
					if (result.result)
						WindowService.openWindow("title.delete_working_set", "views/working-sets/delete-confirm.html")
					else
						WindowService.openWindow("title.delete_working_set", "views/working-sets/delete-not-allowed.html", result)
			)

		# Version actions
		createVersion = () ->
			WindowService.openWindow("action.create_version", "views/working-sets/version-create.html")

		deleteVersion = () ->
			workingSetVersion = WorkingSetService.getWorkingSetVersion()
			if (!!workingSetVersion)
				versionId = workingSetVersion.id
				WorkingSetHttpService.canDeleteVersion(versionId).then(
					success = (response) ->
						result = response.data
						if (result.result)
							WindowService.openWindow("action.delete_version", "views/working-sets/version-delete-confirm.html")
						else
							WindowService.openWindow("action.delete_version", "views/working-sets/version-delete-not-allowed.html", result)
				)

		lockVersion = () ->
			workingSetVersion = WorkingSetService.getWorkingSetVersion()
			if (!!workingSetVersion)
				versionId = workingSetVersion.id
				WorkingSetHttpService.lockVersion(versionId).then(
					success = (response) ->
						NotificationSrv.add(
							title: "rules.working_set_version_locked_title"
							content: "rules.working_set_version_locked_content"
							content_data:
								version: workingSetVersion.name
							bubble:
								show: true
						)

						$state.go("tree.working-sets-edit.versions", {workingSetId: workingSetId, versionId: versionId}, {reload: true})
					error = (response) ->
						throw new Error("Cannot lock working set version")
				)

		unlockVersion = () ->
			workingSetVersion = WorkingSetService.getWorkingSetVersion()
			if (!!workingSetVersion)
				versionId = workingSetVersion.id
				WorkingSetHttpService.unlockVersion(versionId).then(
					success = (response) ->
						NotificationSrv.add(
							title: "rules.working_set_version_unlocked_title"
							content: "rules.working_set_version_unlocked_content"
							content_data:
								version: workingSetVersion.name
							bubble:
								show: true
						)

						$state.go("tree.working-sets-edit.versions", {workingSetId: workingSetId, versionId: versionId}, {reload: true})
					error = (response) ->
						throw new Error("Cannot unlock working set version")
				)

		enableTestingVersion = () ->
			workingSetVersion = WorkingSetService.getWorkingSetVersion()
			if (!!workingSetVersion)
				versionId = workingSetVersion.id
				WorkingSetHttpService.canEnableTestingVersion(versionId).then(
					success = (response) ->
						result = response.data
						if (result.result)
							if (!result.cascade)
								WorkingSetHttpService.enableTestingVersion(versionId).then(
									success = (response) ->
										NotificationSrv.add(
											title: "rules.working_set_version_enabled_testing_title"
											content: "rules.working_set_version_enabled_testing_content"
											content_data:
												version: workingSetVersion.name
											bubble:
												show: true
										)

										$state.go("tree.working-sets-edit.versions", {workingSetId: workingSetId, versionId: versionId}, {reload: true})
									error = (response) ->
										throw new Error("Cannot enable testing working set version")
								)
							else
								WindowService.openWindow("action.enable_testing", "views/working-sets/version-enable-testing-confirm.html", result)
						else
							WindowService.openWindow("action.enable_testing", "views/working-sets/version-enable-testing-not-allowed.html", result)
				)

		disableTestingVersion = () ->
			workingSetVersion = WorkingSetService.getWorkingSetVersion()
			if (!!workingSetVersion)
				versionId = workingSetVersion.id
				WorkingSetHttpService.canDisableTestingVersion(versionId).then(
					success = (response) ->
						result = response.data
						if (result.result)
							WorkingSetHttpService.disableTestingVersion(versionId).then(
								success = (response) ->
									NotificationSrv.add(
										title: "rules.working_set_version_disabled_testing_title"
										content: "rules.working_set_version_disabled_testing_content"
										content_data:
											version: workingSetVersion.name
										bubble:
											show: true
									)
			
									$state.go("tree.working-sets-edit.versions", {workingSetId: workingSetId, versionId: versionId}, {reload: true})
								error = (response) ->
									throw new Error("Cannot disable testing working set version")
							)
						else
							WindowService.openWindow("action.disable_testing", "views/working-sets/version-disable-testing-not-allowed.html", result)
				)

		finalizeVersion = () ->
			workingSetVersion = WorkingSetService.getWorkingSetVersion()
			if (!!workingSetVersion)
				versionId = workingSetVersion.id
				WorkingSetHttpService.canFinalizeVersion(versionId).then(
					success = (response) ->
						result = response.data
						if (result.result)
							WindowService.openWindow("action.finalize_version", "views/working-sets/version-finalize-confirm.html", result)
						else
							WindowService.openWindow("action.finalize_version", "views/working-sets/version-finalize-not-allowed.html", result)
				)

		downloadModelsJar = () ->
			workingSetVersion = WorkingSetService.getWorkingSetVersion()
			if (!!workingSetVersion)
				versionId = workingSetVersion.id
				WorkingSetHttpService.canGetDataModelsJar(versionId).then(
					success = (response) ->
						result = response.data
						if (result.result)
							jarUrl = WorkingSetHttpService.getDataModelsJarUrl(versionId)
							ticket = JSON.stringify(SecuritySrv.getUser().ticket)
							$window.location = jarUrl + "?ticket=" + encodeURIComponent(ticket)
							return
						else
							WindowService.openWindow("action.download_models_jar", "views/working-sets/version-download-models-jar-not-allowed.html", result)
				)

		exportVersion = () ->
			workingSetVersion = WorkingSetService.getWorkingSetVersion()
			if (!!workingSetVersion)
				versionId = workingSetVersion.id
				xmlUrl = WorkingSetHttpService.getExportVersionUrl(versionId)
				ticket = JSON.stringify(SecuritySrv.getUser().ticket)
				$window.location = xmlUrl + "?ticket=" + encodeURIComponent(ticket)
				return

		importVersion = () ->
			WindowService.openWindow("action.import_version", "views/working-sets/version-import.html")

		return this
	])
	.controller("WorkingSetCreateCtrl", \
		["$window", "$scope", "$state", "$stateParams", "WorkingSetHttpService", "CategoryHttpService", "QFormValidation", \
		 ($window,   $scope,   $state,   $stateParams,   WorkingSetHttpService,   CategoryHttpService,   QFormValidation) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		projectId = $stateParams.projectId

		$scope.workingSet =
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
			WorkingSetHttpService.create(projectId, $scope.workingSet).then(
				success = (response) ->
					NotificationSrv.add(
						title: "rules.working_set_created_title"
						content: "rules.working_set_created_content"
						content_data:
							set: $scope.workingSet.name
						bubble:
							show: true
					)

					workingSetId = response.data
					$state.go("tree.working-sets-edit", {workingSetId: workingSetId}, {reload: true})
				error = (response) ->
					if (response.status is 400)
						QFormValidation.renderFormErrors($scope, $scope.workingSetForm, response)
					else
						throw new Error("Cannot create working set")
			)

		$scope.cancel = () ->
			$state.go("tree", {projectId: projectId})

		return this
	])
	.controller("WorkingSetDeleteCtrl", \
		["$window", "$scope", "$state", "$stateParams", "WorkingSetHttpService", "WindowService", \
		 ($window,   $scope,   $state,   $stateParams,   WorkingSetHttpService,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		projectId = $stateParams.projectId
		workingSetId = $stateParams.workingSetId

		$scope.getData = () ->
			return WindowService.getWindow().data

		$scope.delete = () ->
			WorkingSetHttpService.deleteWorkingSet(workingSetId).then(
				success = (response) ->
					WindowService.closeWindow()
					NotificationSrv.add(
						title: "rules.working_set_deleted_title"
						content: "rules.working_set_deleted_content"
						bubble:
							show: true
					)

					$state.go("tree", {projectId: projectId}, {reload: true})
				error = (response) ->
					throw new Error("Cannot delete working set")
			)

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])

