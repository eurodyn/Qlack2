angular.module("rules")
	.controller("ProjectListCtrl", ["$scope", "$state", "ProjectHttpService", "$translate", ($scope, $state, ProjectHttpService, $translate) ->

		$scope.projectsDataSource = new kendo.data.DataSource(
			data: []
		)

		$scope.recentProjects = []

		ProjectHttpService.getAll().then(
			success = (response) ->
				projects = response.data
				$scope.projectsDataSource.data(projects)
			error = (response) ->
				throw new Error("Cannot fetch projects")
		)

		ProjectHttpService.getRecent().then(
			success = (response) ->
				recentProjects = response.data
				$scope.recentProjects = recentProjects
			error = (response) ->
				throw new Error("Cannot fetch recent projects")
		)

		$scope.selectProject = (e) ->
			console.log(e)
			projectId = e.sender.value()

			if (!!projectId)
				$state.go("tree", {projectId: projectId})

		$scope.editProject = (projectId) ->
			$state.go("tree", {projectId: projectId})
			
		$scope.selectProjectTitle = ''
		$translate('select.project').then(
			(result) ->
				$scope.selectProjectTitle = result
		)

		return this
	])
	.controller("ProjectTreeCtrl", \
		["$scope", "$rootScope", "$state", "$stateParams", "$q", "SecuritySrv", "ProjectService", "ProjectHttpService", \
		 ($scope,   $rootScope,   $state,   $stateParams,   $q,   SecuritySrv,   ProjectService,   ProjectHttpService) ->

		projectId = $stateParams.projectId

		securityPromises = []
		securityPromises[0] = SecuritySrv.resolvePermission('RUL_MANAGE_CATEGORY', projectId)
		securityPromises[1] = SecuritySrv.resolvePermission('RUL_MANAGE_WORKING_SET', projectId)
		securityPromises[2] = SecuritySrv.resolvePermission('RUL_MANAGE_RULE', projectId)
		securityPromises[3] = SecuritySrv.resolvePermission('RUL_MANAGE_DATA_MODEL', projectId)
		securityPromises[4] = SecuritySrv.resolvePermission('RUL_MANAGE_LIBRARY', projectId)

		$q.all(securityPromises).then(
			success = () ->
				console.log("Project permissions resolved!")
				return $rootScope.permissions
		)

		$scope.projectTreeDataSource = new kendo.data.HierarchicalDataSource(
			data: []
		)

		ProjectHttpService.getTree(projectId).then(
			success = (response) ->
				tree = [ response.data ]

				addTreeIcons(tree)
				$scope.projectTreeDataSource.data(tree)

				# select the previously selected tree node if it exists
				resourceId = getResourceId()
				if (resourceId?)
					selectTreeNode(resourceId)

			error = (response) ->
				throw new Error("Cannot fetch project tree")
		)

		$scope.projectTreeItemTemplate = kendo.template($("#projectTreeItemTemplate").html())

		$scope.selectResource = (e) ->
			console.log(e)
			item = e.sender.dataItem(e.sender.current())

			if (!item.composite)
				parent = item.parentNode()
				ProjectService.setCategoryId(parent.categoryId or null)

				switch item.type
					when "WorkingSet"
						if ($stateParams.workingSetId isnt item.id)
							$state.go("tree.working-sets-edit", {workingSetId: item.id})
					when "Rule"
						if ($stateParams.ruleId isnt item.id)
							$state.go("tree.rules-edit", {ruleId: item.id})
					when "DataModel"
						if ($stateParams.modelId isnt item.id)
							$state.go("tree.models-edit", {modelId: item.id})
					when "Library"
						if ($stateParams.libraryId isnt item.id)
							$state.go("tree.libraries-edit", {libraryId: item.id})
					when "Category"
						if ($stateParams.categoryId isnt item.id)
							$state.go("tree.categories-edit", {categoryId: item.id})
					else
						throw new Error("Unknown item type")
			else
				e.preventDefault()

		# expand all tree nodes when data is bound
		$scope.expandTreeNodes = (e) ->
			e.sender.expand(".k-item")

		getResourceId = () ->
			if $state.is("tree.categories-edit")
				return $stateParams.categoryId
			else if $state.is("tree.rules-edit") or $state.is("tree.rules-edit.versions")
				return $stateParams.ruleId
			else if $state.is("tree.models-edit") or $state.is("tree.models-edit.versions")
				return $stateParams.modelId
			else if $state.is("tree.libraries-edit") or $state.is("tree.libraries-edit.versions")
				return $stateParams.libraryId
			else if $state.is("tree.working-sets-edit") or $state.is("tree.working-sets-edit.versions")
				return $stateParams.workingSetId
			else
				return null

		addNodeIcon = (node) ->
			if !!node.composite
				node.icon = if node.type is "CompositeCategory" then "fa fa-tag" else "fa fa-folder-open"
			else
				node.icon = if node.type is "Category" then "fa fa-tag" else "fa fa-tasks"

		addTreeIcons = (tree) ->
			root = tree[0]

			treeTraverse(root, addNodeIcon)

		treeTraverse = (node, action) ->
			action(node)

			children = node.items or []
			for child in children
				treeTraverse(child, action)

		selectTreeNode = (resourceId) ->
			tree = $scope.projectTreeDataSource.data()
			root = tree[0]

			nodes = treeFindById(root, resourceId)
			for node in nodes
				console.log(node)

			categoryId = ProjectService.getCategoryId()
			selectedNode = findSelectedNode(nodes, categoryId)

			parent = node.parentNode()
			ProjectService.setCategoryId(parent.categoryId or null)

			treeViewNode = $scope.projectTreeView.findByUid(selectedNode.uid)
			$scope.projectTreeView.select(treeViewNode)

		findSelectedNode = (nodes, categoryId) ->
			if (nodes.length is 0)
				throw new Error("Cannot find resource in tree")
			else if (nodes.length is 1)
				return nodes[0]
			else
				# no category selected
				if (!categoryId)
					return nodes[0]
				else
					# resource still has selected category
					for node in nodes
						parent = node.parentNode()
						if (parent.categoryId is categoryId)
							return node

					# resource has been removed from selected category
					return nodes[0]

		treeFindById = (root, nodeId) ->
			results = []
			predicate = (node) ->
				node.id is nodeId

			treeFind(root, predicate, results)
			return results

		treeFind = (node, predicate, results) ->
			if (predicate(node))
				results.push(node)

			children = node.items or []
			for child in children
				treeFind(child, predicate, results)

		return this
	])

