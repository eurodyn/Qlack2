angular.module("appManagement").controller("AccessCtrl", [
  "$scope", "$state", "$stateParams", "ApplicationHttpService", function($scope, $state, $stateParams, ApplicationHttpService) {
    $scope.appTreeSource = new kendo.data.HierarchicalDataSource({
      transport: {
        read: function(options) {
          var success;
          return ApplicationHttpService.getApplicationsAsTree().then(success = function(result) {
            var element, i, len, ref, tree;
            tree = [];
            tree.push({
              id: '',
              key: 'generic_permissions'
            });
            ref = result.data;
            for (i = 0, len = ref.length; i < len; i++) {
              element = ref[i];
              tree.push(element);
            }
            return options.success(tree);
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
      var selectedApp;
      e.sender.expand(".k-item");
      if ($stateParams.appId != null) {
        selectedApp = e.sender.findByUid(e.sender.dataSource.get($stateParams.appId).uid);
        e.sender.select(selectedApp);
      }
    };
    return $scope.selectApp = function(e) {
      var item;
      item = e.sender.dataItem(e.sender.current());
      if ((item.applications != null)) {
        e.preventDefault();
      }
    };
  }
]);
