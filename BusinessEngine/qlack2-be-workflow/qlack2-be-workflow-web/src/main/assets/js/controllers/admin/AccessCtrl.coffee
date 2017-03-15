angular
	.module("workflowApp")
	.controller "AccessCtrl", ["$scope", "$state", "$stateParams", "ProjectHttpService", \
	($scope, $state, $stateParams, ProjectHttpService) ->
		$scope.accessTreeSource = new kendo.data.HierarchicalDataSource(
			transport:
				read: (options) ->
					ProjectHttpService.getWorkflowResourcesAsTreeForAllProjects().then(
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
								nodes: []
							for element in result.data
#								Remove categories since we do not need them in this tree
#								and nest workflows directly under project
								project = 
									id: element.id
									name: element.name
									type: element.type
									nodes: element.nodes[0].nodes
								projects.nodes.push project
							tree.push(projects)
							options.success(tree)
					)
			schema:
				model:
					id: "id"
					children: "nodes"
		)
		
		$scope.accessTreeTemplate = kendo.template($("#accessTreeTemplate").html())
		
		$scope.initTree = (e) ->
			e.sender.expand(".k-item")
#			If a resource has been specified select it in the tree
			if $stateParams.resourceId?
#				Find the selected App given its ID according to Kendo treeview documentation
				selectedApp = e.sender.findByUid(e.sender.dataSource.get($stateParams.resourceId).uid)
				e.sender.select(selectedApp)
				return
		
		$scope.selectResource = (e) ->
			item = e.sender.dataItem(e.sender.current())
			# Block user from selecting the projects root element.
			# or from selecting categories
			if (item.type is 'root') or (item.type is 'category')
				e.preventDefault()
				return
	]