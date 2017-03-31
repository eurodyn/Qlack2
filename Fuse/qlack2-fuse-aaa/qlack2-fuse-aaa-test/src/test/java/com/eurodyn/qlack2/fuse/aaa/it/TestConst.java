package com.eurodyn.qlack2.fuse.aaa.it;

import java.util.Date;
import java.util.*;


public class TestConst {


    //Constants
    public static final Byte statusUpd = 101;
    public static final Long DATE_TERMINATED_ON = new Date().getTime() - 10000;
    public static final long DATE_CREATED_ON = new Date().getTime();
    public static final String USER_PASSWORD = "698f77e4ca137aa540040775bba14a79";
    public static final boolean USER_SUPERADMIN = false;
    public static final boolean USER_EXTERNAL = false;
    public static final String GROUP_DESCRIPTION = "Description for test";
    public static final String USERATTRIBUTE_DATA = "USERATTRIBUTE_DATA";
    public static final String SESSION_ATTRIBUTE_VALUE = "SESSION_ATTRIBUTE_VALUE";
    public static final String OPERATION_DESCRIPTION = "Description for test";
    public static final boolean OPERATION_DYNAMIC = false;
    public static final String OPERATION_DYNAMIC_CODE = null;

    //Constants for OpTemplateService
    public static final String OPTEMPLATE_DESCRIPTION = "Description for test";

    //Constants for ResourceService
    public static final String RESOURCE_DESCRIPTION = "Description for test";

    //Constants for VerficationService
    public static final String VERIFICATION_DATA = "{\"userID\":\"b987e9ee-2466-4469-9fe3-3dd05434f648\",\"username\":\"user1\",\"email\":\"user1@test.com\"}";
    public static final long VERIFICATION_CREATED_ON = new Date().getTime() - 100000;
    public static final long VERIFICATION_EXPIRES_ON = new Date().getTime();

    //Constants for JSONConfigService
    public static final String JSON_CONFIG_FILE = "src/test/resources/qlack-aaa-config.json";


    //Constants for methods - generateRandomString/getRandomNumber
    private static final String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final int RANDOM_STRING_LENGTH = 10;


    public static String generateRandomString(){

        StringBuffer randStr = new StringBuffer();
        for(int i=0; i<RANDOM_STRING_LENGTH; i++){
            int number = getRandomNumber();
            char ch = CHAR_LIST.charAt(number);
            randStr.append(ch);
        }
        String randStr_new = randStr.toString();
        randStr_new = randStr_new.replaceAll("[0-9]","");
        return randStr_new.toString();

    }


    public static int getRandomNumber(){

        int randomInt = 0;
        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(CHAR_LIST.length());
        if (randomInt - 1 == -1) {
            return randomInt;
        } else {
            return randomInt - 1;
        }

    }

}

