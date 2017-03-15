angular
	.module("formsManagerApp")
	.service "FormHttpService", ["$http", "SERVICES", ($http, SERVICES) ->
		getForm: (formId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.FORM + "/" + formId

		createForm: (formObj) ->
			$http
				method: "POST"
				url: SERVICES._PREFIX + SERVICES.FORM
				data: formObj

		updateForm: (formId, formObj) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.FORM + "/" + formId
				data: formObj

		deleteForm: (formId) ->
			$http
				method: "DELETE"
				url: SERVICES._PREFIX + SERVICES.FORM + "/" + formId

		getFormVersions: (formId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.FORM + "/" + formId + SERVICES.FORM_VERSIONS

		createFormVersion: (formId, formVersionObj) ->
			$http
				method: "POST"
				url: SERVICES._PREFIX + SERVICES.FORM + "/" + formId + SERVICES.FORM_VERSIONS
				data: formVersionObj

		countFormVersionsLockedByOtherUser: (formId) ->
			$http
				method: "GET"
				url: SERVICES._PREFIX + SERVICES.FORM + "/" + formId + SERVICES.FORM_VERSIONS_LOCKED_COUNT

		importFormVersion: (formId, formVersionObj) ->
			$http
				method: "PUT"
				url: SERVICES._PREFIX + SERVICES.FORM + "/" + formId + SERVICES.FORM_VERSION_IMPORT
				data: formVersionObj

	]