angular.module('userManagement').service('ConfigHttpService', [
  '$http', 'SERVICES', function($http, SERVICES) {
    return {
      getOperations: function(subjectId) {
        return $http({
          method: "GET",
          url: SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_SUBJECTS + "/" + subjectId + "/operations"
        });
      },
      saveOperations: function(subjectId, operations) {
        return $http({
          method: "POST",
          url: SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_SUBJECTS + "/" + subjectId + "/operations",
          data: operations
        });
      },
      getManagedUsers: function() {
        return $http({
          method: "GET",
          url: SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_USERS
        });
      },
      getManagedGroups: function() {
        return $http({
          method: "GET",
          url: SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_GROUPS
        });
      },
      manageSubject: function(subjectId) {
        return $http({
          method: "PUT",
          url: SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_SUBJECTS + "/" + subjectId + "/manage"
        });
      },
      unmanageSubject: function(subjectId) {
        return $http({
          method: "PUT",
          url: SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_SUBJECTS + "/" + subjectId + "/unmanage"
        });
      }
    };
  }
]);
