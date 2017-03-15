angular.module("rules")
	.service("Util", [ "SecuritySrv", (SecuritySrv) ->

		generateUUID: ->
			return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) ->
				r = Math.random() * 16 | 0
				v = if c is 'x' then r else (r & 0x3|0x8)
				v.toString(16)
			)

		checkPermissions: (operation, resourceId, projectId) ->
			return SecuritySrv.resolvePermission(operation, resourceId).then(
				success = (permitted) ->
					console.log("check: " + operation + " " + resourceId + " " + permitted)
					if (permitted isnt null and permitted isnt '')
						return permitted
					else
						return SecuritySrv.resolvePermission(operation, projectId).then(
							success = (permitted) ->
								console.log("check: " + operation + " " + projectId + " " + permitted)
								return permitted
						)
			)
	])
