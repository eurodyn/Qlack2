package com.eurodyn.qlack2.fuse.rules.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;

public class PrefixNamingStrategy extends DefaultNamingStrategy {
	private static final long serialVersionUID = 936223117881541885L;

	private static final Logger logger = Logger.getLogger(PrefixNamingStrategy.class.getName());

	private static final String JBPM_PREFIX = "jbpm_";

	private static final String RULES_PREFIX = "rul_runtime";

	public static final NamingStrategy INSTANCE = new PrefixNamingStrategy();

	public PrefixNamingStrategy() {
	}

	@Override
	public String classToTableName(String className) {
		logger.log(Level.FINE, "Processing class name {0}", className);
		String tableName = super.classToTableName(className);
		if (className.startsWith(RULES_PREFIX)) {
			return tableName;
		}
		else {
			return prefix(tableName);
		}
	}

	@Override
	public String tableName(String tableName) {
		logger.log(Level.FINE, "Processing table name {0}", tableName);
		String delegateTableName = super.classToTableName(tableName);
		if (tableName.startsWith(RULES_PREFIX)) {
			return delegateTableName;
		}
		else {
			return prefix(delegateTableName);
		}
	}

	private String prefix(String name) {
		return JBPM_PREFIX + name;
	}

}
