workflowManagerApp = angular.module("workflowApp")

workflowManagerApp.service "UtilService", ["SERVICES", "$q", "SecuritySrv", (SERVICES, $q, SecuritySrv) ->
		
	checkPermissions: (operation, workflowId, projectId) ->
		deferred = $q.defer()
		SecuritySrv.resolvePermission(operation, workflowId).then((permitted) ->
			if permitted is null || permitted is ''
				SecuritySrv.resolvePermission(operation, projectId).then((permitted) ->
					deferred.resolve(permitted)
				)
			else
				deferred.resolve(permitted)
		)
		deferred.promise
			
	createUUID: ->
		return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) ->
			r = Math.random() * 16 | 0
			v = if c is 'x' then r else (r & 0x3|0x8)
			v.toString(16)
		)	
	#Function for opening a modal window and setting custom options
	openWindow: (modal, options) ->
		modal.setOptions(options)
		if options.content?
			modal.refresh({
				url: options.content})
			modal.center().open()
	]
