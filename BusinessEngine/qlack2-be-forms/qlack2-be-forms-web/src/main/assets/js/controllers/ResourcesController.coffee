formsManagerApp = angular.module("formsManagerApp")

formsManagerApp.controller('ResourcesCtrl', ['$scope', '$state','$stateParams', 'ProjectHttpService', 'SecuritySrv', 'ResourcesService', \
		($scope, $state, $stateParams, ProjectHttpService, SecuritySrv, ResourcesService) ->
	projectId = $stateParams.projectId

	SecuritySrv.resolvePermission('FRM_MANAGE_FORM', projectId)
	SecuritySrv.resolvePermission('FRM_MANAGE_CATEGORY', projectId)

	$scope.resourcesTemplate = kendo.template($("#resourcesTemplate").html())

	$scope.formResourcesDataSource = new kendo.data.HierarchicalDataSource(
		transport:
			read: (options) ->
				ProjectHttpService.getFormResourcesAsTree(projectId).then(
					success = (result) ->
						nodes = []
						project =
							id: result.data.id
							name: result.data.name
							type: result.data.type
							items: []
						for node in result.data.items
							if node.items?
								for innerNode in node.items
									if innerNode.items?
										for leafNode in innerNode.items
											leafNode.state = "resources." + leafNode.type
									else
										innerNode.state = "resources." + innerNode.type
							project.items.push(node)
						nodes.push(project)
						options.success(nodes)

						#Select the previously selected tree node if it exists
						$scope.setSelectedTreeNode()
					error = () ->
						options.success([])
				)
		schema:
			model:
				id: "id"
				children: "items"
	)

	$scope.selectTreeNode = (e) ->
		item = e.sender.dataItem(e.sender.current())
		if item.state?
			categoryId = null
			if item.type == 'form' && item.parentNode().type == 'category'
				categoryId = item.parentNode().id
				ResourcesService.setCategoryId(categoryId)
			else
				ResourcesService.setCategoryId(null)
		else
			e.preventDefault()
			return

		if $stateParams.resourceId != item.id
			$state.go(item.state, {resourceId: item.id})

		console.log "Selected node with id = " + item.id + " and type = " + item.type + " and state " + item.state

	#Expands all tree nodes when data is bound
	$scope.expandTreeNodes = (e) ->
		e.sender.expand(".k-item")

	$scope.setSelectedTreeNode = () ->
		if $stateParams.resourceId?
			dataItemUid = null

			data = $scope.formResourcesDataSource.data()

			if $state.is('resources.category')
				items = data[0].items[1].items
				dataItemUid = getDataItem(items)
			else if $state.is('resources.form') || $state.is('resources.form.version')
				items = data[0].items[0].items

				categoryId = ResourcesService.getCategoryId()
				if categoryId?
					#If the categoryId is set, then get this category item and
					#select the form under this category
					for item in items
						if item.id == categoryId
							category = item

					dataItemUid = getDataItem(category.items)
				else
					#If the categoryId is not set then either the form belongs to no category
					#or the state of the form is accessed directly and the first node in the tree
					#for the form should be selected
					for item in items
						if item.type == 'category'
							dataItemUid = getDataItem(item.items)
							if dataItemUid?
								break
						else
							if item.id == $stateParams.resourceId
								dataItemUid = item.uid
								break

			#Find the tree node with the given identifier
			resourcesTreeElement = $scope.resourcesTreeView.findByUid(dataItemUid)

			#Set the selected tree node
			$scope.resourcesTreeView.select(resourcesTreeElement)
		else
			$scope.resourcesTreeView.select($())

	getDataItem = (items) ->
		for item in items
			if item.id == $stateParams.resourceId
				dataItemUid = item.uid
				break
		dataItemUid
])