package com.eurodyn.qlack2.fuse.workflow.runtime.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.cfg.DefaultNamingStrategy;

public class CustomNamingStrategy extends DefaultNamingStrategy {

    private static final long serialVersionUID = 1L;
    private static final String PREFIX = "jbpm_";

    private static final Logger logger = Logger.getLogger(CustomNamingStrategy.class.getName());
    
    @Override
    public String classToTableName(final String className) {
    	logger.log(Level.FINE, "className: " + className);
    	return this.addPrefix(className);
    }
    
    @Override
    public String tableName(final String tableName) {
    	logger.log(Level.FINE, "tableName: " + tableName);
    	return this.addPrefix(tableName);
    }
    
    @Override
	public String foreignKeyColumnName(String propertyName, String propertyEntityName, 
									   String propertyTableName, String referencedColumnName) {	   	
    	String header = propertyName != null ? super.propertyToColumnName(propertyName) : propertyTableName;
    	if ( header == null ) 
    		logger.log(Level.SEVERE, "CustomNamingStrategy not properly filled" );
    	String myColumnName = columnName( header + "_" + referencedColumnName );
    	
    	logger.log(Level.FINE, "NEW foreignKeyColumnName: " + propertyName + " " + propertyEntityName + 
    			" " + propertyTableName + " " + referencedColumnName + " " + header + ", " + myColumnName);
    	return myColumnName;
	}

    @Override
    public String collectionTableName(final String ownerEntity,
            final String ownerEntityTable, final String associatedEntity,
            final String associatedEntityTable, final String propertyName) {
        return this.addPrefix(super.collectionTableName(ownerEntity,
                ownerEntityTable, associatedEntity, associatedEntityTable,
                propertyName));
    }

    @Override
    public String logicalCollectionTableName(final String tableName,
            final String ownerEntityTable, final String associatedEntityTable,
            final String propertyName) {
        return this.addPrefix(super.logicalCollectionTableName(tableName,
                ownerEntityTable, associatedEntityTable, propertyName));
    }

    private String addPrefix(final String composedTableName) {     
    	String result = PREFIX + composedTableName;
    	
    	if (result.length() > 30)
    		result = result.substring(0, 30);
    	
    	logger.log(Level.FINE, "NEW tableName: " + result);
    	
    	return result;
    }
}
