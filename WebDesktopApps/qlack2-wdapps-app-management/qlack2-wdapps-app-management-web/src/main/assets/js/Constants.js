angular.module("appManagement").constant("SERVICES", {
	_PREFIX : "/api/apps/appmanagement",
	APPLICATIONS : "/applications",
	CONFIG : "/config",
	CONFIG_OPERATIONS : "/operations",
	CONFIG_USERS : "/users",
	CONFIG_GROUPS : "/groups",
	CONFIG_MANAGED_USERS : "/users/managed",
	CONFIG_MANAGED_GROUPS : "/groups/managed",
	CONFIG_SUBJECTS : "/subjects",
	
	WD_SERVICE_TOPICS: {
		DESKTOP_SERVICE_CLOSE: "/wd/application/close",
		NOTIFICATION_SERVICE: "/wd/notification"
	}
});
