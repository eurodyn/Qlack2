angular.module('userManagement').service('GroupService', [
  function() {
    var groupPromise;
    groupPromise = null;
    return {
      getGroupPromise: function() {
        return groupPromise;
      },
      setGroupPromise: function(newGroupPromise) {
        return groupPromise = newGroupPromise;
      }
    };
  }
]);
