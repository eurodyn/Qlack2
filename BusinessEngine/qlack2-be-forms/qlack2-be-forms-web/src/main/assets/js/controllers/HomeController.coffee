angular
	.module("formsManagerApp")
	.controller "HomeCtrl", ['$scope', '$state', 'ProjectHttpService', '$translate', ($scope, $state, ProjectHttpService, $translate) ->

		$scope.projectsDataSource = new kendo.data.DataSource(
			transport:
				read: (options) ->
					ProjectHttpService.getProjects().then(
						success = (result) ->
							options.success(result.data)
						error = () ->
							options.success([])
					)
		)

		ProjectHttpService.getRecentProjects().then(
			success = (result) ->
				$scope.recentProjects  = []
				for project in result.data
					$scope.recentProjects.push(project)
				$scope.recentProjects
			error = () ->
				$scope.recentProjects  = []

		)
		
		$scope.selectProjectTitle = ''
		$translate('select_project').then(
			(result) ->
				$scope.selectProjectTitle = result
		)

		#Function called when the user selects a project in the dropdown list
		$scope.selectProject = (e) ->
			projectId = e.sender.value()

			if (projectId?)
				$state.go("resources", {projectId: projectId})

		$scope.editProject = (projectId) ->
			$state.go("resources", {projectId: projectId})
	]
