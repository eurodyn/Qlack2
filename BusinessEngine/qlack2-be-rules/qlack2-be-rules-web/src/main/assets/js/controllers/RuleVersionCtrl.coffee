angular.module("rules")
	.controller("RuleVersionCtrl", \
		["$scope", "$state", "$stateParams", "RuleService", "RuleHttpService", "QDateSrv", \
		 ($scope,   $state,   $stateParams,   RuleService,   RuleHttpService,   QDateSrv) ->

		ruleId = $stateParams.ruleId
		versionId = $stateParams.versionId

		$scope.versionItemTemplate = kendo.template($("#versionItemTemplate").html())

		$scope.versionsDataSource = new kendo.data.DataSource(
			data: []
		)

		$scope.selectVersion = (e) ->
			console.log(e)
			version = e.sender.dataItem(e.item.index())
			$state.go("tree.rules-edit.versions", {ruleId: ruleId, versionId: version.id})

		$scope.ruleVersion = {}

		# resolve after security and main resource
		ruleVersionPromise = RuleService.getRulePromise().then(
			success = (rule) ->
				$scope.versionsDataSource.data(rule.versions)
				return RuleService.initRuleVersion(versionId)
		)

		ruleVersionPromise.then(
			success = (ruleVersion) ->
				RuleService.initActions()

				$scope.canManage = RuleService.getCanManage()
				$scope.canEditVersion = RuleService.getCanEditVersion()

				$scope.ruleVersion = ruleVersion
		)

		ruleVersionPromise.then(
			success = (ruleVersion) ->
				if (ruleVersion.lockedBy)
					$scope.lockedByTooltip =
						lockedby: ruleVersion.lockedBy.firstName + ' ' + ruleVersion.lockedBy.lastName
						lockedon: QDateSrv.localise(ruleVersion.lockedOn, 'lll')
		)

		return this
	])
	.controller("RuleVersionCreateCtrl", \
		["$window", "$scope", "$state", "$stateParams", "RuleService", "RuleHttpService", "QFormValidation", "WindowService", \
		 ($window,   $scope,   $state,   $stateParams,   RuleService,   RuleHttpService,   QFormValidation,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		ruleId = $stateParams.ruleId
		versionId = $stateParams.versionId

		$scope.ruleVersion = { }

		$scope.basedOnListItemTemplate = kendo.template($("#basedOnListItemTemplate").html())

		$scope.versionsDataSource = new kendo.data.DataSource(
			data: []
		)

		# scope-crossing
		RuleService.getRulePromise().then(
			success = (rule) ->
				$scope.versionsDataSource.data(rule.versions)
		)

		$scope.create = () ->
			RuleHttpService.createVersion(ruleId, $scope.ruleVersion).then(
				success = (response) ->
					WindowService.closeWindow()
					NotificationSrv.add(
						title: "rules.rule_version_created_title"
						content: "rules.rule_version_created_content"
						content_data:
							version: $scope.ruleVersion.name
						bubble:
							show: true
					)

					versionId = response.data
					$state.go("tree.rules-edit.versions", {ruleId: ruleId, versionId: versionId}, {reload: true})
				error = (response) ->
					if (response.status is 400)
						QFormValidation.renderFormErrors($scope, $scope.ruleVersionForm, response)
					else
						throw new Error("Cannot create rule version")
			)

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
	.controller("RuleVersionDeleteCtrl", \
		["$window", "$scope", "$state", "$stateParams", "RuleHttpService", "WindowService", \
		 ($window,   $scope,   $state,   $stateParams,   RuleHttpService,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		ruleId = $stateParams.ruleId
		versionId = $stateParams.versionId

		$scope.getData = () ->
			return WindowService.getWindow().data

		$scope.delete = () ->
			RuleHttpService.deleteVersion(versionId).then(
				success = (response) ->
					WindowService.closeWindow()
					NotificationSrv.add(
						title: "rules.rule_version_deleted_title"
						content: "rules.rule_version_deleted_content"
						bubble:
							show: true
					)

					$state.go("tree.rules-edit", {ruleId: ruleId}, {reload: true})
				error = (response) ->
					throw new Error("Cannot delete rule version")
			)

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
	.controller("RuleVersionEnableTestingCtrl", \
		["$scope", "WindowService", \
		 ($scope,   WindowService) ->

		$scope.getData = () ->
			return WindowService.getWindow().data

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
	.controller("RuleVersionDisableTestingCtrl", \
		["$scope", "WindowService", \
		 ($scope,   WindowService) ->

		$scope.getData = () ->
			return WindowService.getWindow().data

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
	.controller("RuleVersionFinalizeCtrl", \
		["$window", "$scope", "$state", "$stateParams", "RuleHttpService", "WindowService", \
		 ($window,   $scope,   $state,   $stateParams,   RuleHttpService,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		ruleId = $stateParams.ruleId
		versionId = $stateParams.versionId

		$scope.getData = () ->
			return WindowService.getWindow().data

		$scope.finalize = () ->
			RuleHttpService.finalizeVersion(versionId).then(
				success = (response) ->
					WindowService.closeWindow()
					NotificationSrv.add(
						title: "rules.rule_version_finalised_title"
						content: "rules.rule_version_finalised_content"
						bubble:
							show: true
					)

					$state.go("tree.rules-edit.versions", {ruleId: ruleId, versionId: versionId}, {reload: true})
				error = (response) ->
					throw new Error("Cannot finalize rule version")
			)

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
	.controller("RuleVersionImportCtrl", \
		["$window", "$scope", "$compile", "$state", "$stateParams", "SecuritySrv", "RuleHttpService", "QFormValidation", "WindowService", \
		 ($window,   $scope,   $compile,   $state,   $stateParams,   SecuritySrv,   RuleHttpService,   QFormValidation,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		ruleId = $stateParams.ruleId

		$scope.ruleVersion = {}

		$scope.attachment = {}

		$scope.getTicket = () ->
			JSON.stringify(SecuritySrv.getUser().ticket)

		prepareVersionForImport = () ->
			if $scope.attachment.flow.files[0]?
				$scope.ruleVersion.file = $scope.attachment.flow.files[0].uniqueIdentifier
			else
				$scope.ruleVersion.file = null

		$scope.import = () ->
			prepareVersionForImport()
			RuleHttpService.importVersion(ruleId, $scope.ruleVersion).then(
				success = (response) ->
					versionId = response.data

					WindowService.closeWindow()
					NotificationSrv.add(
						title: "rules.rule_version_imported_title"
						content: "rules.rule_version_imported_content"
						bubble:
							show: true
					)

					$state.go("tree.rules-edit.versions", {ruleId: ruleId, versionId: versionId}, {reload: true})
				error = (response) ->
					if (response.status is 400)
						QFormValidation.renderFormErrors($scope, $scope.ruleVersionForm, response)
					else
						throw new Error("Cannot import rule version")
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
