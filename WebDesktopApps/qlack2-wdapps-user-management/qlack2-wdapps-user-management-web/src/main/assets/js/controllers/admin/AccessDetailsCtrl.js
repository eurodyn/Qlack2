angular.module("userManagement").controller("AccessDetailsCtrl", 
["$scope", "$state", "$stateParams", "ConfigHttpService", "$window", "SERVICES",
"QNgPubSubService",
function($scope, $state, $stateParams, ConfigHttpService, $window, SERVICES, 
QNgPubSubService) {
    var success;
    
    ConfigHttpService.getOperations($stateParams.ownerId).then(success = function(response) {
      $scope.accessData = response.data;
    });
    
    $scope.accessTypes = new kendo.data.DataSource({
      data: [
        {
          name: 'default',
          value: null
        }, {
          name: 'allowed',
          value: 'true'
        }, {
          name: 'denied',
          value: 'false'
        }
      ]
    });
    
    $scope.accessTemplate = kendo.template($("#accessTemplate").html());
    
    $scope.save = function() {
      var i, len, operation, ref;
      ref = $scope.accessData;
      for (i = 0, len = ref.length; i < len; i++) {
        operation = ref[i];
        if (operation.access === "null") {
          operation.access = null;
        }
      }
      ConfigHttpService.saveOperations($stateParams.ownerId, $scope.accessData).then(success = function(response) {
    	QNgPubSubService.publish(SERVICES.WD_SERVICE_TOPICS.NOTIFICATION_SERVICE, {
    		title: "usermanagement.operations_saved_title",
            content: "usermanagement.operations_saved_content",
            content_data: {
              subject: $scope.selectedSubject.name
            }
    	});
      });
    };
    
    $scope.cancel = function() {
      $state.go($state.current, $stateParams, {
        reload: true
      });
    };
  }
]);
