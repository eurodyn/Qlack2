angular.module("rules")
	.service("WorkingSetHttpService", ["$http", "SERVICES", ($http, SERVICES) ->

		getById: (workingSetId) ->
			$http.get(SERVICES._PREFIX + SERVICES.WORKING_SETS + "/" + workingSetId)

		create: (projectId, workingSet) ->
			workingSet.projectId = projectId
			$http.post(SERVICES._PREFIX + SERVICES.WORKING_SETS, workingSet)

		update: (workingSetId, workingSet) ->
			$http.put(SERVICES._PREFIX + SERVICES.WORKING_SETS + "/" + workingSetId, workingSet)

		deleteWorkingSet: (workingSetId) ->
			$http.delete(SERVICES._PREFIX + SERVICES.WORKING_SETS + "/" + workingSetId)

		canDeleteWorkingSet: (workingSetId) ->
			$http.get(SERVICES._PREFIX + SERVICES.WORKING_SETS + "/" + workingSetId + SERVICES.CAN_DELETE)

		getVersionById: (versionId) ->
			$http.get(SERVICES._PREFIX + SERVICES.WORKING_SET_VERSIONS + "/" + versionId)

		createVersion: (workingSetId, version) ->
			$http.post(SERVICES._PREFIX + SERVICES.WORKING_SETS + "/" + workingSetId + SERVICES.VERSIONS, version)

		canDeleteVersion: (versionId) ->
			$http.get(SERVICES._PREFIX + SERVICES.WORKING_SET_VERSIONS + "/" + versionId + SERVICES.CAN_DELETE)

		deleteVersion: (versionId) ->
			$http.delete(SERVICES._PREFIX + SERVICES.WORKING_SET_VERSIONS + "/" + versionId)

		lockVersion: (versionId) ->
			$http.put(SERVICES._PREFIX + SERVICES.WORKING_SET_VERSIONS + "/" + versionId + SERVICES.VERSION_ACTION_LOCK)

		unlockVersion: (versionId) ->
			$http.put(SERVICES._PREFIX + SERVICES.WORKING_SET_VERSIONS + "/" + versionId + SERVICES.VERSION_ACTION_UNLOCK)

		enableTestingVersion: (versionId) ->
			$http.put(SERVICES._PREFIX + SERVICES.WORKING_SET_VERSIONS + "/" + versionId + SERVICES.VERSION_ACTION_ENABLE_TESTING)

		disableTestingVersion: (versionId) ->
			$http.put(SERVICES._PREFIX + SERVICES.WORKING_SET_VERSIONS + "/" + versionId + SERVICES.VERSION_ACTION_DISABLE_TESTING)

		canUpdateEnabledForTestingVersion: (version) ->
			versionId = version.id
			$http.post(SERVICES._PREFIX + SERVICES.WORKING_SET_VERSIONS + "/" + versionId + SERVICES.CAN_UPDATE_ENABLED_FOR_TESTING, version)

		canEnableTestingVersion: (versionId) ->
			$http.get(SERVICES._PREFIX + SERVICES.WORKING_SET_VERSIONS + "/" + versionId + SERVICES.CAN_ENABLE_TESTING)

		canDisableTestingVersion: (versionId) ->
			$http.get(SERVICES._PREFIX + SERVICES.WORKING_SET_VERSIONS + "/" + versionId + SERVICES.CAN_DISABLE_TESTING)

		finalizeVersion: (versionId) ->
			$http.put(SERVICES._PREFIX + SERVICES.WORKING_SET_VERSIONS + "/" + versionId + SERVICES.VERSION_ACTION_FINALIZE)

		canFinalizeVersion: (versionId) ->
			$http.get(SERVICES._PREFIX + SERVICES.WORKING_SET_VERSIONS + "/" + versionId + SERVICES.CAN_FINALIZE)

		getExportVersionUrl: (versionId) ->
			SERVICES._PREFIX + SERVICES.WORKING_SET_VERSIONS + "/" + versionId + SERVICES.VERSION_ACTION_EXPORT

		importVersion: (workingSetId, version) ->
			$http.put(SERVICES._PREFIX + SERVICES.WORKING_SETS + "/" + workingSetId + SERVICES.VERSION_ACTION_IMPORT, version)

		canGetDataModelsJar: (versionId) ->
			$http.get(SERVICES._PREFIX + SERVICES.WORKING_SET_VERSIONS + "/" + versionId + SERVICES.DATA_MODELS_JAR + SERVICES.CAN_GET)

		getDataModelsJarUrl: (versionId) ->
			SERVICES._PREFIX + SERVICES.WORKING_SET_VERSIONS + "/" + versionId + SERVICES.DATA_MODELS_JAR
	])
