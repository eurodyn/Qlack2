angular.module("userManagement").controller("CreateUserCtrl", 
["$scope", "$state", "$stateParams", "UserService", "UserHttpService", 
"QFormValidation", "$window", "QNgPubSubService", "SERVICES",
function($scope, $state, $stateParams, UserService, UserHttpService, 
QFormValidation, $window, QNgPubSubService, SERVICES) {
    $scope.newUser = true;
    
    $scope.user = {
      active: true,
      superadmin: false,
      groups: []
    };
    
    UserService.setUserPromise(null);
    
    $scope.save = function() {
      var error, success;
      UserHttpService.createUser($scope.user).then(success = function(response) {
    	  QNgPubSubService.publish(SERVICES.WD_SERVICE_TOPICS.NOTIFICATION_SERVICE, {
          title: "usermanagement.user_created_title",
          content: "usermanagement.user_created_content",
          content_data: {
            user: $scope.user.username
          }
        });
    	  
        $state.go("users.user", {
          userId: response.data
        }, {
          reload: true
        });
      }, error = function(response) {
        QFormValidation.renderFormErrors($scope, $scope.userForm, response);
      });
    };
    
    $scope.cancel = function() {
      $state.go("users", {
        reload: true
      });
    };
  }
]);
