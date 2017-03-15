angular.module("rules")
	.service("RuleService", \
		[ "$q", "RuleHttpService", "SecuritySrv", "Util", \
		  ($q,   RuleHttpService,   SecuritySrv,   Util) ->

		rulePromise = null
		rule = null
		ruleVersion = null

		canManage = null
		canLock = null
		canUnlockAny = null
		canEditVersion = null

		actions = []

		actionsMap =
			deleteRule:
				name: "action.delete"
				icon: "fa fa-trash-o"
				value: "deleteRule"
				order: 1
			createVersion:
				name: "action.create_version"
				icon: "fa fa-camera-retro"
				cssClass: "separator"
				value: "createVersion"
				order: 2
			deleteVersion:
				name: "action.delete_version"
				icon: "fa fa-trash-o"
				value: "deleteVersion"
				order: 3
			lockVersion:
				name: "action.lock"
				icon: "fa fa-lock"
				value: "lockVersion"
				order: 4
			unlockVersion:
				name: "action.unlock"
				icon: "fa fa-unlock"
				value: "unlockVersion"
				order: 5
			importVersion:
				name: "action.import",
				icon: "fa fa-sign-in"
				value: "importVersion"
				order: 6
			exportVersion:
				name: "action.export"
				icon: "fa fa-sign-out"
				value: "exportVersion"
				order: 7
			enableTestingVersion:
				name: "action.enable_testing"
				icon: "fa fa-flag"
				value: "enableTestingVersion"
				order: 8
			disableTestingVersion:
				name: "action.disable_testing"
				icon: "fa fa-flag"
				value: "disableTestingVersion"
				order: 9
			finalizeVersion:
				name: "action.finalize_version",
				icon: "fa fa-check-circle-o"
				value: "finalizeVersion"
				order: 10

		init: () ->
			rulePromise = null
			rule = null
			ruleVersion = null

			canManage = null
			canLock = null
			canUnlockAny = null
			canEditVersion = null

			actions = []

		initRulePromise: (ruleId, projectId) ->

			canManagePromise = Util.checkPermissions('RUL_MANAGE_RULE', ruleId, projectId)
			canLockPromise = Util.checkPermissions('RUL_LOCK_RULE', ruleId, projectId)
			canUnlockAnyPromise = Util.checkPermissions('RUL_UNLOCK_ANY_RULE', ruleId, projectId)

			initialRulePromise = RuleHttpService.getById(ruleId).then(
				success = (response) ->
					rule = response.data
					return rule
				error = (response) ->
					throw new Error("Cannot fetch rule")
			)

			securityPromises = $q.all([canManagePromise, canLockPromise, canUnlockAnyPromise])

			securityPromises.then(
				success = (results) ->
					canManage = results[0]
					canLock = results[1]
					canUnlockAny = results[2]
			)

			# resolve after security
			rulePromise = $q.all([securityPromises, initialRulePromise]).then(
				success = () ->
					return initialRulePromise
			)

			return rulePromise

		initRuleVersion: (versionId) ->
			ruleVersionPromise = RuleHttpService.getVersionById(versionId).then(
				success = (response) ->
					ruleVersion = response.data
					return ruleVersion
				error = (response) ->
					throw new Error("Cannot fetch version")
			)

		initActionsNoVersion: () ->
			actions.pop() while actions.length > 0

			# enable 'save' button (hack)
			canEditVersion = true

			if (canManage)
				actions.push(actionsMap.deleteRule)
				actions.push(actionsMap.createVersion)
				actions.push(actionsMap.importVersion)

		initActions: () ->
			actions.pop() while actions.length > 0

			user = SecuritySrv.getUser()
			userId = user.ticket.userID

			if (canManage)
				actions.push(actionsMap.deleteRule)
				actions.push(actionsMap.createVersion)
				actions.push(actionsMap.importVersion)

			if (ruleVersion.state is "FINAL")
				# finalized
				canEditVersion = false

				actions.push(actionsMap.exportVersion)

				if (canManage)
					actions.push(actionsMap.deleteVersion)
			else
				if (ruleVersion.lockedBy is null)
					# unlocked
					canEditVersion = true

					if (canLock)
						actions.push(actionsMap.lockVersion)

					if (canManage)
						actions.push(actionsMap.deleteVersion)
						actions.push(actionsMap.finalizeVersion)

						if (ruleVersion.state is "DRAFT")
							actions.push(actionsMap.enableTestingVersion)
						else
							actions.push(actionsMap.disableTestingVersion)
				else
					if (ruleVersion.lockedBy.id is userId)
						# locked by current user
						canEditVersion = true

						if (canLock or canUnlockAny)
							actions.push(actionsMap.unlockVersion)

						if (canManage)
							actions.push(actionsMap.deleteVersion)
							actions.push(actionsMap.finalizeVersion)

							if (ruleVersion.state is "DRAFT")
								actions.push(actionsMap.enableTestingVersion)
							else
								actions.push(actionsMap.disableTestingVersion)
					else
						# locked by other user
						canEditVersion = false

						if (canUnlockAny)
							actions.push(actionsMap.unlockVersion)

		getRulePromise: () ->
			rulePromise

		getRule: () ->
			rule

		getRuleVersion: () ->
			ruleVersion

		getCanManage: () ->
			canManage

		getCanEditVersion: () ->
			canEditVersion

		getActions: () ->
			actions
	])
