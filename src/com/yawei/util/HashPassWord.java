package com.yawei.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashPassWord {
    public static String getHash(String input){
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes("UTF-8"));
            byte[] hashedSrc = md.digest();
            for(byte b : hashedSrc){
                String temp  = Integer.toHexString(b&0xff);
                result += temp;
            }
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }finally {
            return result;
        }
    }
}
