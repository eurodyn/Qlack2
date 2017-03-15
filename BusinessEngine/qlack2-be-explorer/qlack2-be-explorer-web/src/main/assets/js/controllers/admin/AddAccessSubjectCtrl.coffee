angular
	.module('explorer')
	.controller 'AddAccessSubjectCtrl', ['$scope', '$state', '$stateParams', 'WindowService', '$timeout', 'ConfigHttpService', \
	($scope, $state, $stateParams, WindowService, $timeout, ConfigHttpService) ->
		$scope.userListSource = new kendo.data.DataSource(
			transport:
				read: (options) ->
					ConfigHttpService.getAllUsers().then(
						success = (result) ->
							options.success(result.data)
					)
		)
		$scope.userTemplate = kendo.template($("#addUserTemplate").html())
		$scope.userAltTemplate = kendo.template($("#addUserAltTemplate").html())
		
#		Declaring addUserFilter here as an object in order to be able to bind it to the filter
#		input which resides in a child scope (inside the tab)
		$scope.addUserFilter = {}
		$scope.filterAddUsers = () ->
			$scope.userListSource.filter(
				logic: "or"
				filters: [
					{field: "username", operator: "contains", value: $scope.addUserFilter.text},
					{field: "firstName", operator: "contains", value: $scope.addUserFilter.text},
					{field: "lastName", operator: "contains", value: $scope.addUserFilter.text}
				]
			)
		
		$scope.groupTreeSource = new kendo.data.HierarchicalDataSource(
			transport:
				read: (options) ->
					ConfigHttpService.getAllGroups().then(
						success = (result) ->
#							Iterate over domains and groups in order to assign to them the correct icons
							nodes = []
							for node in result.data
								children = []
								if (node.childGroups?)
									children.push(node.childGroups)
									i = 0
									while (i < children.length)
										for innerNode in children[i]
											innerNode.icon = "group"
											if (innerNode.childGroups?)
												children.push(innerNode.childGroups)
										i++
								node.icon = "sitemap"
								nodes.push(node)
							nodes
							options.success(nodes)
							return
					)
			schema:
				model:
					id: "id"
					children: "childGroups"
		)
		$scope.groupTreeTemplate = kendo.template($("#addGroupTreeTemplate").html())
		$scope.groupTreeLoaded = (e) ->
			e.sender.expand(".k-item")
			return
			
		$scope.lists = {}
#		When selecting a user clear the group selection and vice versa
		$scope.changeUser = () ->
			if $scope.lists.userList.select()[0]?
				clearGroupSelection()
		$scope.selectGroup = () ->
#			Clear the user selection after angular has finished applying its own changes to the DOM
			$timeout(() ->
				clearUserSelection()
			)
			
		clearUserSelection = () ->
			$scope.lists.userList.clearSelection()
		clearGroupSelection = () ->
#			Apparently this is the way recommended by Kendo to clear a treeview selection
			$scope.lists.groupList.select(angular.element())
			
	
		$scope.cancel = () ->
			$scope.$parent.togglePopover()
			
		$scope.add = () ->
			selectedSubjectId
			if $scope.lists.groupList.select()[0]?
				selectedSubjectId = $scope.lists.groupList.dataItem($scope.lists.groupList.select()).id
			if $scope.lists.userList.select()[0]?
				selectedSubjectId = $scope.userListSource.getByUid(angular.element($scope.lists.userList.select()[0]).data("uid")).id
			ConfigHttpService.manageSubject($stateParams.projectId, selectedSubjectId).then(
				success = (result) ->
					$state.go $state.current, $stateParams,
						reload: true
			)
			return
	]