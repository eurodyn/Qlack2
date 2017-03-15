angular.module('userManagement')
.controller('UserListCtrl', 
['$scope', '$stateParams', 'UserHttpService', 
function($scope, $stateParams, UserHttpService) {
    $scope.userListSource = new kendo.data.DataSource({
      transport: {
        read: function(options) {
          var success;
          return UserHttpService.getAllUsers().then(success = function(result) {
            return options.success(result.data);
          });
        }
      }
    });
    $scope.userTemplate = kendo.template($("#userTemplate").html());
    $scope.userAltTemplate = kendo.template($("#userAltTemplate").html());
    $scope.userListLoaded = function(e) {
      var selectedUser, userIndex;
      if ($stateParams.userId != null) {
        selectedUser = e.sender.dataSource.get($stateParams.userId);
        userIndex = e.sender.dataSource.indexOf(selectedUser);
        e.sender.select(e.sender.element.children().eq(userIndex));
      }
    };
    $scope.filterUsers = function() {
      return $scope.userListSource.filter({
        logic: "or",
        filters: [
          {
            field: "username",
            operator: "contains",
            value: $scope.filter
          }, {
            field: "firstName",
            operator: "contains",
            value: $scope.filter
          }, {
            field: "lastName",
            operator: "contains",
            value: $scope.filter
          }
        ]
      });
    };
  }
]);
