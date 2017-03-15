package com.eurodyn.qlack2.fuse.aaa.api.dto;

/**
 * A Java class mirroring the JSON file with configration options.
 */
public class JSONConfig {
	private Group[] groups;
	private Template[] templates;
	private Operation[] operations;
	private GroupHasOperation[] groupHasOperations;
	private TemplateHasOperation[] templateHasOperations;

	public Group[] getGroups() {
		return groups != null ? groups : new Group[0];
	}

	public void setGroups(Group[] groups) {
		this.groups = groups;
	}

	public Template[] getTemplates() {
		return templates != null ? templates : new Template[0];
	}

	public void setTemplates(Template[] templates) {
		this.templates = templates;
	}

	public Operation[] getOperations() {
		return operations != null ? operations : new Operation[0];
	}

	public void setOperations(Operation[] operations) {
		this.operations = operations;
	}

	public GroupHasOperation[] getGroupHasOperations() {
		return groupHasOperations != null ? groupHasOperations : new GroupHasOperation[0];
	}

	public void setGroupHasOperations(GroupHasOperation[] groupHasOperations) {
		this.groupHasOperations = groupHasOperations;
	}

	public TemplateHasOperation[] getTemplateHasOperations() {
		return templateHasOperations != null ? templateHasOperations : new TemplateHasOperation[0];
	}

	public void setTemplateHasOperations(TemplateHasOperation[] templateHasOperations) {
		this.templateHasOperations = templateHasOperations;
	}

	public static class Group {
		private String name;
		private String description;
		private String objectID;
		private String parentGroupName;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getObjectID() {
			return objectID;
		}

		public void setObjectID(String objectID) {
			this.objectID = objectID;
		}

		public String getParentGroupName() {
			return parentGroupName;
		}

		public void setParentGroupName(String parentGroupName) {
			this.parentGroupName = parentGroupName;
		}
	}

	public static class Template {
		private String name;
		private String description;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
	}

	public static class Operation {
		private String name;
		private String description;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
	}

	public static class GroupHasOperation {
		private String groupName;
		private String operationName;
		private boolean deny;
		
		public boolean isDeny() {
			return deny;
		}

		public void setDeny(boolean deny) {
			this.deny = deny;
		}

		public String getGroupName() {
			return groupName;
		}

		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}

		public String getOperationName() {
			return operationName;
		}

		public void setOperationName(String operationName) {
			this.operationName = operationName;
		}
	}

	public static class TemplateHasOperation {
		private String templateName;
		private String operationName;
		private boolean deny;
		
		public boolean isDeny() {
			return deny;
		}

		public void setDeny(boolean deny) {
			this.deny = deny;
		}
		
		public String getTemplateName() {
			return templateName;
		}

		public void setTemplateName(String templateName) {
			this.templateName = templateName;
		}

		public String getOperationName() {
			return operationName;
		}

		public void setOperationName(String operationName) {
			this.operationName = operationName;
		}
	}
}
