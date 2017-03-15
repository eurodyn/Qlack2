angular
	.module("formsManagerApp")
	.controller('FormVersionCtrl', ['$scope', '$compile', '$state', '$stateParams', '$q', 'FormService', 'FormVersionHttpService', 'UtilService', 'ProjectHttpService', 'WindowService', 'QDateSrv', \
			($scope, $compile, $state, $stateParams, $q, FormService, FormVersionHttpService, UtilService, ProjectHttpService, WindowService, QDateSrv) ->

		########################### INITIALISATIONS ###########################
		workingSetsMap = {}
		rulesMap = {}
		conditionTypes = []
		workingSets = []
		rules = []
		conditions = []
		translations = []

		$scope.conditionsHaveError = false
		$scope.translationsHaveError = false
		$scope.translationsErrors = []

		########################### PROMISES ###########################
		conditionTypesPromise = FormVersionHttpService.getConditionTypes()
		conditionTypesPromise.then(
			success = (result) ->
				for conditionTypeNum in result.data
					conditionType = {}
					conditionType.id = conditionTypeNum.toString()
					conditionType.name = "condition_type_" + conditionTypeNum
					conditionTypes.push(conditionType)
			error = () ->
				[]
		)

		workingSetsPromise = ProjectHttpService.getProjectWorkingSets($stateParams.projectId)
		workingSetsPromise.then(
			success = (result) ->
				Array.prototype.push.apply(workingSets, result.data)
				if workingSets?
						workingSetsMap[id] = (workingSetName + " / " + name) for {id, name, workingSetName} in workingSets
			error = () ->
				[]
		)

		rulesPromise = ProjectHttpService.getWorkingSetRules($stateParams.projectId)
		rulesPromise.then(
			success = (result) ->
				Array.prototype.push.apply(rules, result.data)
				if rules?
					rulesMap[id] = (ruleName + " / " + name) for {id, name, ruleName} in rules
			error = () ->
				[]
		)



		#After all promises are resolved, then the form version can be loaded. The required promises are for the loading of
		# the conditionTypes, workingSets and rules. All these data need to be loaded before the form version
		# because otherwise the templates of the conditions grid cannot be resolved correctly.
		$q.all([conditionTypesPromise, workingSetsPromise, rulesPromise]).then((results) ->
			#Ensure that the form is loaded if this state is accessed directly
			FormService.getFormById($stateParams.resourceId, $stateParams.projectId, false).then(
				success = (result) ->
					$scope.versionsDataSource.data(FormService.getForm().formVersions)

					if FormService.getFormLanguages()? && FormService.getFormLanguages()[0]?
						$scope.languagesDataSource.data(FormService.getFormLanguages())
						$scope.selectedLanguageId = FormService.getFormLanguages()[0].id

					FormService.getFormVersionById($stateParams.versionId).then(
						success = (result) ->
							FormService.setActions()

							$scope.formVersion = FormService.getFormVersion()
							$scope.formVersion.lockedOnDate = QDateSrv.localise($scope.formVersion.lockedOn, 'lll')

							$scope.canManageForm = FormService.getCanManageForm()
							$scope.canEditVersion = FormService.getCanEditVersion()
							$scope.rebindVal = FormService.getRebindVal()

							# set 'id' field for new rows so that kendo does not remove them on edit->cancel
							$scope.formVersion.conditions.forEach((item) ->
								item.kendoId = UtilService.createUUID()
								item.conditionId = item.id
							)

							#$scope.conditionsDataSource.data($scope.selectedVersion.conditions) causes the
							#following error: when the user tries to edit a row and clicks on the cancel button,
							#then the row disappears. The problem in this implementation is that the initial data
							#object of the datasource is not affected and when method cancelChanges is called,
							#the datasource is restored to the initial state, which in our case is an empty array.
							#To fix this we should apply changes to the initial data object, by pushing new data
							#into it and calling datasource.read() method. But first we should empty the array from
							#any existing data.
							conditions.pop() while conditions.length > 0
							Array.prototype.push.apply(conditions, $scope.formVersion.conditions)
							$scope.conditionsDataSource.read()

							setParentsDataSource($scope.formVersion.conditions)

							# set 'id' field for new rows so that kendo does not remove them on edit->cancel
							if ($scope.formVersion.translations isnt null)
								$scope.formVersion.translations.forEach((item) ->
									item.kendoId = UtilService.createUUID()
								)

							translations.pop() while translations.length > 0
							Array.prototype.push.apply(translations, $scope.formVersion.translations)
							$scope.translationsDataSource.read()

							return
					)
			)
		)

		########################### DATASOURCES AND KENDO OPTIONS ###########################
		$scope.versionsDataSource =  new kendo.data.DataSource(
			data: []
		)

		$scope.versionsTemplate = kendo.template($("#versionsListTemplate").html())

		$scope.conditionsDataSource = new kendo.data.DataSource(
			data: conditions,
			schema:
				model:
					id: "kendoId"
					fields:
						kendoId: { editable: false, nullable: false }
						conditionId: { editable: false, nullable: false }
						name: { }
						conditionType: { }
						workingSetId: { }
						ruleId: { }
						parentCondition: { defaultValue: null }
		)

		$scope.setConditionsColumns = () ->
			columns = [
				{
					field: "name",
					headerTemplate: "<span translate>condition_name</span>",
					template: "<span data-field-name='dataItem.name'>#=name#</span>",
					width: 80
				},
				{
					field: "conditionType",
					headerTemplate: "<span translate>condition_type</span>"
					width: 60,
					template: "<span data-field-name='dataItem.conditionType' translate>condition_type_#=conditionType#</span>",
					editor: (container, options) ->
						html = "<select kendo-drop-down-list='conditionTypesDropDownList' " +
									   "k-data-text-field=\"'name'\" " +
									   "k-data-value-field=\"'id'\" " +
									   "k-template=\"'<span translate>#=name#</span>'\" " +
									   "k-value-template=\"'<span translate>#=name#</span>'\" " +
									   "k-option-label=\"'{{'select_condition_type' | translate}}'\" " +
									   "k-data-source=\"conditionTypesDataSource\" " +
									   "data-bind='value:" + options.field + "' " +
									   "k-on-change='filterParentsDataSource(kendoEvent)'>" +
								"</select>"

						$(html).appendTo(container)
				},
				{
					field: "workingSetId",
					headerTemplate: "<span translate>condition_working_set</span>"
					width: 60,
					template: "<span data-field-name='dataItem.workingSetId'>{{ getWorkingSetName(dataItem.workingSetId) }}</span>",
					editor: (container, options) ->
						html = "<select id='workingSetsDropDownListId' " +
									   "kendo-drop-down-list='workingSetsDropDownList' " +
									   "k-data-value-field=\"'id'\" " +
									   "k-option-label=\"'{{'select_working_set' | translate}}'\" " +
									   "k-template=\"'<span> #if (data.id != null) {# #=data.workingSetName# / #=data.name# #} else {# #=data# #}# </span>'\" " +
									   "k-value-template=\"'<span> #if (data.id != null) {# #=data.workingSetName# / #=data.name# #} else {# #=data# #}# </span>'\" " +
									   "k-data-source=\"workingSetsDataSource\" " +
									   "data-bind='value:" + options.field + "'>" +
								"</select>"

						$(html).appendTo(container)
				},
				{
					field: "ruleId",
					headerTemplate: "<span translate>condition_rule</span>"
					width: 60,
					template: "<span data-field-name='dataItem.ruleId'>{{ getRuleName(dataItem.ruleId) }}</span>",
					editor: (container, options) ->
						html = "<select id='rulesDropDownListId' " +
									   "kendo-drop-down-list='rulesDropDownList' " +
									   "k-cascade-from=\"'workingSetsDropDownListId'\" " +
									   "k-cascade-from-field=\"'workingSetId'\" " +
									   "k-data-value-field=\"'id'\" " +
									   "k-option-label=\"'{{'select_rule' | translate}}'\" " +
									   "k-template=\"'<span> #if (data.id != null) {# #=data.ruleName# / #=data.name# #} else {# #=data# #}# </span>'\" " +
									   "k-value-template=\"'<span> #if (data.id != null) {# #=data.ruleName# / #=data.name# #} else {# #=data# #}# </span>'\" " +
									   "k-data-source=\"rulesDataSource\" " +
									   "data-bind='value:" + options.field + "'>" +
								"</select>"

						$(html).appendTo(container)
				},
				{
					field: "parentCondition",
					headerTemplate: "<span translate>condition_parent</span>"
					width: 90,
					template: "<span data-field-name='dataItem.parentCondition'>{{ getParentConditionName(dataItem.parentCondition) }}</span>"
					editor: (container, options) ->
						html = "<select kendo-drop-down-list " +
									   "k-data-text-field=\"'name'\" " +
									   "k-data-value-field=\"'id'\" " +
									   "k-option-label=\"'{{'select_parent_condition' | translate}}'\" " +
									   "k-data-source=\"parentsDataSource\" " +
									   "data-bind='value:" + options.field + "'>" +
								"</select>"

						$(html).appendTo(container)
				}
			]

			if $scope.canManageForm && $scope.canEditVersion
				columns.push(
					{
						command: [
							{ name: "edit", text: { edit: "", cancel: "", update: "" } },
							{ name: "destroy", text: "" }
						],
						width: 60
					}
				)

			columns

		$scope.setConditionsToolbar = () ->
			toolbar = []

			if $scope.canManageForm && $scope.canEditVersion
				toolbar.push(
					{name: "create", text: "Add condition"}
				)

			toolbar

		# This datasource cannot be implemented using the transport.read configuration
		# because the conditionTypes should be available before entering the inline editing mode
		# for resolving the label displayed as a template in the conditionType column of the condition's grid.
		$scope.conditionTypesDataSource = new kendo.data.DataSource(
			data: conditionTypes
		)

		# This datasource cannot be implemented using the transport.read configuration
		# because the workingSets should be available before entering the inline editing mode
		# for resolving the label displayed as a template in the workingSet column of the condition's grid.
		$scope.workingSetsDataSource = new kendo.data.DataSource(
			data: workingSets
		)

		# This datasource cannot be implemented using the transport.read configuration
		# because the rules should be available before entering the inline editing mode
		# for resolving the label displayed as a template in the rule column of the condition's grid.
		$scope.rulesDataSource = new kendo.data.DataSource(
			data: rules
		)

		$scope.parentsDataSource = new kendo.data.DataSource(
			data: []
		)

		$scope.translationsDataSource = new kendo.data.DataSource(
			data: translations,
			schema:
				model:
					id: "kendoId"
					fields:
						kendoId:
							editable: false
							nullable: false
						keyId: { },
						key: { },
						value: { },
						language: { }
		)

		# publish tranlations to FormService so that they can shared with CreateFormVersionTranslationsController
		FormService.setTranslations(translations)
		FormService.setTranslationsDataSource($scope.translationsDataSource)

		$scope.setTranslationsColumns = () ->
			columns = [
				{ hidden: true, field: "keyId" },
				{
					field: "key",
					headerTemplate: "<span translate>translation_key</span>",
					width: 90},
				{
					field: "value",
					headerTemplate: "<span translate>translation_value</span>",
					width: 70
				}
			]

			if $scope.canManageForm && $scope.canEditVersion
				columns.push(
					{
						command: [
							{ name: "edit", text: { edit: "", cancel: "", update: "" } },
							{
								name: "delete",
								template: "<button data-ng-click=\'removeTranslations($event)\' class=\'k-button \'><span class=\'k-icon k-delete\'></span></button>"
							}
						],
						width: 50
					}
				)

			columns

		$scope.setTranslationsToolbar = () ->
			toolbar = []

			createItem =
				name: "create"
				template: "<button data-ng-click='addTranslations($event)' class='k-button' translate>create_translations</button>"

			languagesItem =
				name: "languages"
				template: "<div class='pull-right'>" +
							"<span translate>select_language</span>" +
							"<select kendo-drop-down-list " +
									"k-data-text-field=\"'name'\" " +
									"k-data-value-field=\"'id'\" " +
									"k-option-label=\"'{{'select_language' | translate}}'\" " +
									"k-data-source=\"languagesDataSource\" " +
									"k-on-data-bound=\"languagesDataBound(kendoEvent)\" " +
									"k-on-change=\"selectLanguage(kendoEvent)\" " +
									"style=\"width: 100px\">" +
							"</select>" +
						  "</div>"

			if $scope.canManageForm && $scope.canEditVersion
				toolbar.push(createItem, languagesItem)
			else
				toolbar.push(languagesItem)

			return toolbar

		$scope.languagesDataSource = new kendo.data.DataSource(
			data: []
		)

		#The ui-ace option
		$scope.aceOption =
			useWrapMode: true,
			showGutter: false,
			mode: 'xml'

		########################### FORM VERSION FUNCTIONS ###########################
		$scope.selectFormVersion = (e) ->
			versionId = e.sender.dataItem(e.item.index()).id
			console.log "selectFormVersion: " + versionId
			$state.go "resources.form.version",
				resourceId: $stateParams.resourceId
				versionId: versionId

		########################### CONDITIONS GRID FUNCTIONS ###########################
		$scope.getWorkingSetName = (workingSetId) ->
			if workingSetsMap?
				workingSetName = workingSetsMap[workingSetId]
			workingSetName

		$scope.getRuleName = (ruleId) ->
			if rulesMap?
				ruleName = rulesMap[ruleId]
			ruleName

		$scope.getParentConditionName = (parentCondition) ->
			if parentCondition?
				parentConditionName = parentCondition.name
			parentConditionName

		$scope.editRow = (e) ->
			$scope.filterParentsDataSource(e)

		$scope.saveRow = (e) ->
			selectedDataItem = e.model

			# set 'id' field for new rows so that kendo does not remove them on edit->cancel
			if (!selectedDataItem.kendoId)
				selectedDataItem.kendoId = UtilService.createUUID()

			#generate id for newly created rows, in order to allow reference in parent condition's dropdown
			if !selectedDataItem.conditionId
				selectedDataItem.conditionId = UtilService.createUUID()

			#refresh the data in the parents dropdown with the conditions available in the grid
			setParentsDataSource($scope.conditionsDataSource.data())

			#Update the form version conditions in the service to make them available for the FormCtrl
			formVersion = FormService.getFormVersion()
			formVersion.conditions = $scope.conditionsDataSource.data()

			return

		$scope.removeRow = (e) ->
			#refresh the data in the parents dropdown with the conditions available in the grid
			setParentsDataSource($scope.conditionsDataSource.data())

			#Update the form version conditions in the service to make them available for the FormCtrl
			formVersion = FormService.getFormVersion()
			formVersion.conditions = $scope.conditionsDataSource.data()

			return

		setParentsDataSource = (conditions) ->
			parentConditions = []
			for condition in conditions
				if condition?
					parentCondition = {}
					parentCondition.id = condition.conditionId
					parentCondition.name = condition.name
					parentCondition.conditionType = condition.conditionType
					parentConditions.push(parentCondition)

			$scope.parentsDataSource.data(parentConditions)

		$scope.filterParentsDataSource = (e) ->
			senderItem = e.sender

			selectedDataItem = null
			selectedConditionType = null

			if senderItem instanceof kendo.ui.Grid
				selectedDataItem = e.model
				if selectedDataItem? and selectedDataItem.conditionType isnt ''
					selectedConditionType = selectedDataItem.conditionType
			else if senderItem instanceof kendo.ui.DropDownList
				if senderItem.value()? and senderItem.value() isnt ''
					selectedConditionType = parseInt(senderItem.value())

			if selectedConditionType?
				if selectedDataItem?
					$scope.parentsDataSource.filter(
						{
							logic: "and"
							filters: [
								{ field: "conditionType", operator: "eq", value: selectedConditionType },
								{ field: "id", operator: "neq", value: selectedDataItem.conditionId }
							]
						},
						{ field: "name", operator: "neq", value: selectedDataItem.name }
					)
				else
					$scope.parentsDataSource.filter({ field: "conditionType", operator: "eq", value: selectedConditionType })
			else
				$scope.parentsDataSource.filter({ field: "conditionType", operator: "gte", value: 0 })

		### Conditions validation ###
		$scope.$on 'VALIDATION_ERROR_versionConditions', (event, data) ->
			$scope.conditionsHaveError = true
			uid = $scope.conditionsDataSource.at(data.fieldIndex)['uid']
			cell = $('tr[data-uid="' + uid + '"] td span[data-field-name="dataItem.' + data.propertyName + '"]').parent()
			existingErrors = cell.find('.form-control-feedback').remove()
			error = "<i popover='" + data.translation + "' popover-placement='left' popover-trigger='mouseenter' class='fa fa-exclamation-triangle form-control-feedback pull-right'></i>"
			cell.prepend($compile(error)($scope))
			$('#conditionsTab a').addClass('has-error')
			return

		$scope.$on 'VALIDATION_ERROR_conditionCyclicDependency', (event, data) ->
			console.log 'VALIDATION_ERROR_conditionCyclicDependency'
			$scope.conditionsHaveError = true
			div = $('div#conditionsGrid div.k-toolbar')
			error = "<i popover='" + data.translation + "' popover-placement='left' popover-trigger='mouseenter' class='fa fa-exclamation-triangle form-control-feedback pull-right'></i>"
			div.prepend($compile(error)($scope))
			$('#conditionsTab a').addClass('has-error')
			return

		########################### TRANSLATIONS GRID FUNCTIONS ###########################
		$scope.selectLanguage = (e) ->
			senderItem = e.sender
			selectedLanguage = senderItem.value()

			$scope.translationsDataSource.filter({ field: "language", operator: "eq", value: selectedLanguage })

			#In case there exist validation errors show them in all tabs
			for data in $scope.translationsErrors
				$scope.translationsHaveError = true

				uid = $scope.translationsDataSource.at(data.fieldIndex)['uid']
				cell = $('tr[data-uid="' + uid + '"] td span[ng-bind="dataItem.' + data.propertyName + '"]').parent()
				existingErrors = cell.find('.form-control-feedback').remove()
				error = "<i popover='" + data.translation + "' popover-placement='left' popover-trigger='mouseenter' class='fa fa-exclamation-triangle form-control-feedback pull-right'></i>"
				cell.prepend($compile(error)($scope))

				$('#conditionsTab a').addClass('has-error')

		$scope.languagesDataBound = (e) ->
			#Select the first language in the list and trigger
			#the change event to filter the translations grid content

			e.sender.select((dataItem) ->
				dataItem.id == $scope.selectedLanguageId
			)
			e.sender.trigger("change")

		$scope.addTranslations = (e) ->
			#Prevent submission of form
			e.preventDefault()

			WindowService.openWindow("create_translations", "views/forms/createFormVersionTranslations.html")

		$scope.saveTranslationRow = (e) ->
			selectedDataItem = e.model

			data = $scope.translationsDataSource.data()
			for dataItem in data
				if dataItem.keyId == selectedDataItem.keyId
					dataItem.key = selectedDataItem.key

			#Update the form version translations in the service to make them available for the FormCtrl
			formVersion = FormService.getFormVersion()
			formVersion.translations = $scope.translationsDataSource.data()

			for i in [$scope.translationsErrors.length - 1..0] by -1
				translationsError = $scope.translationsErrors[i]
				if translationsError.keyId == selectedDataItem.keyId
					$scope.translationsErrors.splice(i, 1)

			return

		$scope.removeTranslations = (e) ->
			e.preventDefault()

			dataItem = angular.element("#translationsGrid").data("kendoGrid").dataItem(angular.element(e.target).closest("tr"))
			data =
				keyId: dataItem.keyId

			WindowService.openWindow("delete_version", "views/forms/confirmDeleteFormVersionTranslations.html", data)

		### Translations validation ###
		$scope.$on 'VALIDATION_ERROR_versionTranslations', (event, data) ->
			$scope.translationsHaveError = true

			dataItem = $scope.translationsDataSource.at(data.fieldIndex)
			data.keyId = dataItem.keyId
			$scope.translationsErrors.push(data)

			uid = dataItem['uid']
			cell = $('tr[data-uid="' + uid + '"] td span[ng-bind="dataItem.' + data.propertyName + '"]').parent()
			existingErrors = cell.find('.form-control-feedback').remove()
			error = "<i popover='" + data.translation + "' popover-placement='left' popover-trigger='mouseenter' class='fa fa-exclamation-triangle form-control-feedback pull-right'></i>"
			cell.prepend($compile(error)($scope))

			$('#translationsTab a').addClass('has-error')

	])
