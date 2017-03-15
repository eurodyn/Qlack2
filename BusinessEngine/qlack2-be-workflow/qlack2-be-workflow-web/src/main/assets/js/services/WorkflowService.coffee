workflowManagerApp = angular.module("workflowApp")

workflowManagerApp.service "WorkflowService", ["$q", "WorkflowHttpService", "WorkflowVersionHttpService", "UtilService", "SecuritySrv", ($q, WorkflowHttpService, WorkflowVersionHttpService, UtilService, SecuritySrv) ->
		
		workflow = null
		workflowVersion = null
		actions = []

		canViewWorkflow = null
		canManageWorkflow = null
		canExecuteWorkflow = null
		canLockWorkflow = null
		canUnlockWorkflow = null
		rebindVal = null
		
		canEditVersion = null
		
		canExecuteVersion = null

		user = SecuritySrv.getUser()

		getWorkflow: () ->
			workflow

		getWorkflowVersion: () ->
			workflowVersion

		getWorkflowById: (workflowId, projectId, forceReload) ->
			deferred = $q.defer()
			#Only load the form from the server if force reload has been requested or if the existing form is stale
			if (forceReload or !workflow? or (workflow.id isnt workflowId))
				workflow = {}
				workflowVersion = {}
				
				# SECURITY PROMISES
				canViewWorkflowDeferred = UtilService.checkPermissions('WFL_VIEW_WORKFLOW', workflowId, projectId)
				canManageWorkflowDeferred = UtilService.checkPermissions('WFL_MANAGE_WORKFLOW', workflowId, projectId)
				canExecuteWorkflowDeferred = UtilService.checkPermissions('WFL_EXECUTE_WORKFLOW', workflowId, projectId)
				canLockWorkflowDeferred = UtilService.checkPermissions('WFL_LOCK_WORKFLOW', workflowId, projectId)
				canUnlockWorkflowDeferred = UtilService.checkPermissions('WFL_UNLOCK_ANY_WORKFLOW', workflowId, projectId)

				$q.all([canViewWorkflowDeferred, canManageWorkflowDeferred, canLockWorkflowDeferred, canUnlockWorkflowDeferred, canExecuteWorkflowDeferred]).then((results) ->
					canViewWorkflow = results[0]
					canManageWorkflow = results[1]
					canLockWorkflow = results[2]
					canUnlockWorkflow = results[3]
					canExecuteWorkflow = results[4]

					console.log "canViewWorkflow: " + canViewWorkflow
					console.log "canManageWorkflow: " + canManageWorkflow
					console.log "canLockWorkflow: " + canLockWorkflow
					console.log "canUnlockWorkflow: " + canUnlockWorkflow
					console.log "canExecuteWorkflow: " + canExecuteWorkflow

					WorkflowHttpService.getWorkflow(workflowId).then(
						success = (result) ->
							workflow = result.data
							deferred.resolve(result.data)
							return
						error = (result) ->
							deferred.resolve("error while fetching workflow")
							return
					)
				)
			else
				deferred.resolve(workflow)
			deferred.promise
		
		getWorkflowVersionsByWorkflowId: (workflowId) ->
			deferred = $q.defer()
			WorkflowHttpService.getWorkflowVersions(workflowId).then(
				success = (result) ->
					workflow.versions = result.data
					deferred.resolve(result.data)
					return
			)
			deferred.promise

		getWorkflowVersionById: (versionId) ->
			deferred = $q.defer()
			WorkflowVersionHttpService.getWorkflowVersion(versionId).then(
				success = (result) ->
					workflowVersion = result.data
					deferred.resolve(result.data)
					return
				error = (result) ->
					deferred.resolve("error while fetching workflow version")
					return
			)
			deferred.promise
			
		getActions: () ->
			actions

		setActions: () ->
			actions.pop() while actions.length > 0
		
			if canViewWorkflow
				actions.push({ id: "6", name: "export", value: "exportAction", icon: "fa-sign-out fa-lg action", order: 6 })

			if canManageWorkflow
				actions.push({ id: "1", name: "delete_workflow", value: "deleteWorkflowAction", icon: "fa-trash-o", order: 1 })
				actions.push({ id: "2", name: "create_version", value: "createVersionAction", cssClass: "separator", icon: "fa-camera-retro fa-lg action", order: 2 })
				actions.push({ id: "5", name: "import", value: "importAction", icon: "fa-sign-in fa-lg action", order: 5 })
		
			if workflowVersion.id and workflowVersion.id isnt ''
				canEditVersion = true
				rebindVal = false
				canExecuteVersion = false
				
				if workflowVersion.state? && workflowVersion.state == 1
					canEditVersion = false
					canExecuteVersion = true
					if canManageWorkflow
						actions.push({ id: "7", name: "delete_version", value: "deleteVersionAction", icon: "fa-times fa-lg action", order: 7 })
				else
					if workflowVersion.lockedBy?
						if userId? && userId == workflowVersion.lockedBy
							if canLockWorkflow || canUnlockWorkflow
								actions.push({ id: "4", name: "unlock", value: "unlockAction", icon: "fa-unlock fa-lg action", order: 4 })
							if canManageWorkflow
								actions.push({ id: "7", name: "delete_version", value: "deleteVersionAction", icon: "fa-times fa-lg action", order: 7 })
								actions.push({ id: "8", name: "finalize", value: "finaliseVersionAction", icon: "fa-thumbs-o-up fa-lg action", order: 8 })
								if workflowVersion.enableTesting
									canExecuteVersion = true
									actions.push({ name: "disable_testing", value: "disableTestingVersionAction", icon: "fa-flag", order: 9 })
								else
									actions.push({ name: "enable_testing", value: "enableTestingVersionAction", icon: "fa-flag", order: 9 })
						else
							canEditVersion = false
							if canUnlockWorkflow
								actions.push({ id: "4", name: "unlock", value: "unlockAction", icon: "fa-unlock fa-lg action", order: 4 })
					else
						if canLockWorkflow
							actions.push({ id: "3", name: "lock", value: "lockAction", icon: "fa-lock fa-lg action", order: 3 })
						if canManageWorkflow
							actions.push({ id: "7", name: "delete_version", value: "deleteVersionAction", icon: "fa-times fa-lg action", order: 7 })
							actions.push({ id: "8", name: "finalize", value: "finaliseVersionAction", icon: "fa-thumbs-o-up fa-lg action", order: 8 })
							if workflowVersion.enableTesting
								canExecuteVersion = true
								actions.push({ name: "disable_testing", value: "disableTestingVersionAction", icon: "fa-flag", order: 9 })
							else
								actions.push({ name: "enable_testing", value: "enableTestingVersionAction", icon: "fa-flag", order: 9 })
			rebindVal = canManageWorkflow && canEditVersion

		getCanViewWorkflow: () ->
			canViewWorkflow

		getCanManageWorkflow: () ->
			canManageWorkflow

		getCanLockWorkflow: () ->
			canLockWorkflow

		getCanUnlockWorkflow: () ->
			canUnlockWorkflow
			
		getCanExecuteWorkflow: () ->
			canExecuteWorkflow
			
		getCanExecuteVersion: () ->
			canExecuteVersion
			
		getCanEditVersion: () ->
			canEditVersion
		
		getRebindVal: () ->
			rebindVal
	]