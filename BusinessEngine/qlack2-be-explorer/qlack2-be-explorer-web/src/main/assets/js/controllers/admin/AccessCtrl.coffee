angular
	.module("explorer")
	.controller "AccessCtrl", ["$scope", "$state", "$stateParams", "ProjectHttpService", \
	($scope, $state, $stateParams, ProjectHttpService) ->
		$scope.accessTreeSource = new kendo.data.HierarchicalDataSource(
			transport:
				read: (options) ->
					ProjectHttpService.getAllProjects().then(
						success = (result) ->
							tree = []
							tree.push(
								id: ''
								key: 'generic_permissions'
								type: 'generic'
							)
							projects =
								id: ''
								key: 'projects'
								type: 'root'
								projects: []
							for element in result.data
								element.type = 'project'
								projects.projects.push element
							tree.push(projects)
							options.success(tree)
					)
			schema:
				model:
					id: "id"
					children: "projects"
		)
		
		$scope.accessTreeTemplate = kendo.template($("#accessTreeTemplate").html())
		
		$scope.initTree = (e) ->
			e.sender.expand(".k-item")
#			If an application has been specified select it in the tree
			if $stateParams.projectId?
#				Find the selected App given its ID according to Kendo treeview documentation
				selectedApp = e.sender.findByUid(e.sender.dataSource.get($stateParams.projectId).uid)
				e.sender.select(selectedApp)
				return
		
		$scope.selectResource = (e) ->
			item = e.sender.dataItem(e.sender.current())
			# Block user from selecting application groups.
			if (item.projects?)
				e.preventDefault()
				return
	]