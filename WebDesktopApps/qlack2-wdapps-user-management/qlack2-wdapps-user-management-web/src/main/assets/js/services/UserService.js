angular.module('userManagement').service('UserService', [
  function() {
    var userPromise;
    userPromise = null;
    return {
      getUserPromise: function() {
        return userPromise;
      },
      setUserPromise: function(newPromise) {
        return userPromise = newPromise;
      }
    };
  }
]);
