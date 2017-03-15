angular
	.module("formsManagerApp")
	.service "FormService", ["$q", "FormHttpService", "FormVersionHttpService", "ProjectHttpService", "UtilService", "SecuritySrv", ($q, FormHttpService, FormVersionHttpService, ProjectHttpService, UtilService, SecuritySrv) ->
		form = null
		formVersion = null
		languagesMap = null
		formLanguages = []

		actions = []

		canManageForm = null
		canLockForm = null
		canUnlockForm = null
		canEditVersion = null
		rebindVal = null

		user = SecuritySrv.getUser()

		# Share translations between FormVersionController and CreateFormVersionTranslationsController
		translations = null
		translationsDataSource = null

		getForm: () ->
			form

		getFormVersion: () ->
			formVersion

		getFormById: (formId, projectId, forceReload) ->
			deferred = $q.defer()
			# Only load the form from the server if force reload has been requested or if the existing form is stale
			if (forceReload or !form? or (form.id isnt formId))
				# empty form version
				formVersion = null

				langDeferred = $q.defer()
				if languagesMap?
					langDeferred.resolve(languagesMap)
				else
					ProjectHttpService.getLanguages().then(
						success = (result) ->
							if result.data?
								languagesMap = {}
								for lang in result.data
									languagesMap[lang.locale] = lang
									console.log languagesMap[lang.locale]
							langDeferred.resolve(languagesMap)
					)

				#Security promises
				canManageFormDeferred = UtilService.checkPermissions('FRM_MANAGE_FORM', formId, projectId)
				canLockFormDeferred = UtilService.checkPermissions('FRM_LOCK_FORM', formId, projectId)
				canUnlockFormDeferred = UtilService.checkPermissions('FRM_UNLOCK_ANY_FORM', formId, projectId)
				formDeferred = FormHttpService.getForm(formId)

				$q.all([canManageFormDeferred, canLockFormDeferred, canUnlockFormDeferred, formDeferred, langDeferred.promise]).then(
					success = (results) ->
						canManageForm = results[0]
						canLockForm = results[1]
						canUnlockForm = results[2]
						form = results[3].data

						console.log "canManageForm: " + canManageForm
						console.log "canLockForm: " + canLockForm
						console.log "canUnlockForm: " + canUnlockForm

						formLanguages.pop() while formLanguages.length > 0
						for locale in form.locales
							lang = languagesMap[locale]
							console.log lang
							formLanguages.push(lang)

						deferred.resolve(form)
					error = (result) ->
						deferred.resolve("error")
						return
				)
			else
				deferred.resolve(form)
			deferred.promise

		getFormVersionsByFormId: (formId) ->
			deferred = $q.defer()
			FormHttpService.getFormVersions(formId).then(
				success = (result) ->
					form.formVersions = result.data
					deferred.resolve(result.data)
					return
			)
			deferred.promise

		getFormVersionById: (versionId) ->
			deferred = $q.defer()
			FormVersionHttpService.getFormVersion(versionId).then(
				success = (result) ->
					formVersion = result.data
					deferred.resolve(result.data)
					return
				error = (result) ->
					deferred.resolve("error")
					return
			)
			deferred.promise

		getActions: () ->
			actions

		setActions: () ->
			actions.pop() while actions.length > 0

			if canManageForm
				actions.push({ name: "delete", value: "deleteForm", icon: "fa fa-trash-o", order: 1})
				actions.push({ name: "new_version", value: "createFormVersion", cssClass: "separator", icon: "fa fa-camera-retro", order: 2 })
				actions.push({ name: "import", value: "importFormVersion", icon: "fa fa-sign-in", order: 5 })

			if formVersion?
				userId = user.ticket.userID

				rebindVal = false
				canEditVersion = true

				if formVersion.state? && formVersion.state == 1
					#Finalised form
					canEditVersion = false

					actions.push({ name: "export", value: "exportFormVersion", icon: "fa fa-sign-out", order: 6 })

					if canManageForm
						actions.push({ name: "delete_version", value: "deleteFormVersion", icon: "fa fa-trash-o", order: 7 })
				else
					if formVersion.lockedBy?
						#Locked form
						if userId? && userId == formVersion.lockedBy.id
							if canLockForm || canUnlockForm
								actions.push({ name: "unlock", value: "unlockFormVersion", icon: "fa fa-unlock", order: 4 })
							if canManageForm
								actions.push({ name: "delete_version", value: "deleteFormVersion", icon: "fa fa-trash-o", order: 7 })
								actions.push({ name: "finalise_version", value: "finaliseFormVersion", icon: "fa fa-check-circle-o", order: 8 })

								if formVersion.state? && formVersion.state == 2
									actions.push({ name: "disable_testing", value: "disableTestingForFormVersion", icon: "fa fa-flag", order: 9 })
								else if formVersion.state? && formVersion.state == 0
									actions.push({ name: "enable_testing", value: "enableTestingForFormVersion", icon: "fa fa-flag", order: 9 })
						else
							canEditVersion = false
							if canUnlockForm
								actions.push({ name: "unlock", value: "unlockFormVersion", icon: "fa fa-unlock", order: 4 })
					else
						#Unlocked form
						if canLockForm
							actions.push({ name: "lock", value: "lockFormVersion", icon: "fa fa-lock", order: 3 })
						if canManageForm
							actions.push({ name: "delete_version", value: "deleteFormVersion", icon: "fa fa-trash-o", order: 7 })
							actions.push({ name: "finalise_version", value: "finaliseFormVersion", icon: "fa fa-check-circle-o", order: 8 })

							if formVersion.state? && formVersion.state == 2
								actions.push({ name: "disable_testing", value: "disableTestingForFormVersion", icon: "fa fa-flag", order: 9 })
							else if formVersion.state? && formVersion.state == 0
								actions.push({ name: "enable_testing", value: "enableTestingForFormVersion", icon: "fa fa-flag", order: 9 })

				rebindVal = canManageForm && canEditVersion

		getCanManageForm: () ->
			canManageForm

		getCanLockForm: () ->
			canLockForm

		getCanUnlockForm: () ->
			canUnlockForm

		getCanEditVersion: () ->
			canEditVersion

		getRebindVal: () ->
			rebindVal

		getFormLanguages: () ->
			formLanguages

		setTranslations: (versionTranslations) ->
			translations = versionTranslations
			return

		setTranslationsDataSource: (versionTranslationsDataSource) ->
			translationsDataSource = versionTranslationsDataSource
			return

		createFormVersionTranslations: (newTranslations) ->
			Array.prototype.push.apply(translations, newTranslations)
			translationsDataSource.read()

			if !formVersion.translations
				formVersion.translations = []

			Array.prototype.push.apply(formVersion.translations, newTranslations)
			return

		deleteFormVersionTranslations: (keyId) ->
			loop
				i = this.findFormVersionTranslation(keyId)
				if i == null
					break

				translations.splice(i, 1)
				translationsDataSource.read()

				formVersion.translations.splice(i, 1)

			return

		findFormVersionTranslation: (keyId) ->
			for i in [translations.length - 1..0] by -1
				dataItem = translations[i]
				if dataItem.keyId == keyId
					return i
			return null

	]
