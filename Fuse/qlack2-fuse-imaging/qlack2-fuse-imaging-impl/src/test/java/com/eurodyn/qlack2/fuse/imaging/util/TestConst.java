package com.eurodyn.qlack2.fuse.imaging.util;

import java.util.Random;

public class TestConst {
    //Constants for methods - generateRandomString/getRandomNumber
    private static final String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final String standarPath = "qlack2-fuse-imaging-test\\src\\test\\java\\com\\eurodyn\\qlack2\\fuse\\imaging\\tmp\\image.jpg";
    private static final int RANDOM_STRING_LENGTH = 10;
    public static final float scaleFactorX = 0.25f;
    public static final float scaleFactorY = 0.25f;
    public static final float sizeX = 1.05f;
    public static final float sizeY = 0.75f;
    public static final float scaleFactor = 1.25f;
    public static final int scaleFactorInt = 10;
    public static final int scaleFactorIntX = 10;
    public static final int scaleFactorIntY = 10;
    public static final double contrastFactor = 20.0;
    public static final int[] bits = {0,1,10};
    public static final double[] borderColor = {10};
    public static final byte[] overlay = {10};
    public static final String format = "jpeg";

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

    public static String createLocalImagePath(){
        String tempRem = "";
        String tempPath = System.getProperty("user.dir");
        tempRem = tempPath.replace("\\", "\\\\");
        String[] parts = tempRem.split("qlack2-fuse-imaging-test");
        String path = parts[0].toString();

        String replStdrPath = standarPath.replace("\\", "\\\\");

        return path.concat(replStdrPath);
    }

}

