angular.module("rules")
	.controller("DataModelEditCtrl", \
		["$window", "$scope", "$state", "$stateParams", "$q", "SecuritySrv", "DataModelService", "DataModelHttpService", "CategoryHttpService", "QFormValidation", "WindowService", \
		 ($window,   $scope,   $state,   $stateParams,   $q,   SecuritySrv,   DataModelService,   DataModelHttpService,   CategoryHttpService,   QFormValidation,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		projectId = $stateParams.projectId
		modelId = $stateParams.modelId

		DataModelService.init()

		$scope.dataModelService = DataModelService # for watch

		# $scope.canManage = null
		# $scope.canEditVersion = null

		$scope.model = {}

		categories = []
		$scope.categoriesDataSource = new kendo.data.DataSource(
			data: categories
		)

		modelPromise = DataModelService.initDataModelPromise(modelId, projectId)

		categoriesPromise = CategoryHttpService.getByProjectId(projectId).then(
			success = (response) ->
				response.data
			error = (response) ->
				throw new Error("Cannot fetch categories")
		)

		$q.all([modelPromise, categoriesPromise]).then(
			success = (results) ->
				$scope.canManage = DataModelService.getCanManage()

				Array.prototype.push.apply(categories, results[1])

				model = results[0]
				$scope.model = model

				if ($state.current.name is "tree.models-edit")
					if (model.versions.length > 0)
						version = model.versions[0]
						$state.go("tree.models-edit.versions", {modelId: modelId, versionId: version.id})
					else
						DataModelService.initActionsNoVersion()
		)

		$scope.$watch('dataModelService.getCanEditVersion()', (canEditVersion) ->
			$scope.canEditVersion = canEditVersion
		)

		# Update
		$scope.update = () ->
			model = createModelForUpdate()
			if (model.version?)
				DataModelHttpService.canUpdateEnabledForTestingVersion(model.version).then(
					success = (response) ->
						result = response.data
						if (result.result)
							doUpdate(modelId, model)
						else
							WindowService.openWindow("action.update", "views/models/version-update-not-allowed.html", result)
				)
			else
				doUpdate(modelId, model)

		doUpdate = (modelId, model) ->
			DataModelHttpService.update(modelId, model).then(
				success = (response) ->
					NotificationSrv.add(
						title: "rules.data_model_updated_title"
						content: "rules.data_model_updated_content"
						content_data:
							model: $scope.model.name
						bubble:
							show: true
					)

					if ($state.current.name is "tree.models-edit")
						$state.go("tree.models-edit", {modelId: modelId}, {reload: true})
					else
						versionId = $stateParams.versionId
						$state.go("tree.models-edit.versions", {modelId: modelId, versionId: versionId}, {reload: true})
					return
				error = (response) ->
					if (response.status is 400)
						QFormValidation.renderFormErrors($scope, $scope.modelForm, response)
					else
						throw new Error("Cannot update data model")
			)

		$scope.cancel = () ->
			$state.go("tree.models-edit", {modelId: modelId}, {reload: true})

		createModelForUpdate = () ->
			model =
				projectId: projectId # ignored
				name: $scope.model.name
				description: $scope.model.description
				active: $scope.model.active
				categoryIds: $scope.model.categoryIds

			# scope-crossing
			modelVersion = DataModelService.getDataModelVersion()

			if (modelVersion? && modelVersion.state != "FINAL")
				model.version =
					id: modelVersion.id
					description: modelVersion.description
					modelPackage: modelVersion.modelPackage
					parentModelVersionId: modelVersion.parentModelVersionId
					fields: []

				for versionField in modelVersion.fields
					field =
						id: versionField.fieldId
						name: versionField.name
						fieldTypeId: versionField.fieldTypeId
						fieldTypeVersionId: versionField.fieldTypeVersionId

					model.version.fields.push(field)

			return model

		# Actions
		$scope.actionItemTemplate = kendo.template($("#actionItemTemplate").html())

		$scope.actionsDataSource = new kendo.data.DataSource(
			data: []
			sort:
				field: "order"
				dir: "asc"
		)

		$scope.$watchCollection('dataModelService.getActions()', (actions) ->
			$scope.actionsDataSource.data(actions)
		)

		actionCallbacks =
			deleteModel: () ->
				deleteModel()
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

		$scope.selectAction = (e) ->
			console.log(e)
			e.preventDefault()
			item = e.sender.dataItem(e.item.index())
			action = actionCallbacks[item.value]
			if (action?)
				action()

		# Delete action
		deleteModel = () ->
			DataModelHttpService.canDeleteModel(modelId).then(
				success = (response) ->
					result = response.data
					if (result.result)
						WindowService.openWindow("title.delete_data_model", "views/models/delete-confirm.html")
					else
						WindowService.openWindow("title.delete_data_model", "views/models/delete-not-allowed.html", result)
			)

		# Version actions
		createVersion = () ->
			WindowService.openWindow("action.create_version", "views/models/version-create.html")

		deleteVersion = () ->
			modelVersion = DataModelService.getDataModelVersion()
			if (!!modelVersion)
				versionId = modelVersion.id
				DataModelHttpService.canDeleteVersion(versionId).then(
					success = (response) ->
						result = response.data
						if (result.result)
							WindowService.openWindow("action.delete_version", "views/models/version-delete-confirm.html")
						else
							WindowService.openWindow("action.delete_version", "views/models/version-delete-not-allowed.html", result)
				)

		lockVersion = () ->
			modelVersion = DataModelService.getDataModelVersion()
			if (!!modelVersion)
				versionId = modelVersion.id
				DataModelHttpService.lockVersion(versionId).then(
					success = (response) ->
						NotificationSrv.add(
							title: "rules.data_model_version_locked_title"
							content: "rules.data_model_version_locked_content"
							content_data:
								version: modelVersion.name
							bubble:
								show: true
						)

						$state.go("tree.models-edit.versions", {modelId: modelId, versionId: versionId}, {reload: true})
					error = (response) ->
						throw new Error("Cannot lock data model version")
				)

		unlockVersion = () ->
			modelVersion = DataModelService.getDataModelVersion()
			if (!!modelVersion)
				versionId = modelVersion.id
				DataModelHttpService.unlockVersion(versionId).then(
					success = (response) ->
						NotificationSrv.add(
							title: "rules.data_model_version_unlocked_title"
							content: "rules.data_model_version_unlocked_content"
							content_data:
								version: modelVersion.name
							bubble:
								show: true
						)

						$state.go("tree.models-edit.versions", {modelId: modelId, versionId: versionId}, {reload: true})
					error = (response) ->
						throw new Error("Cannot unlock data model version")
				)

		enableTestingVersion = () ->
			modelVersion = DataModelService.getDataModelVersion()
			if (!!modelVersion)
				versionId = modelVersion.id
				DataModelHttpService.canEnableTestingVersion(versionId).then(
					success = (response) ->
						result = response.data
						if (result.result)
							if (!result.cascade)
								DataModelHttpService.enableTestingVersion(versionId).then(
									success = (response) ->
										NotificationSrv.add(
											title: "rules.data_model_version_enabled_testing_title"
											content: "rules.data_model_version_enabled_testing_content"
											content_data:
												version: modelVersion.name
											bubble:
												show: true
										)

										$state.go("tree.models-edit.versions", {modelId: modelId, versionId: versionId}, {reload: true})
									error = (response) ->
										throw new Error("Cannot enable testing data model version")
								)
							else
								WindowService.openWindow("action.enable_testing", "views/models/version-enable-testing-confirm.html", result)
						else
							WindowService.openWindow("action.enable_testing", "views/models/version-enable-testing-not-allowed.html")
				)

		disableTestingVersion = () ->
			modelVersion = DataModelService.getDataModelVersion()
			if (!!modelVersion)
				versionId = modelVersion.id
				DataModelHttpService.canDisableTestingVersion(versionId).then(
					success = (response) ->
						result = response.data
						if (result.result)
							DataModelHttpService.disableTestingVersion(versionId).then(
								success = (response) ->
									NotificationSrv.add(
										title: "rules.data_model_version_disabled_testing_title"
										content: "rules.data_model_version_disabled_testing_content"
										content_data:
											version: modelVersion.name
										bubble:
											show: true
									)

									$state.go("tree.models-edit.versions", {modelId: modelId, versionId: versionId}, {reload: true})
								error = (response) ->
									throw new Error("Cannot disable testing data model version")
							)
						else
							WindowService.openWindow("action.disable_testing", "views/models/version-disable-testing-not-allowed.html", result)
				)

		finalizeVersion = () ->
			modelVersion = DataModelService.getDataModelVersion()
			if (!!modelVersion)
				versionId = modelVersion.id
				DataModelHttpService.canFinalizeVersion(versionId).then(
					success = (response) ->
						result = response.data
						if (result.result)
							WindowService.openWindow("action.finalize_version", "views/models/version-finalize-confirm.html", result)
						else
							WindowService.openWindow("action.finalize_version", "views/models/version-finalize-not-allowed.html")
				)

		exportVersion = () ->
			modelVersion = DataModelService.getDataModelVersion()
			if (!!modelVersion)
				versionId = modelVersion.id
				xmlUrl = DataModelHttpService.getExportVersionUrl(versionId)
				ticket = JSON.stringify(SecuritySrv.getUser().ticket)
				$window.location = xmlUrl + "?ticket=" + encodeURIComponent(ticket)
				return

		importVersion = () ->
			WindowService.openWindow("action.import_version", "views/models/version-import.html")

		return this
	])
	.controller("DataModelCreateCtrl", \
		["$window", "$scope", "$state", "$stateParams", "DataModelHttpService", "CategoryHttpService", "QFormValidation", \
		 ($window,   $scope,   $state,   $stateParams,   DataModelHttpService,   CategoryHttpService,   QFormValidation) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		projectId = $stateParams.projectId

		$scope.model =
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
			DataModelHttpService.create(projectId, $scope.model).then(
				success = (response) ->
					NotificationSrv.add(
						title: "rules.data_model_created_title"
						content: "rules.data_model_created_content"
						content_data:
							model: $scope.model.name
						bubble:
							show: true
					)

					modelId = response.data
					$state.go("tree.models-edit", {modelId: modelId}, {reload: true})
				error = (response) ->
					if (response.status is 400)
						QFormValidation.renderFormErrors($scope, $scope.modelForm, response)
					else
						throw new Error("Cannot create model")
			)

		$scope.cancel = () ->
			$state.go("tree", {projectId: projectId})

		return this
	])
	.controller("DataModelDeleteCtrl", \
		["$window", "$scope", "$state", "$stateParams", "DataModelHttpService", "WindowService", \
		 ($window,   $scope,   $state,   $stateParams,   DataModelHttpService,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		projectId = $stateParams.projectId
		modelId = $stateParams.modelId

		$scope.getData = () ->
			return WindowService.getWindow().data

		$scope.delete = () ->
			DataModelHttpService.deleteModel(modelId).then(
				success = (response) ->
					WindowService.closeWindow()
					NotificationSrv.add(
						title: "rules.data_model_deleted_title"
						content: "rules.data_model_deleted_content"
						bubble:
							show: true
					)

					$state.go("tree", {projectId: projectId}, {reload: true})
				error = (response) ->
					throw new Error("Cannot delete data model")
			)

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
