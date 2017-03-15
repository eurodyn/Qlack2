package com.eurodyn.qlack2.be.rules.impl.util;

/**
 * The configuration parameters which can be edited through the
 * Rules application
 * @author European Dynamics SA
 *
 */
public enum ConfigParam {
	MAX_FILE_SIZE("maxFileSize");
	
	private String name;
	ConfigParam(String name) {
		this.name = name;
	}
	
	public static boolean contains(String value) {
	    for (ConfigParam param : ConfigParam.values()) {
	        if (param.toString().equals(value)) {
	            return true;
	        }
	    }
	    return false;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
