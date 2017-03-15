angular
	.module('explorer')
	.controller 'ProjectListCtrl', ['$scope', '$stateParams', 'ProjectHttpService', \
	($scope, $stateParams, ProjectHttpService) ->
		$scope.projectListSource = new kendo.data.DataSource(
			transport:
				read: (options) ->
					ProjectHttpService.getAllProjects().then(
						success = (result) ->
							options.success(result.data)
					)
		)
		$scope.projectTemplate = kendo.template($("#projectTemplate").html())
		$scope.projectAltTemplate = kendo.template($("#projectAltTemplate").html())
		
		$scope.projectListLoaded = (e) ->
#			If a project has been select it select it in the list
			if $stateParams.projectId?
				selectedProject = e.sender.dataSource.get($stateParams.projectId)
				projectIndex = e.sender.dataSource.indexOf(selectedProject)
				e.sender.select(e.sender.element.children().eq(projectIndex))
				return
		
		return
	]