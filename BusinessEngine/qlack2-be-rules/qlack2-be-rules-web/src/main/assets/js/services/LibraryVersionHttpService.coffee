libraryManagerApp = angular.module("rules")

libraryManagerApp.service "LibraryVersionHttpService", ["$http", "SERVICES", ($http, SERVICES) ->
		getLibraryVersion: (libraryVersionId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.LIBRARY_VERSION + "/" + libraryVersionId

		update: (libraryVersionId, libraryVersion) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.LIBRARY_VERSION + "/" + libraryVersionId
				data: libraryVersion

		canDeleteVersion: (versionId) ->
			$http.get(SERVICES._PREFIX + SERVICES.LIBRARY_VERSION + "/" + versionId + SERVICES.CAN_DELETE)

		delete: (libraryVersionId) ->
			$http
				method: "DELETE"
				url: SERVICES._PREFIX + SERVICES.LIBRARY_VERSION + "/" + libraryVersionId

		lock: (libraryVersionId) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.LIBRARY_VERSION + "/" + libraryVersionId + SERVICES.VERSION_ACTION_LOCK

		unlock: (libraryVersionId) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.LIBRARY_VERSION + "/" + libraryVersionId + SERVICES.VERSION_ACTION_UNLOCK

		canDisableTestingVersion: (versionId) ->
			$http.get(SERVICES._PREFIX + SERVICES.LIBRARY_VERSION + "/" + versionId + SERVICES.CAN_DISABLE_TESTING)

		enableTestingVersion: (versionId) ->
			$http.put(SERVICES._PREFIX + SERVICES.LIBRARY_VERSION + "/" + versionId + SERVICES.VERSION_ACTION_ENABLE_TESTING)

		disableTestingVersion: (versionId) ->
			$http.put(SERVICES._PREFIX + SERVICES.LIBRARY_VERSION + "/" + versionId + SERVICES.VERSION_ACTION_DISABLE_TESTING)

		finalise: (libraryVersionId) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.LIBRARY_VERSION + "/" + libraryVersionId + SERVICES.VERSION_ACTION_FINALIZE

		getExportVersionUrl: (versionId) ->
			SERVICES._PREFIX + SERVICES.LIBRARY_VERSION + "/" + versionId + SERVICES.VERSION_ACTION_EXPORT

	]
