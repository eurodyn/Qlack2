rulesManagerApp = angular.module("rules")

rulesManagerApp.service "LibraryHttpService", ["$http", "SERVICES", ($http, SERVICES) ->

		getByProjectId: (projectId) ->
			$http.get(SERVICES._PREFIX + SERVICES.PROJECTS + "/" + projectId + SERVICES.LIBRARIES,
				params:
					filterEmpty: true
			)

		getLibrary: (libraryId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.LIBRARIES + "/" + libraryId

		getById: (libraryId) ->
			$http.get(SERVICES._PREFIX + SERVICES.LIBRARIES + "/" + libraryId)

		create: (libraryObj) ->
			$http
				method: "POST"
				url: SERVICES._PREFIX + SERVICES.LIBRARIES
				data: libraryObj

		update: (libraryId, library) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.LIBRARIES + "/" + libraryId
				data: library

		canDeleteLibrary: (libraryId) ->
			$http.get(SERVICES._PREFIX + SERVICES.LIBRARIES + "/" + libraryId + SERVICES.CAN_DELETE)

		deleteLibrary: (libraryId) ->
			$http
				method: "DELETE"
				url: SERVICES._PREFIX + SERVICES.LIBRARIES + "/" + libraryId

		countLibraryVersionsLockedByOtherUser: (libraryId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.LIBRARIES + "/" + libraryId + SERVICES.VERSIONS + SERVICES.COUNT_LOCKED_BY_OTHER_USER

		createLibraryVersion: (libraryId, libraryVersionObj) ->
			$http
				method: "POST"
				url: SERVICES._PREFIX + SERVICES.LIBRARIES + "/" + libraryId + SERVICES.VERSIONS
				data: libraryVersionObj

		getVersionsByLibraryId: (libraryId) ->
			$http.get(SERVICES._PREFIX + SERVICES.LIBRARIES + "/" + libraryId + SERVICES.VERSIONS)

		importVersion: (libraryId, version) ->
			$http.put(SERVICES._PREFIX + SERVICES.LIBRARIES + "/" + libraryId + SERVICES.VERSION_ACTION_IMPORT, version)
	]
