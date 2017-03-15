angular.module("userManagement").controller("UserDetailsCtrl", 
["$scope", "$state", "$stateParams", "GroupHttpService", "SecuritySrv", 
function($scope, $state, $stateParams, GroupHttpService, SecuritySrv) {
    SecuritySrv.resolvePermission("WD_MANAGE_USERS");
    
    $scope.groupTreeSource = new kendo.data.HierarchicalDataSource({
      transport: {
        read: function(options) {
          var success;
          return GroupHttpService.getAllGroups().then(success = function(result) {
            options.success(result.data);
          });
        }
      },
      schema: {
        model: {
          id: "id",
          children: "childGroups"
        }
      }
    });
    
    $scope.groupTreeTemplate = kendo.template($("#groupTreeTemplate").html());
    $scope.groupCheckboxTemplate = kendo.template($("#groupCheckboxTemplate").html());
    
    $scope.groupTreeLoaded = function(e) {
      e.sender.expand(".k-item");
    };
    
    $scope.selectUserGroup = function(groupId) {
      var index;
      $scope.groupsHaveError = false;
      index = $scope.user.groups.indexOf(groupId);
      if (index > -1) {
        return $scope.user.groups.splice(index, 1);
      } else {
        return $scope.user.groups.push(groupId);
      }
    };
    
    $scope.$on('VALIDATION_ERROR_groups', function(event, data) {
      $scope.groupsHaveError = true;
      $scope.groupsError = data.translation;
    });
  }
]);
