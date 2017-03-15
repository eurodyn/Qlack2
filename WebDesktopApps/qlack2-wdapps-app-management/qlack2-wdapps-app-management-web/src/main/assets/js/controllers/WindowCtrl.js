angular.module('appManagement').controller('WindowCtrl', [
  '$scope', '$compile', '$rootScope', 'WindowService', '$timeout', function($scope, $compile, $rootScope, WindowService, $timeout) {
    $scope.window = null;
    $scope.$watch((function() {
      return WindowService.getWindow();
    }), (function(newVal, oldVal) {
      $scope.window = newVal;
    }));
    $scope.windowOpen = function(e) {
      return $timeout(function() {
        return e.sender.center().open();
      });
    };
    return $scope.windowClose = function(e) {
      WindowService.closeWindow();
    };
  }
]);
