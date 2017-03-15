angular.module("userManagement").service("WindowService", [
  '$rootScope', function($rootScope) {
    var window;
    window = null;
    return {
      getWindow: function() {
        return window;
      },
      openWindow: function(titleKey, content, data) {
        window = {
          titleKey: titleKey,
          content: content,
          data: data
        };
      },
      closeWindow: function() {
        window = null;
        return $rootScope.$emit('WINDOW_CLOSED');
      }
    };
  }
]);
