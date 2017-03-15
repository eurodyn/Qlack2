angular.module('userManagement').service('GroupHttpService', [
  '$http', 'SERVICES', function($http, SERVICES) {
    return {
      getAllGroups: function() {
        return $http({
          method: "GET",
          url: SERVICES._PREFIX + SERVICES.GROUPS
        });
      },
      getGroup: function(id) {
        return $http({
          method: "GET",
          url: SERVICES._PREFIX + SERVICES.GROUPS + "/" + id
        });
      },
      createGroup: function(group, parentId) {
        return $http({
          method: "POST",
          url: SERVICES._PREFIX + SERVICES.GROUPS + "/" + parentId,
          data: group
        });
      },
      updateGroup: function(group) {
        return $http({
          method: "PUT",
          url: SERVICES._PREFIX + SERVICES.GROUPS + "/" + group.id,
          data: group
        });
      },
      deleteGroup: function(groupId) {
        return $http({
          method: "DELETE",
          url: SERVICES._PREFIX + SERVICES.GROUPS + "/" + groupId
        });
      },
      moveGroup: function(groupId, newParentId) {
        return $http({
          method: "PUT",
          url: SERVICES._PREFIX + SERVICES.GROUPS + "/" + groupId + SERVICES.GROUP_ACTION_MOVE,
          params: {
            newParentId: newParentId
          }
        });
      }
    };
  }
]);
