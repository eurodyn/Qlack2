workflowManagerApp = angular.module("workflowApp")

workflowManagerApp.controller('IndexCtrl',['$scope', '$http', '$location', '$state', '$q', 'ProjectHttpService', ($scope, $http, $location, $state, $q, ProjectHttpService) ->
	
	$scope.projects = {
		dataSource: new kendo.data.DataSource(
			transport:
				read: (options) ->
					ProjectHttpService.getProjects().then(
						success = (result) ->	
							projectData = result.data
							options.success(projectData)
					)
		),
		dataTextField:"name",
		dataValueField:"id",
		change: (e) ->
				selectProject(e)
	}
	
	ProjectHttpService.getRecentProjects().then(
			success = (result) ->	
				$scope.recentProjects  = []	
				for project in result.data
					$scope.recentProjects.push(project)
				$scope.recentProjects
			error = (result) ->
				console.log "Error while getting recent projects" + result
				$scope.recentProjects  = []	
	)
	
	#when the user selects a project in the dropdown list, this function is called
	selectProject = (e) ->
		projectId = e.sender.value()
		if (projectId?)
			$state.go("resources", {projectId: projectId})
])
	