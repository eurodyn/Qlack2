angular.module("userManagement").controller("DeleteGroupCtrl", 
["$scope", "$state", "$stateParams", "GroupService", "GroupHttpService", 
"WindowService", "$window", "QNgPubSubService", "SERVICES",
function($scope, $state, $stateParams, GroupService, GroupHttpService, 
WindowService, $window, QNgPubSubService, SERVICES) {
    
	if ($stateParams.domain === "true") {
      $scope.domain = true;
    } else {
      $scope.domain = false;
    }
    
    $scope.cancel = function() {
      return WindowService.closeWindow();
    };
    
    $scope["delete"] = function() {
      var success;
      GroupService.getGroupPromise().then(success = function(group) {
        GroupHttpService.deleteGroup(group.id).then(success = function(result) {
          notificationTitle;
          notificationContent;
          var notificationContent, notificationTitle;
          if ($stateParams.domain === "true") {
            notificationTitle = "usermanagement.domain_deleted_title";
            notificationContent = "usermanagement.domain_deleted_content";
          } else {
            notificationTitle = "usermanagement.group_deleted_title";
            notificationContent = "usermanagement.group_deleted_content";
          }
          QNgPubSubService.publish(SERVICES.WD_SERVICE_TOPICS.NOTIFICATION_SERVICE, {
            title: notificationTitle,
            content: notificationContent,
            content_data: {
              group: group.name
            }
          });
          
          WindowService.closeWindow();
          $state.go("groups", {}, {
            reload: true
          });
        });
      });
    };
  }
]);
