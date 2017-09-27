package com.eurodyn.qlack2.fuse.lexicon.util;

import java.util.*;

public class TestConst {
    //Constants for methods - generateRandomString/getRandomNumber
    private static final String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final int RANDOM_STRING_LENGTH = 10;
    private static final String standarPath = "qlack2-fuse-imaging-test\\src\\test\\java\\com\\eurodyn\\qlack2\\fuse\\lexicon\\tmp\\excelTest.xls";
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

    public static String createPath(){
        String tempRem = "";
        String tempPath = System.getProperty("user.dir");
        tempRem = tempPath.replace("\\", "\\\\");
        String[] parts = tempRem.split("qlack2-fuse-lexicon-test");
        String path = parts[0].toString();

        String replStdrPath = standarPath.replace("\\", "\\\\");

        return path.concat(replStdrPath);
    }
}

