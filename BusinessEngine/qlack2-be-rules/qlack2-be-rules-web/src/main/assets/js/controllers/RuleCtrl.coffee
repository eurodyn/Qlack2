angular.module("rules")
	.controller("RuleEditCtrl", \
		["$window", "$scope", "$state", "$stateParams", "$q", "SecuritySrv", "RuleService", "RuleHttpService", "CategoryHttpService", "QFormValidation", "WindowService", \
		 ($window,   $scope,   $state,   $stateParams,   $q,   SecuritySrv ,  RuleService,   RuleHttpService,   CategoryHttpService,   QFormValidation,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		projectId = $stateParams.projectId
		ruleId = $stateParams.ruleId

		RuleService.init()

		$scope.ruleService = RuleService # for watch

		# $scope.canManage = null
		# $scope.canEditVersion = null

		$scope.rule = {}

		categories = []
		$scope.categoriesDataSource = new kendo.data.DataSource(
			data: categories
		)

		rulePromise = RuleService.initRulePromise(ruleId, projectId)

		categoriesPromise = CategoryHttpService.getByProjectId(projectId).then(
			success = (response) ->
				response.data
			error = (response) ->
				throw new Error("Cannot fetch categories")
		)

		$q.all([rulePromise, categoriesPromise]).then(
			success = (results) ->
				$scope.canManage = RuleService.getCanManage()

				Array.prototype.push.apply(categories, results[1])

				rule = results[0]
				$scope.rule = rule

				if ($state.current.name is "tree.rules-edit")
					if (rule.versions.length > 0)
						version = rule.versions[0]
						$state.go("tree.rules-edit.versions", {ruleId: ruleId, versionId: version.id})
					else
						RuleService.initActionsNoVersion()
		)

		$scope.$watch('ruleService.getCanEditVersion()', (canEditVersion) ->
			$scope.canEditVersion = canEditVersion
		)

		# Update
		$scope.update = () ->
			rule = createRuleForUpdate()
			RuleHttpService.update(ruleId, rule).then(
				success = (response) ->
					NotificationSrv.add(
						title: "rules.rule_updated_title"
						content: "rules.rule_updated_content"
						content_data:
							rule: $scope.rule.name
						bubble:
							show: true
					)

					if ($state.current.name is "tree.rules-edit")
						$state.go("tree.rules-edit", {ruleId: ruleId}, {reload: true})
					else
						versionId = $stateParams.versionId
						$state.go("tree.rules-edit.versions", {ruleId: ruleId, versionId: versionId}, {reload: true})
					return
				error = (response) ->
					if (response.status is 400)
						QFormValidation.renderFormErrors($scope, $scope.ruleForm, response)
					else
						throw new Error("Cannot update rule")
			)

		$scope.cancel = () ->
			$state.go("tree.rules-edit", {ruleId: ruleId}, {reload: true})

		createRuleForUpdate = () ->
			rule =
				projectId: projectId # ignored
				name: $scope.rule.name
				description: $scope.rule.description
				active: $scope.rule.active
				categoryIds: $scope.rule.categoryIds

			# scope-crossing
			ruleVersion = RuleService.getRuleVersion()

			if (ruleVersion? && ruleVersion.state != "FINAL")
				rule.version =
					id: ruleVersion.id
					description: ruleVersion.description
					content: ruleVersion.content

			return rule

		# Actions
		$scope.actionItemTemplate = kendo.template($("#actionItemTemplate").html())

		$scope.actionsDataSource = new kendo.data.DataSource(
			data: []
			sort:
				field: "order"
				dir: "asc"
		)

		$scope.$watchCollection('ruleService.getActions()', (actions) ->
			$scope.actionsDataSource.data(actions)
		)

		actionCallbacks =
			deleteRule: () ->
				deleteRule()
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
		deleteRule = () ->
			RuleHttpService.canDeleteRule(ruleId).then(
				success = (response) ->
					result = response.data
					if (result.result)
						WindowService.openWindow("title.delete_rule", "views/rules/delete-confirm.html")
					else
						WindowService.openWindow("title.delete_rule", "views/rules/delete-not-allowed.html", result)
			)

		# Version actions
		createVersion = () ->
			WindowService.openWindow("action.create_version", "views/rules/version-create.html")

		deleteVersion = () ->
			ruleVersion = RuleService.getRuleVersion()
			if (!!ruleVersion)
				versionId = ruleVersion.id
				RuleHttpService.canDeleteVersion(versionId).then(
					success = (response) ->
						result = response.data
						if (result.result)
							WindowService.openWindow("action.delete_version", "views/rules/version-delete-confirm.html")
						else
							WindowService.openWindow("action.delete_version", "views/rules/version-delete-not-allowed.html", result)
				)

		lockVersion = () ->
			ruleVersion = RuleService.getRuleVersion()
			if (!!ruleVersion)
				versionId = ruleVersion.id
				RuleHttpService.lockVersion(versionId).then(
					success = (response) ->
						NotificationSrv.add(
							title: "rules.rule_version_locked_title"
							content: "rules.rule_version_locked_content"
							content_data:
								version: ruleVersion.name
							bubble:
								show: true
						)

						$state.go("tree.rules-edit.versions", {ruleId: ruleId, versionId: versionId}, {reload: true})
					error = (response) ->
						throw new Error("Cannot lock rule version")
				)

		unlockVersion = () ->
			ruleVersion = RuleService.getRuleVersion()
			if (!!ruleVersion)
				versionId = ruleVersion.id
				RuleHttpService.unlockVersion(versionId).then(
					success = (response) ->
						NotificationSrv.add(
							title: "rules.rule_version_unlocked_title"
							content: "rules.rule_version_unlocked_content"
							content_data:
								version: ruleVersion.name
							bubble:
								show: true
						)

						$state.go("tree.rules-edit.versions", {ruleId: ruleId, versionId: versionId}, {reload: true})
					error = (response) ->
						throw new Error("Cannot unlock rule version")
				)

		enableTestingVersion = () ->
			ruleVersion = RuleService.getRuleVersion()
			if (!!ruleVersion)
				versionId = ruleVersion.id
				RuleHttpService.canEnableTestingVersion(versionId).then(
					success = (response) ->
						result = response.data
						if (result.result)
							RuleHttpService.enableTestingVersion(versionId).then(
								success = (response) ->
									NotificationSrv.add(
										title: "rules.rule_version_enabled_testing_title"
										content: "rules.rule_version_enabled_testing_content"
										content_data:
											version: ruleVersion.name
										bubble:
											show: true
									)

									$state.go("tree.rules-edit.versions", {ruleId: ruleId, versionId: versionId}, {reload: true})
								error = (response) ->
									throw new Error("Cannot enable testing rule version")
							)
						else
							WindowService.openWindow("action.enable_testing", "views/rules/version-enable-testing-not-allowed.html", result)
				)

		disableTestingVersion = () ->
			ruleVersion = RuleService.getRuleVersion()
			if (!!ruleVersion)
				versionId = ruleVersion.id
				RuleHttpService.canDisableTestingVersion(versionId).then(
					success = (response) ->
						result = response.data
						if (result.result)
							RuleHttpService.disableTestingVersion(versionId).then(
								success = (response) ->
									NotificationSrv.add(
										title: "rules.rule_version_disabled_testing_title"
										content: "rules.rule_version_disabled_testing_content"
										content_data:
											version: ruleVersion.name
										bubble:
											show: true
									)
			
									$state.go("tree.rules-edit.versions", {ruleId: ruleId, versionId: versionId}, {reload: true})
								error = (response) ->
									throw new Error("Cannot disable testing rule version")
							)
						else
							WindowService.openWindow("action.disable_testing", "views/rules/version-disable-testing-not-allowed.html", result)
				)

		finalizeVersion = () ->
			ruleVersion = RuleService.getRuleVersion()
			if (!!ruleVersion)
				versionId = ruleVersion.id
				RuleHttpService.canFinalizeVersion(versionId).then(
					success = (response) ->
						result = response.data
						if (result.result)
							WindowService.openWindow("action.finalize_version", "views/rules/version-finalize-confirm.html", result)
						else
							WindowService.openWindow("action.finalize_version", "views/rules/version-finalize-not-allowed.html", result)
				)

		exportVersion = () ->
			ruleVersion = RuleService.getRuleVersion()
			if (!!ruleVersion)
				versionId = ruleVersion.id
				xmlUrl = RuleHttpService.getExportVersionUrl(versionId)
				ticket = JSON.stringify(SecuritySrv.getUser().ticket)
				$window.location = xmlUrl + "?ticket=" + encodeURIComponent(ticket)
				return

		importVersion = () ->
			WindowService.openWindow("action.import_version", "views/rules/version-import.html")

		return this
	])
	.controller("RuleCreateCtrl", \
		["$window", "$scope", "$state", "$stateParams", "RuleHttpService", "CategoryHttpService", "QFormValidation", \
		 ($window,   $scope,   $state,   $stateParams,   RuleHttpService,   CategoryHttpService,   QFormValidation) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		projectId = $stateParams.projectId

		$scope.rule =
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
			RuleHttpService.create(projectId, $scope.rule).then(
				success = (response) ->
					NotificationSrv.add(
						title: "rules.rule_created_title"
						content: "rules.rule_created_content"
						content_data:
							rule: $scope.rule.name
						bubble:
							show: true
					)

					ruleId = response.data
					$state.go("tree.rules-edit", {ruleId: ruleId}, {reload: true})
				error = (response) ->
					if (response.status is 400)
						QFormValidation.renderFormErrors($scope, $scope.ruleForm, response)
					else
						throw new Error("Cannot create rule")
			)

		$scope.cancel = () ->
			$state.go("tree", {projectId: projectId})

		return this
	])
	.controller("RuleDeleteCtrl", \
		["$window", "$scope", "$state", "$stateParams", "RuleHttpService", "WindowService", \
		 ($window,   $scope,   $state,   $stateParams,   RuleHttpService,   WindowService) ->

		NotificationSrv = $window.parent.WDUtil.service('NotificationSrv')

		projectId = $stateParams.projectId
		ruleId = $stateParams.ruleId

		$scope.getData = () ->
			return WindowService.getWindow().data

		$scope.delete = () ->
			RuleHttpService.deleteRule(ruleId).then(
				success = (response) ->
					WindowService.closeWindow()
					NotificationSrv.add(
						title: "rules.rule_deleted_title"
						content: "rules.rule_deleted_content"
						bubble:
							show: true
					)

					$state.go("tree", {projectId: projectId}, {reload: true})
				error = (response) ->
					throw new Error("Cannot delete rule")
			)

		$scope.cancel = () ->
			WindowService.closeWindow()

		return this
	])
