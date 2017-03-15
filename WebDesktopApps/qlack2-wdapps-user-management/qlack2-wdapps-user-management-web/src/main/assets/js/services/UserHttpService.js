angular.module('userManagement').service('UserHttpService', [
  '$http', 'SERVICES', function($http, SERVICES) {
    return {
      getAllUsers: function(filter) {
        return $http({
          method: "GET",
          url: SERVICES._PREFIX + SERVICES.USERS
        });
      },
      getUser: function(id) {
        return $http({
          method: "GET",
          url: SERVICES._PREFIX + SERVICES.USERS + "/" + id
        });
      },
      createUser: function(user) {
        return $http({
          method: "POST",
          url: SERVICES._PREFIX + SERVICES.USERS,
          data: user
        });
      },
      updateUser: function(user) {
        return $http({
          method: "PUT",
          url: SERVICES._PREFIX + SERVICES.USERS + "/" + user.id,
          data: user
        });
      },
      deleteUser: function(userId) {
        return $http({
          method: "DELETE",
          url: SERVICES._PREFIX + SERVICES.USERS + "/" + userId
        });
      },
      resetUserPassword: function(userId, password) {
        return $http({
          method: "PUT",
          url: SERVICES._PREFIX + SERVICES.USERS + "/" + userId + SERVICES.USER_ACTION_RESET,
          data: password
        });
      }
    };
  }
]);
