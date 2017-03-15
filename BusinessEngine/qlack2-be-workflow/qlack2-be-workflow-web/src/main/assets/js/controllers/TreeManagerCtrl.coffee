workflowManagerApp = angular.module("workflowApp")

workflowManagerApp.controller('TreeManagerCtrl',['$scope', '$http', '$location', '$state', '$stateParams', 'ProjectHttpService', 'SecuritySrv', 'ResourceService', ($scope, $http, $location, $state, $stateParams, ProjectHttpService, SecuritySrv, ResourceService) ->
	
	projectId = $stateParams.projectId
	console.log "selectedProjectId: " + projectId
	
	SecuritySrv.resolvePermission('WFL_MANAGE_WORKFLOW', projectId)
	SecuritySrv.resolvePermission('WFL_MANAGE_CATEGORY', projectId)

	$scope.workflowNodes = {
		dataSource: new kendo.data.HierarchicalDataSource(
			transport:
				read: (options) ->
					ProjectHttpService.getProjectTreeResources(projectId).then(
						success = (result) ->
							groups = []
							project = 
								id: result.data.id
								name: result.data.name
								type: result.data.type
								nodes: []
							groups.push(project)
							root = result.data
							for group in root.nodes
								for node in group.nodes
									node.icon = "fa fa-tag"
									if node.nodes?
										for innerNode in node.nodes
											innerNode.icon = if innerNode.type? and innerNode.type == 'category' then "fa fa-tag" else "fa fa-tasks"
											innerNode.state = "resources." + innerNode.type
									else
										node.state = "resources." + node.type
								project.nodes.push(group)
							groups
							options.success(groups)
							#Select the previously selected tree node if it exists
							setSelectedTreeNode()
						error = () ->
							options.success([])
					)
			schema:
				model:
					id: "id"
					children: "nodes"
		),
		dataTextField:"['name', 'name']",
		template: kendo.template($("#resourcesTemplate").html()),
		select: (e) ->
			selectTreeNode(e)
		dataBound: (e) ->
			expandTreeNodes(e)
	}
	
	$scope.projectCategories = {
		dataSource: new kendo.data.DataSource(
			transport:
				read: (options) ->
					ProjectHttpService.getCategoriesByProjectId(projectId).then(
						success = (result) ->	
							projectData = result.data
							options.success(projectData)
					)
		)
	}
	
	#Expands all tree nodes when data is bound
	expandTreeNodes = (e) ->
		e.sender.expand(".k-item")
	
	selectTreeNode = (e) ->
		item = e.sender.dataItem(e.sender.current())
		if item.state?
			categoryId = null
			if item.type == 'workflow' && item.parentNode().type == 'category'
				categoryId = item.parentNode().id
			ResourceService.setCategoryId(categoryId)
			
			$state.go(item.state, {resourceId: item.id}, {reload:true})
		console.log "Selected node with id = " + item.id + " and type = " + item.type + " and state " + item.state
		
		
	setSelectedTreeNode = () ->
		if $stateParams.resourceId?
			dataItemUid = null
			data = $scope.workflowNodes.dataSource.data()
			if $state.is('resources.category')
				items = data[1].nodes
				dataItemUid = getDataItem(items)
			else if $state.is('resources.workflow') || $state.is('resources.workflow.version')
				items = data[0].nodes
				categoryId = ResourceService.getCategoryId()
				if categoryId?
					#If the categoryId is set, then get this category item and
					#select the workflow under this category
					for item in items
						if item.id == categoryId
							dataItemUid = getDataItem(item.nodes)
							break			
				else
					#the first node in the tree for the workflow should be selected
					for item in items
						if item.type == 'category'
							dataItemUid = getDataItem(item.nodes)
						else
							if item.id == $stateParams.resourceId
								dataItemUid = item.uid
						if dataItemUid?
							break
			#Find the tree node with the given identifier
			resourcesTreeElement = $scope.workflowsTreeView.findByUid(dataItemUid)
			#Set the selected tree node
			$scope.workflowsTreeView.select(resourcesTreeElement)
		else
			$scope.workflowsTreeView.select($())

	getDataItem = (items) ->
		for item in items
			if item.id == $stateParams.resourceId
				dataItemUid = item.uid
				break
		dataItemUid
])
