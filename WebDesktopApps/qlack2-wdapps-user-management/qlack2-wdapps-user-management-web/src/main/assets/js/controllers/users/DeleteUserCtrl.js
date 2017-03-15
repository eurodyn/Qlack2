angular.module("userManagement").controller("DeleteUserCtrl", 
["$scope", "$state", "$stateParams", "UserService", "UserHttpService", 
"WindowService", "$window", "QNgPubSubService", "SERVICES",
function($scope, $state, $stateParams, UserService, UserHttpService, 
WindowService, $window, QNgPubSubService, SERVICES) {
    $scope.cancel = function() {
      WindowService.closeWindow();
    };
    
    $scope["delete"] = function() {
      var success;
      UserService.getUserPromise().then(success = function(user) {
        UserHttpService.deleteUser(user.id).then(success = function(result) {
        	QNgPubSubService.publish(SERVICES.WD_SERVICE_TOPICS.NOTIFICATION_SERVICE, {
	            title: "usermanagement.user_deleted_title",
	            content: "usermanagement.user_deleted_content",
	            content_data: {
	              user: user.username
	            }
	          });
          
          WindowService.closeWindow();
          
          $state.go("users", {}, {
            reload: true
          });
        });
      });
    };
  }
]);
