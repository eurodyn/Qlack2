angular
	.module("formsManagerApp")
	.service "FormVersionHttpService", ["$http", "SERVICES", ($http, SERVICES) ->
		getFormVersion: (formVersionId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.FORM_VERSION + "/" + formVersionId

		deleteFormVersion: (formVersionId) ->
			$http
				method: "DELETE"
				url: SERVICES._PREFIX + SERVICES.FORM_VERSION + "/" + formVersionId

		lockFormVersion: (formVersionId) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.FORM_VERSION + "/" + formVersionId + SERVICES.FORM_VERSION_LOCK

		unlockFormVersion: (formVersionId) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.FORM_VERSION + "/" + formVersionId + SERVICES.FORM_VERSION_UNLOCK

		canFinaliseFormVersion: (formVersionId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.FORM_VERSION + "/" + formVersionId + SERVICES.FORM_VERSION_CAN_FINALISE

		finaliseFormVersion: (formVersionId) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.FORM_VERSION + "/" + formVersionId + SERVICES.FORM_VERSION_FINALISE

		enableTestingForFormVersion: (formVersionId) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.FORM_VERSION + "/" + formVersionId + SERVICES.FORM_VERSION_ENABLE_TESTING

		disableTestingForFormVersion: (formVersionId) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.FORM_VERSION + "/" + formVersionId + SERVICES.FORM_VERSION_DISABLE_TESTING

		getConditionTypes: () ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.FORM_VERSION + SERVICES.CONDITION_TYPES
	]