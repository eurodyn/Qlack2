angular.module("userManagement").controller("CreateGroupCtrl", 
["$scope", "$state", "$stateParams", "GroupService", "GroupHttpService", 
 "QFormValidation", "$window", "QNgPubSubService", "SERVICES",
 function($scope, $state, $stateParams, GroupService, GroupHttpService, 
QFormValidation, $window, QNgPubSubService, SERVICES) {
    var NotificationSrv;
    
    $scope.group = {};
    GroupService.setGroupPromise(null);
    
    $scope.cancel = function() {
      $state.go("groups.group", {
        groupId: $stateParams.groupId
      }, {
        reload: true
      });
    };
    
    $scope.save = function() {
      var error, success;
      GroupHttpService.createGroup($scope.group, $stateParams.groupId).then(success = function(response) {
        notificationTitle;
        notificationContent;
        var notificationContent, notificationTitle;
        if ($stateParams.groupId === '0') {
          notificationTitle = "usermanagement.domain_created_title";
          notificationContent = "usermanagement.domain_created_content";
        } else {
          notificationTitle = "usermanagement.group_created_title";
          notificationContent = "usermanagement.group_created_content";
        }
        
        QNgPubSubService.publish(SERVICES.WD_SERVICE_TOPICS.NOTIFICATION_SERVICE, {
        	title: notificationTitle,
            content: notificationContent,
            content_data: {
              group: $scope.group.name
            },
        });
        
        $state.go("groups.group", {
          groupId: response.data
        }, {
          reload: true
        });
      }, error = function(response) {
        QFormValidation.renderFormErrors($scope, $scope.groupForm, response);
      });
    };
  }
]);
