package com.eurodyn.qlack2.fuse.aaa.impl;

import java.util.Date;

/**
 *
 * @author European Dynamics SA
 */
public class TestConstants {

    public static final String APPLICATION_SESSION_ID = "APPLICATION_SESSION_ID";
    public static final Long DATE_TERMINATED_ON = new Date().getTime() -10000;
    public static final String SESSION_USER_ID = "SESSION_USER_ID";
    public static final String SESSIONDTOID = "SESSION_DTO_ID";


    public static final long DATE_CREATED_ON = new Date().getTime();
    public static final String USER_ID = "02d91f99-5d99-410f-b222-bf99e18db635";


    public static final long USER_DB_VERSION= 0;
    public static final String USER_PASSWORD = "698f77e4ca137aa540040775bba14a79";
    public static final String USER_SALT = "CuEEPmbz9DwSy8pJ";
    public static final long USER_STATUS = 1;
    public static final String USER_NAME = "user1";
    public static final boolean USER_SUPERADMIN = false;
    public static final boolean USER_EXTERNAL = false;
    //Group constants
    public static final String GROUP_ID = "1ae40fff-0cf1-4112-83ed-64e6c2cc8a13";


    public static final long GROUP_DB_VERSION = 1;
    public static final String GROUP_DESCRIPTION = "The default group all users are assigned to when registering with the system.";
    public static final String GROUP_NAME = "ESCP_USER";
    public static final String GROUP_OBJECT_ID = "GROUP_OBJECT_ID";
    //UserAttribute Constants
    public static final String USERATTRIBUTE_ID = "528085cf-825b-4b79-b097-c6942a4620ea";

    public static final byte[] USERATTRIBUTE_BIND_DATA = {};
    public static final String USERATTRIBUTE_CONTENT_TYPE = "SERATTRIBUTE_CONTENT_TYPE";
    public static final String USERATTRIBUTE_DATA = "USERATTRIBUTE_DATA";
    public static final long USERATTRIBUTE_DB_VERSION = 0;
    public static final String USERATTRIBUTE_NAME = "USERATTRIBUTE_NAME";

    //Session Constantns
    public static final String SESSION_ID = "SESSION_ID";
    public static final String SESSION_APPLICATION_ID = "SESSION_APPLICATION_ID";
    public static final String SESSION_ATTRIBUTE_ID = "SESSION_ATTRIBUTE_ID";
    public static final String SESSION_ATTRIBUTE_NAME = "SESSION_ATTRIBUTE_NAME";
    public static final String SESSION_ATTRIBUTE_VALUE = "SESSION_ATTRIBUTE_VALUE";
    public static final long SESSION_CREATED_ON = new Date().getTime() - 10000;
    public static final long SESSION_DB_VERSION = 0;
    public static final long SESSION_TERMINATED_ON = new Date().getTime();

    //SessionAttributes
    public static final String SESSIONATT_ID = "SESSIONATT_ID";
    public static final String SESSIONATT_NAME = "SESSIONATT_NAME" ;
    public static final String SESSIONATT_VALUE = "SESSIONATT_VALUE";
    public static final long SESSIONATT_DB_VERSION = 0;


    //Operations
    public static final String OPERATION_ID =  "11e8550f-2022-44dc-a0ce-1c9f06a34cb0";
    public static final long OPERATION_DB_VERSION = 0;
    public static final String OPERATION_DESCRIPTION = "A member of the community.";
    public static final boolean OPERATION_DYNAMIC = false;
    public static final String OPERATION_DYNAMIC_CODE = null ;
    public static final String OPERATION_NAME = "COMMUNITY_MEMBERSHIP";

    //OpTemplate
    public static final String OPTEMPLATE_ID = "OPTEMPLATE_ID";
    public static final long OPTEMPLATE_DB_VERSION = 0;
    public static final String OPTEMPLATE_DESCRIPTION = "OPTEMPLATE_DESCRIPTION";
    public static final String OPTEMPLATE_NAME = "OPTEMPLATE_NAME";

    //OpTemplateHasOperation
    public static final String OPTEMPLATEHASOPERATION_ID = "OPTEMPLATEHASOPERATION_ID";
    public static final long OPTEMPLATEHASOPERATION_DB_VERSION = 0;
    public static final boolean OPTEMPLATEHASOPERATION_DENY = false;

    //GroupHasOperation
    public static final String GROUPHASOPERATION_ID = "GROUPHASOPERATION_ID";
    public static final long GROUPHASOPERATION_DB_VERSION = 0;
    public static final boolean GROUPHASOPERATION_DENY = false;

    //Resource
    public static final String RESOURCE_ID = "12b85b99-7ce7-4840-bff8-225808d0c071";
    public static final String RESOURCE_DESCRIPTION = "CM:FOLDER with id dfede998-9121-4af5-a99f-5738361626ce";
    public static final String RESOURCE_NAME = "pr1";
    public static final String RESOURCE_OBJECT_ID = "dfede998-9121-4af5-a99f-5738361626ce";


    //UserHasOperation
    public static final String USERHASOPERATION_ID = "USERHASOPERATION_ID";
    public static final boolean USERHASOPERATION_DENY = false;


    //Verfication
    public static final String VERIFICATION_ID = "b07412b9-7ad1-49c6-94d6-f59096d678fa";
    public static final String VERIFICATION_DATA = "{\"userID\":\"b987e9ee-2466-4469-9fe3-3dd05434f648\",\"username\":\"user1\",\"email\":\"user1@y.gr\"}";
    public static final long VERIFICATION_CREATED_ON = new Date().getTime() - 100000;
    public static final long VERIFICATION_EXPIRES_ON = new Date().getTime();


    public static final String JSON_CONFIG_FILE = "src/test/resources/qlack-aaa-config.json";
}
