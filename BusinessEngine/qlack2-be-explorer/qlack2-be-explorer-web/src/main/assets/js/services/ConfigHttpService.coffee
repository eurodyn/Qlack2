angular
	.module('explorer')
	.service 'ConfigHttpService', ['$http', 'SERVICES', ($http, SERVICES) ->		
		getOperations: (projectId, subjectId) ->
			url = SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_SUBJECTS + "/" + subjectId + "/operations"
			if !!projectId
				url = url + "?projectId=" + projectId
			$http
				method: "GET"
				url:  url
				
		saveOperations: (projectId, subjectId, operations) ->
			url = SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_SUBJECTS + "/" + subjectId + "/operations"
			if !!projectId
				url = url + "?projectId=" + projectId
			$http
				method: "POST"
				url: url
				data: operations
				
		getManagedUsers: (projectId) ->
			url = SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_MANAGED_USERS
			if !!projectId
				url = url + "?projectId=" + projectId
			$http
				method: "GET"
				url: url
				
		getManagedGroups: (projectId) ->
			url = SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_MANAGED_GROUPS
			if !!projectId
				url = url + "?projectId=" + projectId
			$http
				method: "GET"
				url: url
				
		manageSubject: (projectId, subjectId) ->
			url = SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_SUBJECTS + "/" + subjectId + "/manage"
			if !!projectId
				url = url + "?projectId=" + projectId
			$http
				method: "PUT"
				url: url
				
		unmanageSubject: (projectId, subjectId) ->
			url = SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_SUBJECTS + "/" + subjectId + "/unmanage"
			if !!projectId
				url = url + "?projectId=" + projectId
			$http
				method: "PUT"
				url: url
				
		getAllUsers: () ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_USERS
				
		getAllGroups: () ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_GROUPS
	]