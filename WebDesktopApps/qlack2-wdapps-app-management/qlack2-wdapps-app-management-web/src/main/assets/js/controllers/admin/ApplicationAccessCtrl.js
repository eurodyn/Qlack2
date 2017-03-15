angular.module('appManagement')
.controller('ApplicationAccessCtrl', ['$scope', '$state', '$stateParams', 
'WindowService', 'ConfigHttpService', '$q', '$timeout', 
function($scope, $state, $stateParams, WindowService, ConfigHttpService, $q, 
$timeout) {
    $scope.appId = $stateParams.appId;
    $scope.subjectsDataSource = new kendo.data.DataSource({
      transport: {
        read: function(options) {
          var success;
          return $q.all([ConfigHttpService.getManagedUsers($stateParams.appId), ConfigHttpService.getManagedGroups($stateParams.appId)]).then(success = function(arg) {
            var group, groupSubject, groupsResult, i, j, len, len1, parent, ref, ref1, subjects, user, userSubject, usersResult;
            usersResult = arg[0], groupsResult = arg[1];
            subjects = [];
            ref = usersResult.data;
            for (i = 0, len = ref.length; i < len; i++) {
              user = ref[i];
              userSubject = {
                id: user.id,
                name: user.firstName + " " + user.lastName,
                description: user.username
              };
              if (user.superadmin) {
                userSubject.type = "superadmin";
              } else {
                userSubject.type = "user";
              }
              subjects.push(userSubject);
            }
            ref1 = groupsResult.data;
            for (j = 0, len1 = ref1.length; j < len1; j++) {
              group = ref1[j];
              groupSubject = {
                id: group.id,
                name: group.name,
                description: null
              };
              if ((group.parentGroup != null)) {
                groupSubject.type = "group";
                parent = group.parentGroup;
                while ((parent != null)) {
                  groupSubject.name = parent.name + " / " + groupSubject.name;
                  parent = parent.parentGroup;
                }
              } else {
                groupSubject.type = "domain";
              }
              subjects.push(groupSubject);
            }
            options.success(subjects);
          });
        }
      },
      sort: {
        field: "name",
        dir: "asc"
      }
    });
    $scope.subjectTemplate = kendo.template($("#subjectTemplate").html());
    $scope.subjectAltTemplate = kendo.template($("#subjectAltTemplate").html());
    $scope.filterSubjects = function() {
      return $scope.subjectsDataSource.filter({
        logic: "or",
        filters: [
          {
            field: "name",
            operator: "contains",
            value: $scope.subjectFilter
          }, {
            field: "description",
            operator: "contains",
            value: $scope.subjectFilter
          }
        ]
      });
    };
    $scope.subjectListLoaded = function(e) {
      return $timeout(function() {
        var listItem, selectedUser;
        if ($stateParams.ownerId != null) {
          selectedUser = e.sender.dataSource.get($stateParams.ownerId);
          listItem = e.sender.items().filter("[data-uid='" + selectedUser.uid + "']");
          e.sender.select(listItem);
        }
      });
    };
    $scope.selectedSubject = null;
    $scope.subjectChanged = function(e) {
      return $scope.selectedSubject = $scope.subjectsDataSource.getByUid(angular.element(e.sender.select()[0]).data("uid"));
    };
    $scope.showPopover = false;
    $scope.togglePopover = function() {
      return $scope.showPopover = !$scope.showPopover;
    };
    return $scope.removeSubject = function() {
      return WindowService.openWindow("remove_from_access_management", "views/admin/removeAccessSubject.html", {
        subjectId: $scope.selectedSubject.id
      });
    };
  }
]);
