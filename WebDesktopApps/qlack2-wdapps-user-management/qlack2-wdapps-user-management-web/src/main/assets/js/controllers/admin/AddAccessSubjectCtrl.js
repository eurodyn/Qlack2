angular.module('userManagement').controller('AddAccessSubjectCtrl', [
  '$scope', '$state', '$stateParams', 'WindowService', 'UserHttpService', 'GroupHttpService', '$timeout', 'ConfigHttpService', function($scope, $state, $stateParams, WindowService, UserHttpService, GroupHttpService, $timeout, ConfigHttpService) {
    var clearGroupSelection, clearUserSelection;
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
    $scope.userTemplate = kendo.template($("#addUserTemplate").html());
    $scope.userAltTemplate = kendo.template($("#addUserAltTemplate").html());
    $scope.addUserFilter = {};
    $scope.filterAddUsers = function() {
      return $scope.userListSource.filter({
        logic: "or",
        filters: [
          {
            field: "username",
            operator: "contains",
            value: $scope.addUserFilter.text
          }, {
            field: "firstName",
            operator: "contains",
            value: $scope.addUserFilter.text
          }, {
            field: "lastName",
            operator: "contains",
            value: $scope.addUserFilter.text
          }
        ]
      });
    };
    $scope.groupTreeSource = new kendo.data.HierarchicalDataSource({
      transport: {
        read: function(options) {
          var success;
          return GroupHttpService.getAllGroups().then(success = function(result) {
            var children, i, innerNode, j, k, len, len1, node, nodes, ref, ref1;
            nodes = [];
            ref = result.data;
            for (j = 0, len = ref.length; j < len; j++) {
              node = ref[j];
              children = [];
              if ((node.childGroups != null)) {
                children.push(node.childGroups);
                i = 0;
                while (i < children.length) {
                  ref1 = children[i];
                  for (k = 0, len1 = ref1.length; k < len1; k++) {
                    innerNode = ref1[k];
                    innerNode.icon = "group";
                    if ((innerNode.childGroups != null)) {
                      children.push(innerNode.childGroups);
                    }
                  }
                  i++;
                }
              }
              node.icon = "sitemap";
              nodes.push(node);
            }
            nodes;
            options.success(nodes);
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
    $scope.groupTreeTemplate = kendo.template($("#addGroupTreeTemplate").html());
    $scope.groupTreeLoaded = function(e) {
      e.sender.expand(".k-item");
    };
    $scope.lists = {};
    $scope.changeUser = function() {
      if ($scope.lists.userList.select()[0] != null) {
        return clearGroupSelection();
      }
    };
    $scope.selectGroup = function() {
      return $timeout(function() {
        return clearUserSelection();
      });
    };
    clearUserSelection = function() {
      return $scope.lists.userList.clearSelection();
    };
    clearGroupSelection = function() {
      return $scope.lists.groupList.select(angular.element());
    };
    $scope.cancel = function() {
      return $scope.$parent.togglePopover();
    };
    return $scope.add = function() {
      subjectId;
      var subjectId, success;
      if ($scope.lists.groupList.select()[0] != null) {
        subjectId = $scope.lists.groupList.dataItem($scope.lists.groupList.select()).id;
      }
      if ($scope.lists.userList.select()[0] != null) {
        subjectId = $scope.userListSource.getByUid(angular.element($scope.lists.userList.select()[0]).data("uid")).id;
      }
      return ConfigHttpService.manageSubject(subjectId).then(success = function(result) {
        return $state.go($state.current, $stateParams, {
          reload: true
        });
      });
    };
  }
]);
