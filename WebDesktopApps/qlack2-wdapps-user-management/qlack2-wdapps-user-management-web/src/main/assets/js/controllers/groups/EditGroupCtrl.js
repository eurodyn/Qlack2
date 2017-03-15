angular.module("userManagement").controller("EditGroupCtrl", 
["$scope", "$rootScope", "$state", "$stateParams", "GroupService", 
"GroupHttpService", "UserHttpService", "WindowService", "SecuritySrv", 
"QFormValidation", "$window", "QNgPubSubService", "SERVICES",
function($scope, $rootScope, $state, $stateParams, GroupService, 
GroupHttpService, UserHttpService, WindowService, SecuritySrv, QFormValidation, 
$window, QNgPubSubService, SERVICES) {
    var deleteGroup, groupPromise, securityPromise, success;
    
    securityPromise = SecuritySrv.resolvePermission("WD_MANAGE_GROUPS");
    
    if ($stateParams.groupId !== "0") {
      groupPromise = GroupHttpService.getGroup($stateParams.groupId).then(success = function(response) {
        $scope.group = response.data;
        return response.data;
      });
    } else {
      groupPromise = null;
    }
    
    GroupService.setGroupPromise(groupPromise);
    $scope.actionsListTemplate = kendo.template($("#actionsListTemplate").html());
    
    securityPromise.then(success = function(result) {
      if ($rootScope.permissions["WD_MANAGE_GROUPS"]) {
        if ($stateParams.domain === "true") {
          $scope.actionsDataSource = new kendo.data.DataSource({
            data: [
              {
                key: "delete_domain",
                icon: "fa-times",
                onSelect: function() {
                  return deleteGroup();
                }
              }
            ]
          });
        } else {
          $scope.actionsDataSource = new kendo.data.DataSource({
            data: [
              {
                key: "delete_group",
                icon: "fa-times",
                onSelect: function() {
                  return deleteGroup();
                }
              }
            ]
          });
        }
      }
    });
    
    $scope.userDataSource = new kendo.data.DataSource({
      transport: {
        read: function(options) {
          return UserHttpService.getAllUsers().then(success = function(result) {
            return options.success(result.data);
          });
        }
      }
    });
    
    $scope.userColumns = [
      {
        field: "username",
        headerTemplate: "<span translate>username</span>"
      }, {
        field: "firstName",
        headerTemplate: "<span translate>first_name</span>"
      }, {
        field: "lastName",
        headerTemplate: "<span translate>last_name</span>"
      }, {
        field: "id",
        headerTemplate: "<span translate>member</span>",
        template: "<input type='checkbox' name='groupUsers' value='#:id#' ng-checked='group.users.indexOf(\"#=id#\") > -1' ng-click='selectGroupUser(\"#:id#\")' ng-disabled='!permissions[\"WD_MANAGE_GROUPS\"]'/>",
        filterable: false,
        sortable: false
      }
    ];
    
    $scope.selectGroupUser = function(userId) {
      var index;
      index = $scope.group.users.indexOf(userId);
      if (index > -1) {
        return $scope.group.users.splice(index, 1);
      } else {
        return $scope.group.users.push(userId);
      }
    };
    
    $scope.executeAction = function(e) {
      var item;
      e.preventDefault();
      item = e.sender.dataItem(e.item.index());
      if ((item.onSelect != null)) {
        return item.onSelect();
      }
    };
    
    deleteGroup = function() {
      if ($stateParams.domain === "true") {
        return WindowService.openWindow("delete_domain", "views/groups/deleteGroup.html");
      } else {
        return WindowService.openWindow("delete_group", "views/groups/deleteGroup.html");
      }
    };
    
    $scope.cancel = function() {
      return $state.go($state.current, $stateParams, {
        reload: true
      });
    };
    
    $scope.save = function() {
      var error;
      return GroupHttpService.updateGroup($scope.group).then(success = function(response) {
        notificationTitle;
        notificationContent;
        var notificationContent, notificationTitle;
        if ($stateParams.domain === "true") {
          notificationTitle = "usermanagement.domain_updated_title";
          notificationContent = "usermanagement.domain_updated_content";
        } else {
          notificationTitle = "usermanagement.group_updated_title";
          notificationContent = "usermanagement.group_updated_content";
        }
        QNgPubSubService.publish(SERVICES.WD_SERVICE_TOPICS.NOTIFICATION_SERVICE, {
          title: notificationTitle,
          content: notificationContent,
          content_data: {
            group: $scope.group.name
          }
        });
        
        $state.go("groups.group", {
          groupId: $stateParams.groupId
        }, {
          reload: true
        });
      }, error = function(response) {
        QFormValidation.renderFormErrors($scope, $scope.groupForm, response);
      });
    };
  }
]);
