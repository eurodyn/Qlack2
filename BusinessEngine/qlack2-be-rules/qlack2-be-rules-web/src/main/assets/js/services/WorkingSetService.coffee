angular.module("rules")
	.service("WorkingSetService", \
		[ "$q", "WorkingSetHttpService", "SecuritySrv", "Util", \
		  ($q,   WorkingSetHttpService,   SecuritySrv,   Util) ->

		workingSetPromise = null
		workingSet = null
		workingSetVersion = null

		canManage = null
		canLock = null
		canUnlockAny = null
		canEditVersion = null
		rebindVal = null

		actions = []

		actionsMap =
			deleteWorkingSet:
				name: "action.delete"
				icon: "fa fa-trash-o"
				value: "deleteWorkingSet"
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
			downloadModelsJar:
				name: "action.download_models_jar",
				icon: "fa fa-archive"
				value: "downloadModelsJar"
				order: 11

		init: () ->
			workingSetPromise = null
			workingSet = null
			workingSetVersion = null

			canManage = null
			canLock = null
			canUnlockAny = null
			canEditVersion = null
			rebindVal = null

			actions = []

		initWorkingSetPromise: (workingSetId, projectId) ->

			canManagePromise = Util.checkPermissions('RUL_MANAGE_WORKING_SET', workingSetId, projectId)
			canLockPromise = Util.checkPermissions('RUL_LOCK_WORKING_SET', workingSetId, projectId)
			canUnlockAnyPromise = Util.checkPermissions('RUL_UNLOCK_ANY_WORKING_SET', workingSetId, projectId)

			initialWorkingSetPromise = WorkingSetHttpService.getById(workingSetId).then(
				success = (response) ->
					workingSet = response.data
					return workingSet
				error = (response) ->
					throw new Error("Cannot fetch working set")
			)

			securityPromises = $q.all([canManagePromise, canLockPromise, canUnlockAnyPromise])

			securityPromises.then(
				success = (results) ->
					canManage = results[0]
					canLock = results[1]
					canUnlockAny = results[2]
			)

			# resolve after security
			workingSetPromise = $q.all([securityPromises, initialWorkingSetPromise]).then(
				success = () ->
					return initialWorkingSetPromise
			)

			return workingSetPromise

		initWorkingSetVersion: (versionId) ->
			workingSetVersionPromise = WorkingSetHttpService.getVersionById(versionId).then(
				success = (response) ->
					workingSetVersion = response.data
					return workingSetVersion
				error = (response) ->
					throw new Error("Cannot fetch version")
			)

		initActionsNoVersion: () ->
			actions.pop() while actions.length > 0

			rebindVal = false

			# enable 'save' button (hack)
			canEditVersion = true

			if (canManage)
				actions.push(actionsMap.deleteWorkingSet)
				actions.push(actionsMap.createVersion)
				actions.push(actionsMap.importVersion)

		initActions: () ->
			actions.pop() while actions.length > 0

			rebindVal = false

			user = SecuritySrv.getUser()
			userId = user.ticket.userID

			if (canManage)
				actions.push(actionsMap.deleteWorkingSet)
				actions.push(actionsMap.createVersion)
				actions.push(actionsMap.importVersion)

			if (workingSetVersion.state is "TESTING" or workingSetVersion.state is "FINAL")
				actions.push(actionsMap.downloadModelsJar)

			if (workingSetVersion.state is "FINAL")
				# finalized
				canEditVersion = false

				actions.push(actionsMap.exportVersion)

				if (canManage)
					actions.push(actionsMap.deleteVersion)
			else
				if (workingSetVersion.lockedBy is null)
					# unlocked
					canEditVersion = true

					if (canLock)
						actions.push(actionsMap.lockVersion)

					if (canManage)
						actions.push(actionsMap.deleteVersion)
						actions.push(actionsMap.finalizeVersion)

						if (workingSetVersion.state is "DRAFT")
							actions.push(actionsMap.enableTestingVersion)
						else
							actions.push(actionsMap.disableTestingVersion)
				else
					if (workingSetVersion.lockedBy.id is userId)
						# locked by current user
						canEditVersion = true

						if (canLock or canUnlockAny)
							actions.push(actionsMap.unlockVersion)

						if (canManage)
							actions.push(actionsMap.deleteVersion)
							actions.push(actionsMap.finalizeVersion)

							if (workingSetVersion.state is "DRAFT")
								actions.push(actionsMap.enableTestingVersion)
							else
								actions.push(actionsMap.disableTestingVersion)
					else
						# locked by other user
						canEditVersion = false

						if (canUnlockAny)
							actions.push(actionsMap.unlockVersion)

			rebindVal = canManage && canEditVersion

		getWorkingSetPromise: () ->
			workingSetPromise

		getWorkingSet: () ->
			workingSet

		getWorkingSetVersion: () ->
			workingSetVersion

		getCanManage: () ->
			canManage

		getCanEditVersion: () ->
			canEditVersion

		getRebindVal: () ->
			rebindVal

		getActions: () ->
			actions
	])
