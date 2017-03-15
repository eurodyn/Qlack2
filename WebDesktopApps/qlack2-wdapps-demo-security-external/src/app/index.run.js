(function() {
  "use strict";

  angular
    .module("qlack2WdappsDemoSecurityExternal")
    .run(runBlock);

  /** @ngInject */
  function runBlock($log, QNgPubSubService) {
    QNgPubSubService.init("qlack2WdappsDemoSecurityExternal", false);
	  $log.debug("runBlock end");
  }

})();
