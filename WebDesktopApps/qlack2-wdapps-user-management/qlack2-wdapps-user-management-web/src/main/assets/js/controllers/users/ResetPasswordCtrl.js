angular.module("userManagement").controller("ResetPasswordCtrl", 
["$scope", "$state", "$stateParams", "UserService", "UserHttpService", 
"WindowService", "QFormValidation", "$window", "QNgPubSubService", "SERVICES",
function($scope, $state, $stateParams, UserService, UserHttpService, 
WindowService, QFormValidation, $window, QNgPubSubService, SERVICES) {
    $scope.user = {};
    
    $scope.cancel = function() {
      WindowService.closeWindow();
    };
    
    $scope.save = function() {
      var success;
      UserService.getUserPromise().then(success = function(selectedUser) {
        var error;
        UserHttpService.resetUserPassword(selectedUser.id, $scope.user).then(success = function(result) {
        	QNgPubSubService.publish(SERVICES.WD_SERVICE_TOPICS.NOTIFICATION_SERVICE, {
	            title: "usermanagement.password_reset_title",
	            content: "usermanagement.password_reset_content",
	            content_data: {
	              user: selectedUser.username
	            }
	          });
          WindowService.closeWindow();
          $state.go("users.user", {
            userId: selectedUser.id
          }, {
            reload: true
          });
        }, error = function(response) {
          QFormValidation.renderFormErrors($scope, $scope.userForm, response);
        });
      });
    };
  }
]);
