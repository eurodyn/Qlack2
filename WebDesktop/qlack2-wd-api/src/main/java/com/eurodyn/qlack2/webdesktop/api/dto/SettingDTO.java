package com.eurodyn.qlack2.webdesktop.api.dto;

public class SettingDTO {
	private String key;
	private String val;
	private String group;
	private boolean sensitive;

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the val
	 */
	public String getVal() {
		return val;
	}

	/**
	 * @param val
	 *            the val to set
	 */
	public void setVal(String val) {
		this.val = val;
	}

	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @param group
	 *            the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * @return the sensitive
	 */
	public boolean isSensitive() {
		return sensitive;
	}

	/**
	 * @param sensitive
	 *            the sensitive to set
	 */
	public void setSensitive(boolean sensitive) {
		this.sensitive = sensitive;
	}
	
	public boolean getValAsBoolean() {
		if (val.equals("1") || val.equalsIgnoreCase("true")) {
			return true;
		} else {
			return false;
		}
	}
}
