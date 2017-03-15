package com.eurodyn.qlack2.fuse.aaa.api;

import org.osgi.framework.Bundle;

import java.net.URL;

/**
 * A service to parse Fuse AAA configuration files expressed in JSON to allow
 * applications to register their security preferences on runtime. This service
 * allows the following elements of Fuse AAA to be automatically created:
 * <ul>
 * <li>Groups: Create and Edit</li>
 * <li>Templates: Create and Edit</li>
 * <li>Operations: Create and Edit</li>
 * <li>Associations between Groups and Operations: Create only</li>
 * <li>Associations between Templates and Operations: Create only</li>
 * </ul>
 * A sample of the configuration file that is parsed by this service:
 * <pre>
 * {
	"groups": [
		{ "name": "grp1", "description": "grp1 desc", "objectID": "obj1" },
		{ "name": "grp2", "description": "grp2 desc", "objectID": "obj2" },
		{ "name": "grp3", "description": "grp3 desc new", "parentGroupName": "grp1" }
	],
	
	"templates": [
		{ "name": "tem1", "description": "tem1 desc" },
		{ "name": "tem2", "description": "tem2 desc" }
	],
	
	"operations": [
		{ "name": "op1", "description": "op1 desc" },
		{ "name": "op2", "description": "op2 desc" }
	],
	
	"groupHasOperations": [
		{ "groupName": "grp1", "operationName": "op1", "deny": true},
		{ "groupName": "grp2", "operationName": "op2"}
	],
	
	"templateHasOperations": [
		{ "templateName": "tem1", "operationName": "op1", "deny": true},
		{ "templateName": "tem2", "operationName": "op2"}
	]
}
 * 
 * </pre>
 */
public interface JSONConfigService {
    void processBundle(Bundle bundle);

    void parseConfig(String bundleSymbolicName, URL configFileURL);
}
