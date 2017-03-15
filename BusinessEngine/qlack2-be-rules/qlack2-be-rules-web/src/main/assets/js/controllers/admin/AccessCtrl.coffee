angular
	.module("rules")
	.controller "AccessCtrl", \
		["$scope", "$state", "$stateParams", "ProjectHttpService", \
		 ($scope,   $state,   $stateParams,   ProjectHttpService) ->

		$scope.accessTreeSource = new kendo.data.HierarchicalDataSource(
			transport:
				read: (options) ->
					ProjectHttpService.getTreeForAllProjects().then(
						success = (result) ->
							tree = []
							tree.push(
								id: ''
								key: 'generic_permissions'
								type: 'generic'
								icon: 'fa fa-group'
							)
							projects =
								id: ''
								key: 'projects'
								type: 'root'
								composite: true
								items: []
							for element in result.data
#								Remove categories since we do not need them in this tree
								project =
									id: element.projectId
									name: element.name
									type: element.type
									composite: true
									items: element.items[0..3]
								projects.items.push project
							addTreeIcons(projects)
							tree.push(projects)
							options.success(tree)
					)
			schema:
				model:
					id: "id"
					children: "items"
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
			if (!!item.composite)
				if (item.type isnt 'Project')
					e.preventDefault()

		addNodeIcon = (node) ->
			if !!node.composite
				node.icon = if node.type is "CompositeCategory" then "fa fa-tag" else "fa fa-folder-open"
			else
				node.icon = if node.type is "Category" then "fa fa-tag" else "fa fa-tasks"

		addTreeIcons = (root) ->
			treeTraverse(root, addNodeIcon)

		treeTraverse = (node, action) ->
			action(node)

			children = node.items or []
			for child in children
				treeTraverse(child, action)
	]
