angular.module("rules")
	.controller("DataModelVersionCtrl", \
		["$scope", "$compile", "$state", "$stateParams", "$q", "$timeout", "DataModelService", "DataModelHttpService", "Util", "QDateSrv", \
		 ($scope,   $compile,   $state,   $stateParams,   $q,   $timeout,   DataModelService,   DataModelHttpService,   Util,   QDateSrv) ->

		projectId = $stateParams.projectId
		modelId = $stateParams.modelId
		versionId = $stateParams.versionId

		$scope.versionItemTemplate = kendo.template($("#versionItemTemplate").html())

		$scope.versionsDataSource = new kendo.data.DataSource(
			data: []
		)

		$scope.selectVersion = (e) ->
			console.log(e)
			version = e.sender.dataItem(e.item.index())
			$state.go("tree.models-edit.versions", {modelId: modelId, versionId: version.id})

		$scope.modelVersion = {}

		# resolve after security and main resource
		modelVersionPromise = DataModelService.getDataModelPromise().then(
			success = (model) ->
				$scope.versionsDataSource.data(model.versions)
				return DataModelService.initDataModelVersion(versionId)
		)

		modelVersionPromise.then(
			success = (version) ->
				DataModelService.initActions()

				$scope.canManage = DataModelService.getCanManage()
				$scope.canEditVersion = DataModelService.getCanEditVersion()
				$scope.rebindVal = DataModelService.getRebindVal()

				# init parent model datasources
				parentModelListPromise = fetchParentModelList()
				parentModelVersionListPromise = fetchParentModelVersionList(version.parentModelId)

				# show data model version after init of parent model datasources
				$q.all([parentModelListPromise, parentModelVersionListPromise]).then(
					success = () ->
						# XXX hack to convince angular kendo to set the values of the parent dropdowns
						$timeout(
							() ->
								$scope.modelVersion = DataModelService.getDataModelVersion()
							, 0)
				)
		)

		modelVersionPromise.then(
			success = (modelVersion) ->
				if (modelVersion.lockedBy)
					$scope.lockedByTooltip =
						lockedby: modelVersion.lockedBy.firstName + ' ' + modelVersion.lockedBy.lastName
						lockedon: QDateSrv.localise(modelVersion.lockedOn, 'lll')
		)

		$scope.getDataModel = () ->
			DataModelService.getDataModel()

		# init fields
		modelVersionPromise.then(
			success = (version) ->
				setFieldsKendoId(version.fields)
				updateFieldsDataSource(version.fields)
		)

		# init field types
		modelVersionPromise.then(
			success = () ->
				fetchFieldTypeList()
		)

		### Parent ###
		$scope.parentModelsDataSource = new kendo.data.DataSource(
			data: []
		)

		$scope.parentModelVersionsDataSource = new kendo.data.DataSource(
			data: []
		)

		$scope.selectParent = (e) ->
			console.log(e)
			parentModelId = e.sender.value()
			fetchParentModelVersionList(parentModelId)

		fetchParentModelList = () ->
			return DataModelHttpService.getByProjectId(projectId).then(
				success = (response) ->
					newParentModels = response.data
					$scope.parentModelsDataSource.data(newParentModels)
			)

		fetchParentModelVersionList = (parentModelId) ->
			if (!!parentModelId)
				return DataModelHttpService.getVersionsByModelIdAndFilterCycles(parentModelId, versionId).then(
					success = (response) ->
						newParentModelVersions = response.data
						$scope.parentModelVersionsDataSource.data(newParentModelVersions)
				)
			else
				return $q.when().then(
					success = () ->
						$scope.parentModelVersionsDataSource.data([])
				)

		$scope.parentHasError = false

		$scope.$on('VALIDATION_ERROR_parent', (event, data) ->
			$scope.parentHasError = true
			div = $('#parentModelVersionDiv')
			error = "<i popover='" + data.translation + "' popover-placement='left' popover-trigger='mouseenter' class='fa fa-exclamation-triangle form-control-feedback pull-right'></i>"
			div.prepend($compile(error)($scope))
			return
		)

		### Fields ###
		fields = []

		setFieldsKendoId = (items) ->
			items.forEach((item) ->
				item.kendoId = Util.generateUUID()

				item.fieldId = item.id
			)

		# http://icescrum.eurodyn.com:8080/p/QBE#story/248?tab=comments
		updateFieldsDataSource = (newFields) ->
			fields.pop() while fields.length > 0
			Array.prototype.push.apply(fields, newFields)
			$scope.fieldsDataSource.read()

		$scope.fieldsDataSource = new kendo.data.DataSource(
			data: fields
			schema:
				model:
					id: "kendoId"
					fields:
						kendoId:
							editable: false
							nullable: false
						fieldId:
							editable: false
							defaultValue: null
						name:
							nullable: false

						fieldTypeId: {
						}
						fieldTypeName: {
						}
						fieldTypeVersionId: {
						}
						fieldTypeVersionName: {
						}
		)

		$scope.fieldsToolbar = () ->
			toolbar = []

			if ($scope.canManage && $scope.canEditVersion)
				toolbar.push(
					name: "create"
					text: "<span translate>action.add_field</span>"
				)

			return toolbar

		$scope.fieldsColumns = () ->
			columns = [
				{
					field: "name"
					headerTemplate: "<span translate>header.name</span>"
				},
				{
					field: "fieldTypeName"
					headerTemplate: "<span translate>header.type</span>"
					editor: (container, options) ->
						html = "<select kendo-drop-down-list
										k-data-source=\"fieldTypeDataSource\"
										k-data-text-field=\"'name'\"
										k-data-value-field=\"'id'\"
										k-option-label=\"'{{'select.type' | translate}}'\"
										k-on-change=\"selectFieldType(kendoEvent)\"
										data-bind=\"value: fieldTypeId\" >
								</select>"

						$(html).appendTo(container)
				},
				{
					field: "fieldTypeVersionName"
					headerTemplate: "<span translate>header.version</span>"
					editor: (container, options) ->
						html = "<select kendo-drop-down-list
										k-data-source=\"fieldTypeVersionDataSource\"
										k-data-text-field=\"'name'\"
										k-data-value-field=\"'id'\"
										k-option-label=\"'{{'select.type_version' | translate}}'\"
										data-bind=\"value: fieldTypeVersionId\" >
								</select>"

						$(html).appendTo(container)
				}
			]

			if ($scope.canManage && $scope.canEditVersion)
				columns.push(
					command: [
						{
							name: "edit"
							text:
								edit: "",
								update: ""
								cancel: ""
						}, {
							name: "destroy"
							text: ""
						}
					]
					width: 80
				)

			return columns

		### Field types ###
		$scope.fieldTypeDataSource = new kendo.data.DataSource(
			data: []
		)

		$scope.fieldTypeVersionDataSource = new kendo.data.DataSource(
			data: []
		)

		fetchFieldTypeList = () ->
			primitiveTypesPromise = DataModelHttpService.getFieldTypes().then(
				success = (response) ->
					response.data
			)

			modelTypesPromise = DataModelHttpService.getByProjectId(projectId).then(
				success = (response) ->
					response.data
			)

			$q.all([primitiveTypesPromise, modelTypesPromise]).then(
				success = (results) ->
					types = []

					primitiveTypes = results[0]
					primitiveTypes.forEach((type) ->
						type.type = "primitive"
						types.push(type)
					)

					modelTypes = results[1]
					modelTypes.forEach((type) ->
						type.type = "model"
						types.push(type)
					)

					$scope.fieldTypeDataSource.data(types)
			)

		fetchFieldTypeVersionListOrNull = (fieldTypeId) ->
			if (!!fieldTypeId and isModelType(fieldTypeId))
				fetchFieldTypeVersionList(fieldTypeId)
			else
				$scope.fieldTypeVersionDataSource.data([])

		fetchFieldTypeVersionList = (fieldTypeId) ->
			DataModelHttpService.getVersionsByModelId(fieldTypeId).then(
				success = (response) ->
					fieldTypeVersions = response.data
					$scope.fieldTypeVersionDataSource.data(fieldTypeVersions)
			)

		### Field editing ###
		$scope.selectFieldType = (e) ->
			console.log(e)
			fieldTypeId = e.sender.value()
			fetchFieldTypeVersionListOrNull(fieldTypeId)

		$scope.editField = (e) ->
			console.log(e)

			selectedField = e.model

			fieldTypeId = selectedField.fieldTypeId
			fetchFieldTypeVersionListOrNull(fieldTypeId)

		$scope.saveField = (e) ->
			console.log(e)

			selectedField = e.model

			# set 'id' field for new rows so that kendo does not remove them on edit->cancel
			if (!selectedField.kendoId)
				selectedField.kendoId = Util.generateUUID()

			selectedField.fieldTypeName = findTypeName(selectedField.fieldTypeId)
			selectedField.fieldTypeVersionName = findTypeVersionName(selectedField.fieldTypeVersionId)

			# update fields for access from DataModelCtrl
			modelVersion = DataModelService.getDataModelVersion()
			modelVersion.fields = $scope.fieldsDataSource.data()

		$scope.cancelField = (e) ->
			console.log(e)

		$scope.removeField = (e) ->
			console.log(e)

			# update fields for access from DataModelCtrl
			modelVersion = DataModelService.getDataModelVersion()
			modelVersion.fields = $scope.fieldsDataSource.data()

		isModelType = (fieldTypeId) ->
			fieldType = $scope.fieldTypeDataSource.get(fieldTypeId)
			fieldType.type is "model"

		findTypeName = (fieldTypeId) ->
			fieldType = $scope.fieldTypeDataSource.get(fieldTypeId)
			if (fieldType?)
				fieldType.name
			else
				null

		findTypeVersionName = (fieldTypeVersionId) ->
			if (fieldTypeVersionId?)
				fieldTypeVersion = $scope.fieldTypeVersionDataSource.get(fieldTypeVersionId)
				if (fieldTypeVersion?)
					fieldTypeVersion.name
				else
					null
			else
				null

		$scope.fieldsHaveError = false

		$scope.$on('VALIDATION_ERROR_version.fields', (event, data) ->
			$scope.fieldsHaveError = true
			uid = $scope.fieldsDataSource.at(data.fieldIndex)['uid']
			# translate property for case where we show 'name' instead of 'id'
			propertyName = data.propertyName
			if (propertyName is "fieldTypeId")
				propertyName = "fieldTypeName"
			cell = $('tr[data-uid="' + uid + '"] td span[ng-bind="dataItem.' + propertyName + '"]').parent()
			existingErrors = cell.find('.form-control-feedback').remove()
			error = "<i popover='" + data.translation + "' popover-placement='left' popover-trigger='mouseenter' class='fa fa-exclamation-triangle form-control-feedback pull-right'></i>"
			cell.prepend($compile(error)($scope))
			return
		)

		$scope.$on('VALIDATION_ERROR_fields', (event, data) ->
			$scope.fieldsHaveError = true
			div = $('div#fieldsGrid div.k-toolbar')
			error = "<i popover='" + data.translation + "' popover-placement='left' popover-trigger='mouseenter' class='fa fa-exclamation-triangle form-control-feedback pull-right'></i>"
			div.prepend($compile(error)($scope))
			return
		)

		return this
	])
	.controller("DataModelVersionCreateCtrl", \
		["$window", "$scope", "$state", "$stateParams", "DataModelService", "DataModelHttpService", "QFormValidation", "WindowService", \
		 ($window,   $scope,   $state,   $stateParams,   DataModelService,   DataModelHttpService,   QFormValidation,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		modelId = $stateParams.modelId
		versionId = $stateParams.versionId

		$scope.modelVersion = { }

		$scope.basedOnListItemTemplate = kendo.template($("#basedOnListItemTemplate").html())

		$scope.versionsDataSource = new kendo.data.DataSource(
			data: []
		)

		# scope-crossing
		DataModelService.getDataModelPromise().then(
			success = (model) ->
				$scope.versionsDataSource.data(model.versions)
		)

		$scope.create = () ->
			DataModelHttpService.createVersion(modelId, $scope.modelVersion).then(
				success = (response) ->
					WindowService.closeWindow()
					NotificationSrv.add(
						title: "rules.data_model_version_created_title"
						content: "rules.data_model_version_created_content"
						content_data:
							version: $scope.modelVersion.name
						bubble:
							show: true
					)

					versionId = response.data
					$state.go("tree.models-edit.versions", {modelId: modelId, versionId: versionId}, {reload: true})
				error = (response) ->
					if (response.status is 400)
						QFormValidation.renderFormErrors($scope, $scope.modelVersionForm, response)
					else
						throw new Error("Cannot create model version")
			)

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
	.controller("DataModelVersionUpdateCtrl", \
		["$scope", "WindowService", \
		 ($scope,   WindowService) ->

		$scope.getData = () ->
			return WindowService.getWindow().data

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
	.controller("DataModelVersionDeleteCtrl", \
		["$window", "$scope", "$state", "$stateParams", "DataModelHttpService", "WindowService", \
		 ($window,   $scope,   $state,   $stateParams,   DataModelHttpService,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		modelId = $stateParams.modelId
		versionId = $stateParams.versionId

		$scope.getData = () ->
			return WindowService.getWindow().data

		$scope.delete = () ->
			DataModelHttpService.deleteVersion(versionId).then(
				success = (response) ->
					WindowService.closeWindow()
					NotificationSrv.add(
						title: "rules.data_model_version_deleted_title"
						content: "rules.data_model_version_deleted_content"
						bubble:
							show: true
					)

					$state.go("tree.models-edit", {modelId: modelId}, {reload: true})
				error = (response) ->
					throw new Error("Cannot delete data model version")
			)

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
	.controller("DataModelVersionEnableTestingCtrl", \
		["$window", "$scope", "$state", "$stateParams", "DataModelService", "DataModelHttpService", "WindowService", \
		 ($window,   $scope,   $state,   $stateParams,   DataModelService,   DataModelHttpService,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		modelId = $stateParams.modelId
		versionId = $stateParams.versionId

		$scope.getData = () ->
			return WindowService.getWindow().data

		$scope.enableTesting = () ->
			modelVersion = DataModelService.getDataModelVersion()
			DataModelHttpService.enableTestingVersion(versionId).then(
				success = (response) ->
					WindowService.closeWindow()
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

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
	.controller("DataModelVersionDisableTestingCtrl", \
		["$scope", "WindowService", \
		 ($scope,   WindowService) ->

		$scope.getData = () ->
			return WindowService.getWindow().data

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
	.controller("DataModelVersionFinalizeCtrl", \
		["$window", "$scope", "$state", "$stateParams", "DataModelHttpService", "WindowService", \
		 ($window,   $scope,   $state,   $stateParams,   DataModelHttpService,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		modelId = $stateParams.modelId
		versionId = $stateParams.versionId

		$scope.getData = () ->
			return WindowService.getWindow().data

		$scope.finalize = () ->
			DataModelHttpService.finalizeVersion(versionId).then(
				success = (response) ->
					WindowService.closeWindow()
					NotificationSrv.add(
						title: "rules.data_model_version_finalised_title"
						content: "rules.data_model_version_finalised_content"
						bubble:
							show: true
					)

					$state.go("tree.models-edit.versions", {modelId: modelId, versionId: versionId}, {reload: true})
				error = (response) ->
					throw new Error("Cannot finalize data model version")
			)

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
	.controller("DataModelVersionImportCtrl", \
		["$window", "$scope", "$compile", "$state", "$stateParams", "SecuritySrv", "DataModelHttpService", "QFormValidation", "WindowService", \
		 ($window,   $scope,   $compile,   $state,   $stateParams,   SecuritySrv,   DataModelHttpService,   QFormValidation,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		modelId = $stateParams.modelId

		$scope.modelVersion = {}

		$scope.attachment = {}

		$scope.getTicket = () ->
			JSON.stringify(SecuritySrv.getUser().ticket)

		prepareVersionForImport = () ->
			if $scope.attachment.flow.files[0]?
				$scope.modelVersion.file = $scope.attachment.flow.files[0].uniqueIdentifier
			else
				$scope.modelVersion.file = null

		$scope.import = () ->
			prepareVersionForImport()
			DataModelHttpService.importVersion(modelId, $scope.modelVersion).then(
				success = (response) ->
					versionId = response.data

					WindowService.closeWindow()
					NotificationSrv.add(
						title: "rules.data_model_version_imported_title"
						content: "rules.data_model_version_imported_content"
						bubble:
							show: true
					)

					$state.go("tree.models-edit.versions", {modelId: modelId, versionId: versionId}, {reload: true})
				error = (response) ->
					if (response.status is 400)
						QFormValidation.renderFormErrors($scope, $scope.modelVersionForm, response)
					else
						throw new Error("Cannot import data model version")
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
