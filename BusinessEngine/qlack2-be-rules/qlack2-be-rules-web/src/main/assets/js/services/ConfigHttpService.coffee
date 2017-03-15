angular
	.module('rules')
	.service 'ConfigHttpService', ['$http', 'SERVICES', ($http, SERVICES) ->
						
		getOperations: (resourceId, subjectId) ->
			url = SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_SUBJECTS + "/" + subjectId + SERVICES.CONFIG_OPERATIONS
			if !!resourceId
				url = url + "?resourceId=" + resourceId
			$http
				method: "GET"
				url:  url
				
		saveOperations: (resourceId, subjectId, operations) ->
			url = SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_SUBJECTS + "/" + subjectId + SERVICES.CONFIG_OPERATIONS
			if !!resourceId
				url = url + "?resourceId=" + resourceId
			$http
				method: "POST"
				url: url
				data: operations
				
		getManagedUsers: (resourceId) ->
			url = SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_MANAGED_USERS
			if !!resourceId
				url = url + "?resourceId=" + resourceId
			$http
				method: "GET"
				url: url
				
		getManagedGroups: (resourceId) ->
			url = SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_MANAGED_GROUPS
			if !!resourceId
				url = url + "?resourceId=" + resourceId
			$http
				method: "GET"
				url: url
				
		manageSubject: (resourceId, subjectId) ->
			url = SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_SUBJECTS + "/" + subjectId + "/manage"
			if !!resourceId
				url = url + "?resourceId=" + resourceId
			$http
				method: "PUT"
				url: url
				
		unmanageSubject: (resourceId, subjectId) ->
			url = SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_SUBJECTS + "/" + subjectId + "/unmanage"
			if !!resourceId
				url = url + "?resourceId=" + resourceId
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
				
		getDomains: () ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_DOMAINS
	]