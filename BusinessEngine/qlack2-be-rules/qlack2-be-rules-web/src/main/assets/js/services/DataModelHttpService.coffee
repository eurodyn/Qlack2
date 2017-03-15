angular.module("rules")
	.service("DataModelHttpService", ["$http", "SERVICES", ($http, SERVICES) ->

		getByProjectId: (projectId) ->
			$http.get(SERVICES._PREFIX + SERVICES.PROJECTS + "/" + projectId + SERVICES.DATA_MODELS,
				params:
					filterEmpty: true
			)

		getById: (modelId) ->
			$http.get(SERVICES._PREFIX + SERVICES.DATA_MODELS + "/" + modelId)

		create: (projectId, model) ->
			model.projectId = projectId
			$http.post(SERVICES._PREFIX + SERVICES.DATA_MODELS, model)

		update: (modelId, model) ->
			$http.put(SERVICES._PREFIX + SERVICES.DATA_MODELS + "/" + modelId, model)

		deleteModel: (modelId) ->
			$http.delete(SERVICES._PREFIX + SERVICES.DATA_MODELS + "/" + modelId)

		canDeleteModel: (modelId) ->
			$http.get(SERVICES._PREFIX + SERVICES.DATA_MODELS + "/" + modelId + SERVICES.CAN_DELETE)

		getVersionsByModelId: (modelId) ->
			$http.get(SERVICES._PREFIX + SERVICES.DATA_MODELS + "/" + modelId + SERVICES.VERSIONS)

		getVersionsByModelIdAndFilterCycles: (modelId, versionId) ->
			$http.get(SERVICES._PREFIX + SERVICES.DATA_MODELS + "/" + modelId + SERVICES.VERSIONS,
				params:
					filterCycles: versionId
			)

		getVersionById: (versionId) ->
			$http.get(SERVICES._PREFIX + SERVICES.DATA_MODEL_VERSIONS + "/" + versionId)

		createVersion: (modelId, version) ->
			$http.post(SERVICES._PREFIX + SERVICES.DATA_MODELS + "/" + modelId + SERVICES.VERSIONS, version)

		deleteVersion: (versionId) ->
			$http.delete(SERVICES._PREFIX + SERVICES.DATA_MODEL_VERSIONS + "/" + versionId)

		canDeleteVersion: (versionId) ->
			$http.get(SERVICES._PREFIX + SERVICES.DATA_MODEL_VERSIONS + "/" + versionId + SERVICES.CAN_DELETE)

		getFieldTypes: () ->
			$http.get(SERVICES._PREFIX + SERVICES.DATA_MODEL_VERSIONS + SERVICES.FIELD_TYPES)

		lockVersion: (versionId) ->
			$http.put(SERVICES._PREFIX + SERVICES.DATA_MODEL_VERSIONS + "/" + versionId + SERVICES.VERSION_ACTION_LOCK)

		unlockVersion: (versionId) ->
			$http.put(SERVICES._PREFIX + SERVICES.DATA_MODEL_VERSIONS + "/" + versionId + SERVICES.VERSION_ACTION_UNLOCK)

		enableTestingVersion: (versionId) ->
			$http.put(SERVICES._PREFIX + SERVICES.DATA_MODEL_VERSIONS + "/" + versionId + SERVICES.VERSION_ACTION_ENABLE_TESTING)

		disableTestingVersion: (versionId) ->
			$http.put(SERVICES._PREFIX + SERVICES.DATA_MODEL_VERSIONS + "/" + versionId + SERVICES.VERSION_ACTION_DISABLE_TESTING)

		canUpdateEnabledForTestingVersion: (version) ->
			versionId = version.id
			$http.post(SERVICES._PREFIX + SERVICES.DATA_MODEL_VERSIONS + "/" + versionId + SERVICES.CAN_UPDATE_ENABLED_FOR_TESTING, version)

		canEnableTestingVersion: (versionId) ->
			$http.get(SERVICES._PREFIX + SERVICES.DATA_MODEL_VERSIONS + "/" + versionId + SERVICES.CAN_ENABLE_TESTING)

		canDisableTestingVersion: (versionId) ->
			$http.get(SERVICES._PREFIX + SERVICES.DATA_MODEL_VERSIONS + "/" + versionId + SERVICES.CAN_DISABLE_TESTING)

		finalizeVersion: (versionId) ->
			$http.put(SERVICES._PREFIX + SERVICES.DATA_MODEL_VERSIONS + "/" + versionId + SERVICES.VERSION_ACTION_FINALIZE)

		canFinalizeVersion: (versionId) ->
			$http.get(SERVICES._PREFIX + SERVICES.DATA_MODEL_VERSIONS + "/" + versionId + SERVICES.CAN_FINALIZE)

		getExportVersionUrl: (versionId) ->
			SERVICES._PREFIX + SERVICES.DATA_MODEL_VERSIONS + "/" + versionId + SERVICES.VERSION_ACTION_EXPORT

		importVersion: (modelId, version) ->
			$http.put(SERVICES._PREFIX + SERVICES.DATA_MODELS + "/" + modelId + SERVICES.VERSION_ACTION_IMPORT, version)
	])
