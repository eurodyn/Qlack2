angular.module("rules")
	.service("LibraryService", \
		[ "$q", "LibraryHttpService", "LibraryVersionHttpService", "SecuritySrv", "Util", \
		  ($q,   LibraryHttpService,   LibraryVersionHttpService,   SecuritySrv,   Util) ->

		libraryPromise = null
		library = null
		libraryVersion = null

		canManage = null
		canLock = null
		canUnlockAny = null
		canEditVersion = null

		actions = []

		actionsMap =
			deleteLibrary:
				name: "action.delete"
				icon: "fa fa-trash-o"
				value: "deleteLibrary"
				order: 1
			createVersion:
				name: "action.create_version"
				icon: "fa fa-camera-retro"
				cssClass: "separator"
				value: "createVersion"
				order: 2
			updateVersionContent:
				name: "action.update_version_content"
				icon: "fa fa-pencil"
				value: "updateVersionContent"
				order: 3
			deleteVersion:
				name: "action.delete_version"
				icon: "fa fa-trash-o"
				value: "deleteVersion"
				order: 4
			lockVersion:
				name: "action.lock"
				icon: "fa fa-lock"
				value: "lockVersion"
				order: 5
			unlockVersion:
				name: "action.unlock"
				icon: "fa fa-unlock"
				value: "unlockVersion"
				order: 6
			importVersion:
				name: "action.import",
				icon: "fa fa-sign-in"
				value: "importVersion"
				order: 7
			exportVersion:
				name: "action.export"
				icon: "fa fa-sign-out"
				value: "exportVersion"
				order: 8
			enableTestingVersion:
				name: "action.enable_testing"
				icon: "fa fa-flag"
				value: "enableTestingVersion"
				order: 9
			disableTestingVersion:
				name: "action.disable_testing"
				icon: "fa fa-flag"
				value: "disableTestingVersion"
				order: 10
			finalizeVersion:
				name: "action.finalize_version",
				icon: "fa fa-check-circle-o"
				value: "finalizeVersion"
				order: 11

		init: () ->
			libraryPromise = null
			library = null
			libraryVersion = null

			canManage = null
			canLock = null
			canUnlockAny = null
			canEditVersion = null

			actions = []

		initLibraryPromise: (libraryId, projectId) ->

			canManagePromise = Util.checkPermissions('RUL_MANAGE_LIBRARY', libraryId, projectId)
			canLockPromise = Util.checkPermissions('RUL_LOCK_LIBRARY', libraryId, projectId)
			canUnlockAnyPromise = Util.checkPermissions('RUL_UNLOCK_ANY_LIBRARY', libraryId, projectId)

			initialLibraryPromise = LibraryHttpService.getById(libraryId).then(
				success = (response) ->
					library = response.data
					return library
				error = (response) ->
					throw new Error("Cannot fetch library")
			)

			securityPromises = $q.all([canManagePromise, canLockPromise, canUnlockAnyPromise])

			securityPromises.then(
				success = (results) ->
					canManage = results[0]
					canLock = results[1]
					canUnlockAny = results[2]
			)

			# resolve after security
			libraryPromise = $q.all([securityPromises, initialLibraryPromise]).then(
				success = () ->
					return initialLibraryPromise
			)

			return libraryPromise

		initLibraryVersion: (versionId) ->
			libraryVersionPromise = LibraryVersionHttpService.getLibraryVersion(versionId).then(
				success = (response) ->
					libraryVersion = response.data
					return libraryVersion
				error = (response) ->
					throw new Error("Cannot fetch version")
			)

		initActionsNoVersion: () ->
			actions.pop() while actions.length > 0

			# enable 'save' button (hack)
			canEditVersion = true

			if (canManage)
				actions.push(actionsMap.deleteLibrary)
				actions.push(actionsMap.createVersion)
				actions.push(actionsMap.importVersion)

		initActions: () ->
			actions.pop() while actions.length > 0

			user = SecuritySrv.getUser()
			userId = user.ticket.userID

			if (canManage)
				actions.push(actionsMap.deleteLibrary)
				actions.push(actionsMap.createVersion)
				actions.push(actionsMap.importVersion)

			if (libraryVersion.state is 1)
				# finalized
				canEditVersion = false

				actions.push(actionsMap.exportVersion)

				if (canManage)
					actions.push(actionsMap.deleteVersion)
			else
				if (libraryVersion.lockedBy is null)
					# unlocked
					canEditVersion = true

					if (canLock)
						actions.push(actionsMap.lockVersion)

					if (canManage)
						actions.push(actionsMap.updateVersionContent)
						actions.push(actionsMap.deleteVersion)
						actions.push(actionsMap.finalizeVersion)

						if (libraryVersion.state is 0)
							actions.push(actionsMap.enableTestingVersion)
						else
							actions.push(actionsMap.disableTestingVersion)
				else
					if (libraryVersion.lockedBy.id is userId)
						# locked by current user
						canEditVersion = true

						if (canLock or canUnlockAny)
							actions.push(actionsMap.unlockVersion)

						if (canManage)
							actions.push(actionsMap.updateVersionContent)
							actions.push(actionsMap.deleteVersion)
							actions.push(actionsMap.finalizeVersion)

							if (libraryVersion.state is 0)
								actions.push(actionsMap.enableTestingVersion)
							else
								actions.push(actionsMap.disableTestingVersion)
					else
						# locked by other user
						canEditVersion = false

						if (canUnlockAny)
							actions.push(actionsMap.unlockVersion)

		getLibraryPromise: () ->
			libraryPromise

		getLibrary: () ->
			library

		getLibraryVersion: () ->
			libraryVersion

		getCanManage: () ->
			canManage

		getCanEditVersion: () ->
			canEditVersion

		getActions: () ->
			actions
	])
