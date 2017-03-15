angular.module("rules")
	.controller("WorkingSetVersionCtrl", \
		["$scope", "$compile", "$state", "$stateParams", "$q", "WorkingSetService", "WorkingSetHttpService", "RuleHttpService", "DataModelHttpService", "LibraryHttpService", "Util", "QDateSrv", \
		 ($scope,   $compile,   $state,   $stateParams,   $q,   WorkingSetService,   WorkingSetHttpService,   RuleHttpService,   DataModelHttpService,   LibraryHttpService,   Util,   QDateSrv) ->

		projectId = $stateParams.projectId
		workingSetId = $stateParams.workingSetId
		versionId = $stateParams.versionId

		$scope.versionItemTemplate = kendo.template($("#versionItemTemplate").html())

		$scope.versionsDataSource = new kendo.data.DataSource(
			data: []
		)

		$scope.selectVersion = (e) ->
			console.log(e)
			version = e.sender.dataItem(e.item.index())
			$state.go("tree.working-sets-edit.versions", {workingSetId: workingSetId, versionId: version.id})

		$scope.workingSetVersion = {}

		# resolve after security and main resource
		workingSetVersionPromise = WorkingSetService.getWorkingSetPromise().then(
			success = (workingSet) ->
				$scope.versionsDataSource.data(workingSet.versions)
				return WorkingSetService.initWorkingSetVersion(versionId)
		)

		workingSetVersionPromise.then(
			success = (workingSetVersion) ->
				WorkingSetService.initActions()

				$scope.canManage = WorkingSetService.getCanManage()
				$scope.canEditVersion = WorkingSetService.getCanEditVersion()
				$scope.rebindVal = WorkingSetService.getRebindVal()

				$scope.workingSetVersion = workingSetVersion
		)

		workingSetVersionPromise.then(
			success = (workingSetVersion) ->
				if (workingSetVersion.lockedBy)
					$scope.lockedByTooltip =
						lockedby: workingSetVersion.lockedBy.firstName + ' ' + workingSetVersion.lockedBy.lastName
						lockedon: QDateSrv.localise(workingSetVersion.lockedOn, 'lll')
		)

		# init contained resource versions
		workingSetVersionPromise.then(
			success = (version) ->
				updateVersionDataSources(version)
		)

		updateVersionDataSources = (version) ->
			rulesPromise = RuleHttpService.getByProjectId(projectId).then(
				success = (response) ->
					rules = response.data
					$scope.selectRuleDataSource.data(rules)
			)

			modelsPromise = DataModelHttpService.getByProjectId(projectId).then(
				success = (response) ->
					models = response.data
					$scope.selectModelDataSource.data(models)
			)

			librariesPromise = LibraryHttpService.getByProjectId(projectId).then(
				success = (response) ->
					libraries = response.data
					$scope.selectLibraryDataSource.data(libraries)
			)

			$q.all([rulesPromise, modelsPromise, librariesPromise]).then(
				success = () ->
					setRuleVersionsKendoId(version.rules)
					setModelVersionsKendoId(version.dataModels)
					setLibraryVersionsKendoId(version.libraries)

					updateRuleVersionsDataSource(version.rules)
					updateModelVersionsDataSource(version.dataModels)
					updateLibraryVersionsDataSource(version.libraries)
			)

		setRuleVersionsKendoId = (items) ->
			items.forEach((item) ->
				item.kendoId = Util.generateUUID()

				item.ruleName = getRuleName(item.ruleId)

				item.versionId = item.id
				item.versionName = item.name
			)

		setModelVersionsKendoId = (items) ->
			items.forEach((item) ->
				item.kendoId = Util.generateUUID()

				item.dataModelName = getModelName(item.dataModelId)

				item.versionId = item.id
				item.versionName = item.name
			)

		setLibraryVersionsKendoId = (items) ->
			items.forEach((item) ->
				item.kendoId = Util.generateUUID()

				item.libraryName = getLibraryName(item.libraryId)

				item.versionId = item.id
				item.versionName = item.name
			)

		getRuleName = (ruleId) ->
			rule = $scope.selectRuleDataSource.get(ruleId)
			if (!!rule)
				rule.name
			else
				""

		getModelName = (modelId) ->
			model = $scope.selectModelDataSource.get(modelId)
			if (!!model)
				model.name
			else
				""

		getLibraryName = (libraryId) ->
			library = $scope.selectLibraryDataSource.get(libraryId)
			if (!!library)
				library.name
			else
				""

		### Rules grid ###
		ruleVersions = []

		updateRuleVersionsDataSource = (newRuleVersions) ->
			ruleVersions.pop() while ruleVersions.length > 0
			Array.prototype.push.apply(ruleVersions, newRuleVersions)
			$scope.ruleVersionsDataSource.read()

		$scope.ruleVersionsDataSource = new kendo.data.DataSource(
			data: ruleVersions
			schema:
				model:
					id: "kendoId"
					fields:
						kendoId:
							editable: false
							nullable: false
						ruleId:
							nullable: false
						versionId:
							nullable: false
		)

		$scope.ruleVersionsToolbar = () ->
			toolbar = []

			if ($scope.canManage && $scope.canEditVersion)
				toolbar.push(
					name: "create"
					text: "<span translate>action.add_rule_version</span>"
				)

			return toolbar

		$scope.ruleVersionsColumns = () ->
			columns = [
				{
					field: "ruleId"
					headerTemplate: "<span translate>header.name</span>"
					template: "{{ dataItem.ruleName }}"
					editor: (container, options) ->
						html = "<select kendo-drop-down-list
										k-data-source=\"selectRuleDataSource\"
										k-data-text-field=\"'name'\"
										k-data-value-field=\"'id'\"
										k-option-label=\"'{{'select.rule' | translate}}'\"
										k-on-change=\"selectRule(kendoEvent)\"
										data-bind=\"value: ruleId\">
								</select>"

						$(html).appendTo(container)
				},
				{
					field: "versionId"
					headerTemplate: "<span translate>header.version</span>"
					template: "{{ dataItem.versionName }}"
					editor: (container, options) ->
						html = "<select kendo-drop-down-list
										k-data-source=\"selectRuleVersionDataSource\"
										k-data-text-field=\"'name'\"
										k-data-value-field=\"'id'\"
										k-option-label=\"'{{'select.rule_version' | translate}}'\"
										data-bind=\"value: versionId\">
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

		### Data models grid ###
		modelVersions = []

		updateModelVersionsDataSource = (newModelVersions) ->
			modelVersions.pop() while modelVersions.length > 0
			Array.prototype.push.apply(modelVersions, newModelVersions)
			$scope.modelVersionsDataSource.read()

		$scope.modelVersionsDataSource = new kendo.data.DataSource(
			data: modelVersions
			schema:
				model:
					id: "kendoId"
					fields:
						kendoId:
							editable: false
							nullable: false
						dataModelId:
							nullable: false
						versionId:
							nullable: false
		)

		$scope.modelVersionsToolbar = () ->
			toolbar = []

			if ($scope.canManage && $scope.canEditVersion)
				toolbar.push(
					name: "create"
					text: "<span translate>action.add_data_model_version</span>"
				)

			return toolbar

		$scope.modelVersionsColumns = () ->
			columns = [
				{
					field: "dataModelId"
					headerTemplate: "<span translate>header.name</span>"
					template: "{{ dataItem.dataModelName }}"
					editor: (container, options) ->
						html = "<select kendo-drop-down-list
										k-data-source=\"selectModelDataSource\"
										k-data-text-field=\"'name'\"
										k-data-value-field=\"'id'\"
										k-option-label=\"'{{'select.data_model' | translate}}'\"
										k-on-change=\"selectModel(kendoEvent)\"
										data-bind=\"value: dataModelId\">
								</select>"

						$(html).appendTo(container)
				},
				{
					field: "versionId"
					headerTemplate: "<span translate>header.version</span>"
					template: "{{ dataItem.versionName }}"
					editor: (container, options) ->
						html = "<select kendo-drop-down-list
										k-data-source=\"selectModelVersionDataSource\"
										k-data-text-field=\"'name'\"
										k-data-value-field=\"'id'\"
										k-option-label=\"'{{'select.data_model_version' | translate}}'\"
										data-bind=\"value: versionId\">
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

		### Libraries grid ###
		libraryVersions = []

		updateLibraryVersionsDataSource = (newLibraryVersions) ->
			libraryVersions.pop() while libraryVersions.length > 0
			Array.prototype.push.apply(libraryVersions, newLibraryVersions)
			$scope.libraryVersionsDataSource.read()

		$scope.libraryVersionsDataSource = new kendo.data.DataSource(
			data: libraryVersions
			schema:
				model:
					id: "kendoId"
					fields:
						kendoId:
							editable: false
							nullable: false
						libraryId:
							nullable: false
						versionId:
							nullable: false
		)

		$scope.libraryVersionsToolbar = () ->
			toolbar = []

			if ($scope.canManage && $scope.canEditVersion)
				toolbar.push(
					name: "create"
					text: "<span translate>action.add_library_version</span>"
				)

			return toolbar

		$scope.libraryVersionsColumns = () ->
			columns = [
				{
					field: "libraryId"
					headerTemplate: "<span translate>header.name</span>"
					template: "{{ dataItem.libraryName }}"
					editor: (container, options) ->
						html = "<select kendo-drop-down-list
										k-data-source=\"selectLibraryDataSource\"
										k-data-text-field=\"'name'\"
										k-data-value-field=\"'id'\"
										k-option-label=\"'{{'select.library' | translate}}'\"
										k-on-change=\"selectLibrary(kendoEvent)\"
										data-bind=\"value: libraryId\">
								</select>"

						$(html).appendTo(container)
				}, {
					field: "versionId"
					headerTemplate: "<span translate>header.version</span>"
					template: "{{ dataItem.versionName }}"
					editor: (container, options) ->
						html = "<select kendo-drop-down-list
										k-data-source=\"selectLibraryVersionDataSource\"
										k-data-text-field=\"'name'\"
										k-data-value-field=\"'id'\"
										k-option-label=\"'{{'select.library_version' | translate}}'\"
										data-bind=\"value: versionId\">
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

		### Rules grid editing ###
		$scope.selectRuleDataSource = new kendo.data.DataSource(
			data: []
		)

		$scope.selectRuleVersionDataSource = new kendo.data.DataSource(
			data: []
		)

		findRuleVersionName = (versionId) ->
			version = $scope.selectRuleVersionDataSource.get(versionId)
			if (!!version)
				return version.name
			else
				return null

		$scope.selectRule = (e) ->
			console.log(e)
			ruleId = e.sender.value()
			updateSelectRuleVersionDataSourceOrNull(ruleId)

		updateSelectRuleVersionDataSourceOrNull = (ruleId) ->
			if (!!ruleId)
				updateSelectRuleVersionDataSource(ruleId)
			else
				$scope.selectRuleVersionDataSource.data([])

		updateSelectRuleVersionDataSource = (ruleId) ->
			RuleHttpService.getVersionsByRuleId(ruleId).then(
				success = (response) ->
					ruleVersions = response.data
					$scope.selectRuleVersionDataSource.data(ruleVersions)
			)

		$scope.editRule = (e) ->
			console.log(e)
			selectedRule = e.model

			updateSelectRuleVersionDataSourceOrNull(selectedRule.ruleId)

		$scope.saveRule = (e) ->
			console.log(e)

			selectedRule = e.model

			# set 'id' field for new rows so that kendo does not remove them on edit->cancel
			if (!selectedRule.kendoId)
				selectedRule.kendoId = Util.generateUUID()

			selectedRule.ruleName = getRuleName(selectedRule.ruleId)

			selectedRule.versionName = findRuleVersionName(selectedRule.versionId)

			# sync rules for access from WorkingSetCtrl
			workingSetVersion = WorkingSetService.getWorkingSetVersion()
			workingSetVersion.rules = $scope.ruleVersionsDataSource.data()

		$scope.cancelRule = (e) ->
			console.log(e)

		$scope.removeRule = (e) ->
			console.log(e)

			# sync rules for access from WorkingSetCtrl
			workingSetVersion = WorkingSetService.getWorkingSetVersion()
			workingSetVersion.rules = $scope.ruleVersionsDataSource.data()

		### Data models grid editing ###
		$scope.selectModelDataSource = new kendo.data.DataSource(
			data: []
		)

		$scope.selectModelVersionDataSource = new kendo.data.DataSource(
			data: []
		)

		findModelVersionName = (versionId) ->
			version = $scope.selectModelVersionDataSource.get(versionId)
			if (!!version)
				return version.name
			else
				return null

		$scope.selectModel = (e) ->
			console.log(e)
			modelId = e.sender.value()
			updateSelectModelVersionDataSourceOrNull(modelId)

		updateSelectModelVersionDataSourceOrNull = (modelId) ->
			if (!!modelId)
				updateSelectModelVersionDataSource(modelId)
			else
				$scope.selectModelVersionDataSource.data([])

		updateSelectModelVersionDataSource = (modelId) ->
			DataModelHttpService.getVersionsByModelId(modelId).then(
				success = (response) ->
					modelVersions = response.data
					$scope.selectModelVersionDataSource.data(modelVersions)
			)

		$scope.editDataModel = (e) ->
			console.log(e)
			selectedModel = e.model

			updateSelectModelVersionDataSourceOrNull(selectedModel.dataModelId)

		$scope.saveDataModel = (e) ->
			console.log(e)

			selectedModel = e.model

			# set 'id' field for new rows so that kendo does not remove them on edit->cancel
			if (!selectedModel.kendoId)
				selectedModel.kendoId = Util.generateUUID()

			selectedModel.dataModelName = getModelName(selectedModel.dataModelId)

			selectedModel.versionName = findModelVersionName(selectedModel.versionId)

			# sync data models for access from WorkingSetCtrl
			workingSetVersion = WorkingSetService.getWorkingSetVersion()
			workingSetVersion.dataModels = $scope.modelVersionsDataSource.data()

		$scope.cancelDataModel = (e) ->
			console.log(e)

		$scope.removeDataModel = (e) ->
			console.log(e)

			# sync data models for access from WorkingSetCtrl
			workingSetVersion = WorkingSetService.getWorkingSetVersion()
			workingSetVersion.dataModels = $scope.modelVersionsDataSource.data()

		### Libraries grid editing ###
		$scope.selectLibraryDataSource = new kendo.data.DataSource(
			data: []
		)

		$scope.selectLibraryVersionDataSource = new kendo.data.DataSource(
			data: []
		)

		findLibraryVersionName = (versionId) ->
			version = $scope.selectLibraryVersionDataSource.get(versionId)
			if (!!version)
				return version.name
			else
				return null

		$scope.selectLibrary = (e) ->
			console.log(e)
			libraryId = e.sender.value()
			updateSelectLibraryVersionDataSourceOrNull(libraryId)

		updateSelectLibraryVersionDataSourceOrNull = (libraryId) ->
			if (!!libraryId)
				updateSelectLibraryVersionDataSource(libraryId)
			else
				$scope.selectLibraryVersionDataSource.data([])

		updateSelectLibraryVersionDataSource = (libraryId) ->
			LibraryHttpService.getVersionsByLibraryId(libraryId).then(
				success = (response) ->
					libraryVersions = response.data
					$scope.selectLibraryVersionDataSource.data(libraryVersions)
			)

		$scope.editLibrary = (e) ->
			console.log(e)
			selectedLibrary = e.model

			updateSelectLibraryVersionDataSourceOrNull(selectedLibrary.libraryId)

		$scope.saveLibrary = (e) ->
			console.log(e)

			selectedLibrary = e.model

			# set 'id' field for new rows so that kendo does not remove them on edit->cancel
			if (!selectedLibrary.kendoId)
				selectedLibrary.kendoId = Util.generateUUID()

			selectedLibrary.libraryName = getLibraryName(selectedLibrary.libraryId)

			selectedLibrary.versionName = findLibraryVersionName(selectedLibrary.versionId)

			# sync libraries for access from WorkingSetCtrl
			workingSetVersion = WorkingSetService.getWorkingSetVersion()
			workingSetVersion.libraries = $scope.libraryVersionsDataSource.data()

		$scope.cancelLibrary = (e) ->
			console.log(e)

		$scope.removeLibrary = (e) ->
			console.log(e)

			# sync libraries for access from WorkingSetCtrl
			workingSetVersion = WorkingSetService.getWorkingSetVersion()
			workingSetVersion.libraries = $scope.libraryVersionsDataSource.data()

		### Validation ###
		$scope.ruleVersionsHaveError = false

		$scope.$on('VALIDATION_ERROR_ruleVersionIds', (event, data) ->
			$scope.ruleVersionsHaveError = true
			div = $('div#ruleVersionsGrid div.k-toolbar')
			error = "<i popover='" + data.translation + "' popover-placement='left' popover-trigger='mouseenter' class='fa fa-exclamation-triangle form-control-feedback pull-right'></i>"
			div.prepend($compile(error)($scope))
			return
		)

		$scope.modelVersionsHaveError = false

		$scope.$on('VALIDATION_ERROR_dataModelVersionIds', (event, data) ->
			$scope.modelVersionsHaveError = true
			div = $('div#modelVersionsGrid div.k-toolbar')
			error = "<i popover='" + data.translation + "' popover-placement='left' popover-trigger='mouseenter' class='fa fa-exclamation-triangle form-control-feedback pull-right'></i>"
			div.prepend($compile(error)($scope))
			return
		)

		$scope.libraryVersionsHaveError = false

		$scope.$on('VALIDATION_ERROR_libraryVersionIds', (event, data) ->
			$scope.libraryVersionsHaveError = true
			div = $('div#libraryVersionsGrid div.k-toolbar')
			error = "<i popover='" + data.translation + "' popover-placement='left' popover-trigger='mouseenter' class='fa fa-exclamation-triangle form-control-feedback pull-right'></i>"
			div.prepend($compile(error)($scope))
			return
		)

		return this
	])
	.controller("WorkingSetVersionCreateCtrl", \
		["$window", "$scope", "$state", "$stateParams", "WorkingSetService", "WorkingSetHttpService", "QFormValidation", "WindowService", \
		 ($window,   $scope,   $state,   $stateParams,   WorkingSetService,   WorkingSetHttpService,   QFormValidation,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		workingSetId = $stateParams.workingSetId
		versionId = $stateParams.versionId

		$scope.workingSetVersion = { }

		$scope.basedOnListItemTemplate = kendo.template($("#basedOnListItemTemplate").html())

		$scope.versionsDataSource = new kendo.data.DataSource(
			data: []
		)

		# scope-crossing
		WorkingSetService.getWorkingSetPromise().then(
			success = (workingSet) ->
				$scope.versionsDataSource.data(workingSet.versions)
		)

		$scope.create = () ->
			WorkingSetHttpService.createVersion(workingSetId, $scope.workingSetVersion).then(
				success = (response) ->
					WindowService.closeWindow()
					NotificationSrv.add(
						title: "rules.working_set_version_created_title"
						content: "rules.working_set_version_created_content"
						content_data:
							version: $scope.workingSetVersion.name
						bubble:
							show: true
					)

					versionId = response.data
					$state.go("tree.working-sets-edit.versions", {workingSetId: workingSetId, versionId: versionId}, {reload: true})
				error = (response) ->
					if (response.status is 400)
						QFormValidation.renderFormErrors($scope, $scope.workingSetVersionForm, response)
					else
						throw new Error("Cannot create working set version")
			)

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
	.controller("WorkingSetVersionDeleteCtrl", \
		["$window", "$scope", "$state", "$stateParams", "WorkingSetHttpService", "WindowService", \
		 ($window,   $scope,   $state,   $stateParams,   WorkingSetHttpService,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		workingSetId = $stateParams.workingSetId
		versionId = $stateParams.versionId

		$scope.delete = () ->
			WorkingSetHttpService.deleteVersion(versionId).then(
				success = (response) ->
					WindowService.closeWindow()
					NotificationSrv.add(
						title: "rules.working_set_version_deleted_title"
						content: "rules.working_set_version_deleted_content"
						bubble:
							show: true
					)

					$state.go("tree.working-sets-edit", {workingSetId: workingSetId}, {reload: true})
				error = (response) ->
					throw new Error("Cannot delete working set version")
			)

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
	.controller("WorkingSetVersionUpdateCtrl", \
		["$scope", "WindowService", \
		 ($scope,   WindowService) ->

		$scope.getData = () ->
			return WindowService.getWindow().data

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
	.controller("WorkingSetVersionEnableTestingCtrl", \
		["$window", "$scope", "$state", "$stateParams", "WorkingSetService", "WorkingSetHttpService", "WindowService", \
		 ($window,   $scope,   $state,   $stateParams,   WorkingSetService,   WorkingSetHttpService,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		workingSetId = $stateParams.workingSetId
		versionId = $stateParams.versionId

		$scope.getData = () ->
			return WindowService.getWindow().data

		$scope.enableTesting = () ->
			workingSetVersion = WorkingSetService.getWorkingSetVersion()
			WorkingSetHttpService.enableTestingVersion(versionId).then(
				success = (response) ->
					WindowService.closeWindow()
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

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
	.controller("WorkingSetVersionDisableTestingCtrl", \
		["$scope", "WindowService", \
		 ($scope,   WindowService) ->

		$scope.getData = () ->
			return WindowService.getWindow().data

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
	.controller("WorkingSetVersionFinalizeCtrl", \
		["$window", "$scope", "$state", "$stateParams", "WorkingSetHttpService", "WindowService", \
		 ($window,   $scope,   $state,   $stateParams,   WorkingSetHttpService,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		workingSetId = $stateParams.workingSetId
		versionId = $stateParams.versionId

		$scope.getData = () ->
			return WindowService.getWindow().data

		$scope.finalize = () ->
			WorkingSetHttpService.finalizeVersion(versionId).then(
				success = (response) ->
					WindowService.closeWindow()
					NotificationSrv.add(
						title: "rules.working_set_version_finalised_title"
						content: "rules.working_set_version_finalised_content"
						bubble:
							show: true
					)

					$state.go("tree.working-sets-edit.versions", {workingSetId: workingSetId, versionId: versionId}, {reload: true})
				error = (response) ->
					throw new Error("Cannot finalize working set version")
			)

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
	.controller("WorkingSetVersionDownloadModelsJarCtrl", \
		["$scope", "WindowService", \
		 ($scope,   WindowService) ->

		$scope.getData = () ->
			return WindowService.getWindow().data

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
	.controller("WorkingSetVersionImportCtrl", \
		["$window", "$scope", "$compile", "$state", "$stateParams", "SecuritySrv", "WorkingSetHttpService", "QFormValidation", "WindowService", \
		 ($window,   $scope,   $compile,   $state,   $stateParams,   SecuritySrv,   WorkingSetHttpService,   QFormValidation,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		workingSetId = $stateParams.workingSetId

		$scope.workingSetVersion = {}

		$scope.attachment = {}

		$scope.getTicket = () ->
			JSON.stringify(SecuritySrv.getUser().ticket)

		prepareVersionForImport = () ->
			if $scope.attachment.flow.files[0]?
				$scope.workingSetVersion.file = $scope.attachment.flow.files[0].uniqueIdentifier
			else
				$scope.workingSetVersion.file = null

		$scope.import = () ->
			prepareVersionForImport()
			WorkingSetHttpService.importVersion(workingSetId, $scope.workingSetVersion).then(
				success = (response) ->
					versionId = response.data

					WindowService.closeWindow()
					NotificationSrv.add(
						title: "rules.working_set_version_imported_title"
						content: "rules.working_set_version_imported_content"
						bubble:
							show: true
					)

					$state.go("tree.working-sets-edit.versions", {workingSetId: workingSetId, versionId: versionId}, {reload: true})
				error = (response) ->
					if (response.status is 400)
						QFormValidation.renderFormErrors($scope, $scope.workingSetVersionForm, response)
					else
						throw new Error("Cannot import working set version")
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
