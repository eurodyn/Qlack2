angular
	.module('explorer')
	.service 'ProjectHttpService', ['$http', 'SERVICES', ($http, SERVICES) ->
		getAllProjects: () ->
			$http
				method: "GET"
				url:  SERVICES._PREFIX + SERVICES.PROJECTS

		getProject: (id) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.PROJECTS + "/" + id

		createProject: (project) ->
			$http
				method: "POST"
				url: SERVICES._PREFIX + SERVICES.PROJECTS
				data: project

		updateProject: (project) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.PROJECTS + "/" + project.id
				data: project

		deleteProject: (id) ->
			$http
				method: "DELETE"
				url: SERVICES._PREFIX + SERVICES.PROJECTS + "/" + id
	]