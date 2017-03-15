angular.module('userManagement').controller('GroupListCtrl', [
  '$scope', '$stateParams', 'GroupHttpService', function($scope, $stateParams, GroupHttpService) {
    $scope.groupTreeSource = new kendo.data.HierarchicalDataSource({
      transport: {
        read: function(options) {
          var success;
          return GroupHttpService.getAllGroups().then(success = function(result) {
            var children, i, innerNode, j, k, len, len1, node, nodes, ref, ref1;
            nodes = [];
            ref = result.data;
            for (j = 0, len = ref.length; j < len; j++) {
              node = ref[j];
              children = [];
              if ((node.childGroups != null)) {
                children.push(node.childGroups);
                i = 0;
                while (i < children.length) {
                  ref1 = children[i];
                  for (k = 0, len1 = ref1.length; k < len1; k++) {
                    innerNode = ref1[k];
                    innerNode.icon = "group";
                    if ((innerNode.childGroups != null)) {
                      children.push(innerNode.childGroups);
                    }
                  }
                  i++;
                }
              }
              node.icon = "sitemap";
              nodes.push(node);
            }
            nodes;
            options.success(nodes);
          });
        }
      },
      schema: {
        model: {
          id: "id",
          children: "childGroups"
        }
      }
    });
    $scope.groupTreeTemplate = kendo.template($("#groupTreeTemplate").html());
    $scope.groupTreeLoaded = function(e) {
      var selectedGroup, selectedGroupUid;
      e.sender.expand(".k-item");
      if ($stateParams.groupId != null) {
        selectedGroup = e.sender.dataSource.get($stateParams.groupId);
        if ((selectedGroup != null)) {
          selectedGroupUid = e.sender.findByUid(selectedGroup.uid);
          e.sender.select(selectedGroupUid);
        }
      }
    };
    $scope.groupMoved = function(e) {
      var groupId, newParentId;
      newParentId = e.sender.dataItem(e.destinationNode).id;
      groupId = e.sender.dataItem(e.sourceNode).id;
      return GroupHttpService.moveGroup(groupId, newParentId);
    };
  }
]);
