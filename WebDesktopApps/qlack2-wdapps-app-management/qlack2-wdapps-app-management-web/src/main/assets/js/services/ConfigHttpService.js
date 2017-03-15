angular.module('appManagement').service('ConfigHttpService', [
  '$http', 'SERVICES', function($http, SERVICES) {
    return {
      getOperations: function(appId, subjectId) {
        var url;
        url = SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_SUBJECTS + "/" + subjectId + "/operations";
        if (!!appId) {
          url = url + "?appId=" + appId;
        }
        return $http({
          method: "GET",
          url: url
        });
      },
      saveOperations: function(appId, subjectId, operations) {
        var url;
        url = SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_SUBJECTS + "/" + subjectId + "/operations";
        if (!!appId) {
          url = url + "?appId=" + appId;
        }
        return $http({
          method: "POST",
          url: url,
          data: operations
        });
      },
      getManagedUsers: function(appId) {
        var url;
        url = SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_MANAGED_USERS;
        if (!!appId) {
          url = url + "?appId=" + appId;
        }
        return $http({
          method: "GET",
          url: url
        });
      },
      getManagedGroups: function(appId) {
        var url;
        url = SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_MANAGED_GROUPS;
        if (!!appId) {
          url = url + "?appId=" + appId;
        }
        return $http({
          method: "GET",
          url: url
        });
      },
      manageSubject: function(appId, subjectId) {
        var url;
        url = SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_SUBJECTS + "/" + subjectId + "/manage";
        if (!!appId) {
          url = url + "?appId=" + appId;
        }
        return $http({
          method: "PUT",
          url: url
        });
      },
      unmanageSubject: function(appId, subjectId) {
        var url;
        url = SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_SUBJECTS + "/" + subjectId + "/unmanage";
        if (!!appId) {
          url = url + "?appId=" + appId;
        }
        return $http({
          method: "PUT",
          url: url
        });
      },
      getAllUsers: function() {
        return $http({
          method: "GET",
          url: SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_USERS
        });
      },
      getAllGroups: function() {
        return $http({
          method: "GET",
          url: SERVICES._PREFIX + SERVICES.CONFIG + SERVICES.CONFIG_GROUPS
        });
      }
    };
  }
]);
