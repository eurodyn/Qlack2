workflowManagerApp = angular.module("workflowApp")

workflowManagerApp.service "ProjectService", ["$q", "ProjectHttpService", ($q, ProjectHttpService) ->

		project = {}

		getProject: () ->
			project

		getProjectById: (projectId) ->
			deferred = $q.defer()
			if (!project? or (project.id isnt projectId))
				projectPromise = ProjectHttpService.getProject(projectId)
				$q.all([projectPromise]).then(
					success = (results) ->
						project = results[1].data
	
						deferred.resolve("success")
						return
					error = (result) ->
						deferred.resolve("error")
						return
				)
			else
				deferred.resolve(project)
		deferred.promise
	]