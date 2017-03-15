angular.module("rules")
	.service("RuleHttpService", ["$http", "SERVICES", ($http, SERVICES) ->

		getByProjectId: (projectId) ->
			$http.get(SERVICES._PREFIX + SERVICES.PROJECTS + "/" + projectId + SERVICES.RULES,
				params:
					filterEmpty: true
			)

		getById: (ruleId) ->
			$http.get(SERVICES._PREFIX + SERVICES.RULES + "/" + ruleId)

		create: (projectId, rule) ->
			rule.projectId = projectId
			$http.post(SERVICES._PREFIX + SERVICES.RULES, rule)

		update: (ruleId, rule) ->
			$http.put(SERVICES._PREFIX + SERVICES.RULES + "/" + ruleId, rule)

		deleteRule: (ruleId) ->
			$http.delete(SERVICES._PREFIX + SERVICES.RULES + "/" + ruleId)

		canDeleteRule: (ruleId) ->
			$http.get(SERVICES._PREFIX + SERVICES.RULES + "/" + ruleId + SERVICES.CAN_DELETE)

		getVersionsByRuleId: (ruleId) ->
			$http.get(SERVICES._PREFIX + SERVICES.RULES + "/" + ruleId + SERVICES.VERSIONS)

		getVersionById: (versionId) ->
			$http.get(SERVICES._PREFIX + SERVICES.RULE_VERSIONS + "/" + versionId)

		createVersion: (ruleId, version) ->
			$http.post(SERVICES._PREFIX + SERVICES.RULES + "/" + ruleId + SERVICES.VERSIONS, version)

		deleteVersion: (versionId) ->
			$http.delete(SERVICES._PREFIX + SERVICES.RULE_VERSIONS + "/" + versionId)

		canDeleteVersion: (versionId) ->
			$http.get(SERVICES._PREFIX + SERVICES.RULE_VERSIONS + "/" + versionId + SERVICES.CAN_DELETE)

		lockVersion: (versionId) ->
			$http.put(SERVICES._PREFIX + SERVICES.RULE_VERSIONS + "/" + versionId + SERVICES.VERSION_ACTION_LOCK)

		unlockVersion: (versionId) ->
			$http.put(SERVICES._PREFIX + SERVICES.RULE_VERSIONS + "/" + versionId + SERVICES.VERSION_ACTION_UNLOCK)

		canEnableTestingVersion: (versionId) ->
			$http.get(SERVICES._PREFIX + SERVICES.RULE_VERSIONS + "/" + versionId + SERVICES.CAN_ENABLE_TESTING)

		enableTestingVersion: (versionId) ->
			$http.put(SERVICES._PREFIX + SERVICES.RULE_VERSIONS + "/" + versionId + SERVICES.VERSION_ACTION_ENABLE_TESTING)

		canDisableTestingVersion: (versionId) ->
			$http.get(SERVICES._PREFIX + SERVICES.RULE_VERSIONS + "/" + versionId + SERVICES.CAN_DISABLE_TESTING)

		disableTestingVersion: (versionId) ->
			$http.put(SERVICES._PREFIX + SERVICES.RULE_VERSIONS + "/" + versionId + SERVICES.VERSION_ACTION_DISABLE_TESTING)

		canFinalizeVersion: (versionId) ->
			$http.get(SERVICES._PREFIX + SERVICES.RULE_VERSIONS + "/" + versionId + SERVICES.CAN_FINALIZE)

		finalizeVersion: (versionId) ->
			$http.put(SERVICES._PREFIX + SERVICES.RULE_VERSIONS + "/" + versionId + SERVICES.VERSION_ACTION_FINALIZE)

		getExportVersionUrl: (versionId) ->
			SERVICES._PREFIX + SERVICES.RULE_VERSIONS + "/" + versionId + SERVICES.VERSION_ACTION_EXPORT

		importVersion: (ruleId, version) ->
			$http.put(SERVICES._PREFIX + SERVICES.RULES + "/" + ruleId + SERVICES.VERSION_ACTION_IMPORT, version)
	])
