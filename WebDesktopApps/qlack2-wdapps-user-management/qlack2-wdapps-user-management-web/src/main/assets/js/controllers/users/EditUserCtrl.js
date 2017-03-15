angular.module("userManagement").controller("EditUserCtrl", 
["$scope", "$rootScope", "$state", "$stateParams", "UserService", 
"UserHttpService", "GroupHttpService", "WindowService", "SecuritySrv", 
"QFormValidation", "$window", "SERVICES", "QNgPubSubService",
function($scope, $rootScope, $state, $stateParams, UserService, UserHttpService, 
GroupHttpService, WindowService, SecuritySrv, QFormValidation, $window, SERVICES, 
QNgPubSubService) {
    var deleteUser, resetUserPassword, securityPromise, success, userPromise;
    securityPromise = SecuritySrv.resolvePermission("WD_MANAGE_USERS");
    
    $scope.newUser = false;
    userPromise = UserHttpService.getUser($stateParams.userId).then(success = function(response) {
      $scope.user = response.data;
      return response.data;
    });
    
    UserService.setUserPromise(userPromise);
    
    $scope.actionsListTemplate = kendo.template($("#actionsListTemplate").html());
    
    securityPromise.then(success = function(result) {
      return $scope.actionsDataSource = new kendo.data.DataSource({
        data: [
          {
            key: "delete_user",
            icon: "fa-times",
            onSelect: function() {
              return deleteUser();
            }
          }, {
            key: "reset_password",
            icon: "fa-refresh",
            onSelect: function() {
              return resetUserPassword();
            }
          }
        ]
      });
    });
    
    $scope.executeAction = function(e) {
      var item;
      e.preventDefault();
      item = e.sender.dataItem(e.item.index());
      if ((item.onSelect != null)) {
        return item.onSelect();
      }
    };
    
    deleteUser = function() {
      return WindowService.openWindow("delete_user", "views/users/deleteUser.html");
    };
    
    resetUserPassword = function() {
      return WindowService.openWindow("reset_password", "views/users/resetPassword.html");
    };
    
    $scope.save = function() {
      var error;
      return UserHttpService.updateUser($scope.user).then(success = function(response) {
    	  QNgPubSubService.publish(SERVICES.WD_SERVICE_TOPICS.NOTIFICATION_SERVICE, {
	          title: "usermanagement.user_updated_title",
	          content: "usermanagement.user_updated_content",
	          content_data: {
	            user: $scope.user.username
	          }
	        });
        $state.go($state.current, $stateParams, {
          reload: true
        });
      }, error = function(response) {
        QFormValidation.renderFormErrors($scope, $scope.userForm, response);
      });
    };
    
    $scope.cancel = function() {
      $state.go($state.current, $stateParams, {
        reload: true
      });
    };
  }
]);
