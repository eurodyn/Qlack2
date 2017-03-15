angular
	.module("workflowApp")
	.controller "ParentCtrl", ["$scope", ($scope) ->
		$scope.project = {
			selectedProjectId: null
		}
	]