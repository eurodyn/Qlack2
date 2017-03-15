angular.module("appManagement")
.controller("AppsCtrl", ["$scope", "$state", "$stateParams", "ApplicationHttpService", 
function($scope, $state, $stateParams, ApplicationHttpService) {
	
	$scope.appTreeSource = new kendo.data.HierarchicalDataSource({
		transport: {
			read: function(options) {
				ApplicationHttpService.getApplicationsAsTree().then(function(result) {
					return options.success(result.data);
				});
			}
		},
		schema: {
			model: {
				id: "id",
				children: "applications"
			}
		}
	});

	$scope.appTreeTemplate = kendo.template($("#appTreeTemplate").html());

	$scope.initTree = function(e) {
		e.sender.expand(".k-item");
		if ($stateParams.applicationId != null) {
			var selectedApp = e.sender.findByUid(e.sender.dataSource.get($stateParams.applicationId).uid);
			e.sender.select(selectedApp);
		}
	};

	$scope.selectApp = function(e) {
		var item = e.sender.dataItem(e.sender.current());
		if (item.icon == null) {
			e.preventDefault();
		} else {
			$state.go("apps.application", {
				applicationId: item.id
			});
		}
	};
}]);
