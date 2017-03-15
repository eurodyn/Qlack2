package com.eurodyn.qlack2.fuse.crypto.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.Charset;

/**
 * Created by kumar.prashant on 08/06/2016.
 */
public class TestUtils {


    public static String hmacSHA256(String secret, String message, Charset charSet){

//        HmacUtils.
//        return "encrypted";

        return String.valueOf(DigestUtils.sha256(message.getBytes()));
    }

    public static String md5(String message) {

        return DigestUtils.md5Hex(message);
    }
}
