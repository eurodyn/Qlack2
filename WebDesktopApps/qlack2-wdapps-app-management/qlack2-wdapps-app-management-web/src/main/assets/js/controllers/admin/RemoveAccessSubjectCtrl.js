angular.module('appManagement').controller('RemoveAccessSubjectCtrl', [
  '$scope', '$state', '$stateParams', 'ConfigHttpService', 'WindowService', function($scope, $state, $stateParams, ConfigHttpService, WindowService) {
    $scope.cancel = function() {
      return WindowService.closeWindow();
    };
    $scope.remove = function() {
      var success;
      return ConfigHttpService.unmanageSubject($stateParams.appId, WindowService.getWindow().data.subjectId).then(success = function(response) {
        WindowService.closeWindow();
        return $state.go("access.application", $stateParams.appId, {
          reload: true
        });
      });
    };
  }
]);
