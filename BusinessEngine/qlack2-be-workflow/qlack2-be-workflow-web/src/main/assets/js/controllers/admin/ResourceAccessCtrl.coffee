angular
	.module('workflowApp')
	.controller 'ResourceAccessCtrl', ['$scope', '$state', '$stateParams', 'WindowService', 'ConfigHttpService', '$q', '$timeout', \
	($scope, $state, $stateParams, WindowService, ConfigHttpService, $q, $timeout) ->
		$scope.resourceId = $stateParams.resourceId
		
		$scope.subjectsDataSource = new kendo.data.DataSource(
			transport:
				read: (options) ->
					$q.all([ConfigHttpService.getManagedUsers($stateParams.resourceId), ConfigHttpService.getManagedGroups($stateParams.resourceId)]).then(
						success = ([usersResult, groupsResult]) ->
							subjects = []
							for user in usersResult.data
								userSubject = 
									id: user.id
									name: user.firstName + " " + user.lastName
									description: user.username
								if (user.superadmin)
									userSubject.type = "superadmin"
								else
									userSubject.type = "user"
								subjects.push(userSubject)
							for group in groupsResult.data
								groupSubject = 
									id: group.id
									name: group.name
									description: null
								if (group.parentGroup?)
									groupSubject.type = "group"
									parent = group.parentGroup
									while (parent?)
										groupSubject.name = parent.name + " / " + groupSubject.name
										parent = parent.parentGroup
								else 
									groupSubject.type = "domain"
								subjects.push(groupSubject)
							
							options.success(subjects)
							return
					)
			sort:
				field: "name"
				dir: "asc"
		)
		$scope.subjectTemplate = kendo.template($("#subjectTemplate").html())
		$scope.subjectAltTemplate = kendo.template($("#subjectAltTemplate").html())
		
		$scope.filterSubjects = () ->
			$scope.subjectsDataSource.filter(
				logic: "or"
				filters: [
					{field: "name", operator: "contains", value: $scope.subjectFilter},
					{field: "description", operator: "contains", value: $scope.subjectFilter}
				]
			)
		
		$scope.subjectListLoaded = (e) ->
#			If a user has been select it select it in the list
			$timeout(() ->
				if $stateParams.ownerId?
					selectedUser = e.sender.dataSource.get($stateParams.ownerId)
					listItem = e.sender.items().filter("[data-uid='" + selectedUser.uid + "']")
					e.sender.select(listItem)
					return
			)
		
		$scope.selectedSubject = null		
		$scope.subjectChanged = (e) ->
			$scope.selectedSubject = $scope.subjectsDataSource.getByUid(angular.element(e.sender.select()[0]).data("uid"))
			
		$scope.showPopover = false
		$scope.togglePopover = () ->
			$scope.showPopover = !$scope.showPopover
			
		$scope.removeSubject = () ->
			WindowService.openWindow("remove_from_access_management", "views/admin/removeAccessSubject.html", {subjectId: $scope.selectedSubject.id})
	]