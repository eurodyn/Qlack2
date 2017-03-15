angular
	.module("formsManagerApp")
	.service "UtilService", ['$q', 'SecuritySrv', ($q, SecuritySrv) ->
		createUUID: ->
			return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) ->
				r = Math.random() * 16 | 0
				v = if c is 'x' then r else (r & 0x3|0x8)
				v.toString(16)
			)

		checkPermissions: (operation, formId, projectId) ->
			deferred = $q.defer()
			SecuritySrv.resolvePermission(operation, formId).then((permitted) ->
				console.log operation + " " + formId + " " + permitted
				if permitted is null || permitted is ''
					SecuritySrv.resolvePermission(operation, projectId).then((permitted) ->
						console.log operation + " " + projectId + " " + permitted
						deferred.resolve(permitted)
					)
				else
					deferred.resolve(permitted)
			)
			deferred.promise
	]