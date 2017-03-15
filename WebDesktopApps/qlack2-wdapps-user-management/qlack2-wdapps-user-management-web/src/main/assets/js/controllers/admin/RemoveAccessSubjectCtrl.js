angular.module('userManagement').controller('RemoveAccessSubjectCtrl', [
  '$scope', '$state', '$stateParams', 'ConfigHttpService', 'WindowService', function($scope, $state, $stateParams, ConfigHttpService, WindowService) {
    $scope.cancel = function() {
      return WindowService.closeWindow();
    };
    $scope.remove = function() {
      var success;
      return ConfigHttpService.unmanageSubject(WindowService.getWindow().data.subjectId).then(success = function(response) {
        WindowService.closeWindow();
        return $state.go("access", {}, {
          reload: true
        });
      });
    };
  }
]);
