angular.module("rules")
	.service("DataModelService", \
		[ "$q", "DataModelHttpService", "SecuritySrv", "Util", \
		  ($q,   DataModelHttpService,   SecuritySrv,   Util) ->

		dataModelPromise = null
		dataModel = null
		dataModelVersion = null

		canManage = null
		canLock = null
		canUnlockAny = null
		canEditVersion = null
		rebindVal = null

		actions = []

		actionsMap =
			deleteModel:
				name: "action.delete"
				icon: "fa fa-trash-o"
				value: "deleteModel"
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
			dataModelPromise = null
			dataModel = null
			dataModelVersion = null

			canManage = null
			canLock = null
			canUnlockAny = null
			canEditVersion = null
			rebindVal = null

			actions = []

		initDataModelPromise: (modelId, projectId) ->

			canManagePromise = Util.checkPermissions('RUL_MANAGE_DATA_MODEL', modelId, projectId)
			canLockPromise = Util.checkPermissions('RUL_LOCK_DATA_MODEL', modelId, projectId)
			canUnlockAnyPromise = Util.checkPermissions('RUL_UNLOCK_ANY_DATA_MODEL', modelId, projectId)

			initialDataModelPromise = DataModelHttpService.getById(modelId).then(
				success = (response) ->
					dataModel = response.data
					return dataModel
				error = (response) ->
					throw new Error("Cannot fetch data model")
			)

			securityPromises = $q.all([canManagePromise, canLockPromise, canUnlockAnyPromise])

			securityPromises.then(
				success = (results) ->
					canManage = results[0]
					canLock = results[1]
					canUnlockAny = results[2]
			)

			# resolve after security
			dataModelPromise = $q.all([securityPromises, initialDataModelPromise]).then(
				success = () ->
					return initialDataModelPromise
			)

			return dataModelPromise

		initDataModelVersion: (versionId) ->
			dataModelVersionPromise = DataModelHttpService.getVersionById(versionId).then(
				success = (response) ->
					dataModelVersion = response.data
					return dataModelVersion
				error = (response) ->
					throw new Error("Cannot fetch version")
			)

		initActionsNoVersion: () ->
			actions.pop() while actions.length > 0

			rebindVal = false

			# enable 'save' button (hack)
			canEditVersion = true

			if (canManage)
				actions.push(actionsMap.deleteModel)
				actions.push(actionsMap.createVersion)
				actions.push(actionsMap.importVersion)

		initActions: () ->
			actions.pop() while actions.length > 0

			rebindVal = false

			user = SecuritySrv.getUser()
			userId = user.ticket.userID

			if (canManage)
				actions.push(actionsMap.deleteModel)
				actions.push(actionsMap.createVersion)
				actions.push(actionsMap.importVersion)

			if (dataModelVersion.state is "FINAL")
				# finalized
				canEditVersion = false

				actions.push(actionsMap.exportVersion)

				if (canManage)
					actions.push(actionsMap.deleteVersion)
			else
				if (dataModelVersion.lockedBy is null)
					# unlocked
					canEditVersion = true

					if (canLock)
						actions.push(actionsMap.lockVersion)

					if (canManage)
						actions.push(actionsMap.deleteVersion)
						actions.push(actionsMap.finalizeVersion)

						if (dataModelVersion.state is "DRAFT")
							actions.push(actionsMap.enableTestingVersion)
						else
							actions.push(actionsMap.disableTestingVersion)
				else
					if (dataModelVersion.lockedBy.id is userId)
						# locked by current user
						canEditVersion = true

						if (canLock or canUnlockAny)
							actions.push(actionsMap.unlockVersion)

						if (canManage)
							actions.push(actionsMap.deleteVersion)
							actions.push(actionsMap.finalizeVersion)

							if (dataModelVersion.state is "DRAFT")
								actions.push(actionsMap.enableTestingVersion)
							else
								actions.push(actionsMap.disableTestingVersion)
					else
						# locked by other user
						canEditVersion = false

						if (canUnlockAny)
							actions.push(actionsMap.unlockVersion)

			rebindVal = canManage && canEditVersion

		getDataModelPromise: () ->
			dataModelPromise

		getDataModel: () ->
			dataModel

		getDataModelVersion: () ->
			dataModelVersion

		getCanManage: () ->
			canManage

		getCanEditVersion: () ->
			canEditVersion

		getRebindVal: () ->
			rebindVal

		getActions: () ->
			actions
	])
