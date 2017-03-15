angular.module("rules")
	.controller("IndexCtrl", ["$scope", "$stateParams", "$http", "SERVICES", ($scope, $stateParams, $http, SERVICES) ->
		$scope.hasPendingRequests = () ->
			$http.pendingRequests.length > 0
	])
