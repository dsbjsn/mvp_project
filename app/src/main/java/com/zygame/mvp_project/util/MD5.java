package com.zygame.mvp_project.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author admin
 */
public class MD5 {
    /**
     * MD5验证方法
     *
     * @param text    明文
     * @param charset 字符编码
     * @param md5     密文
     * @return true/false
     * @throws Exception
     */
    public static boolean verify(String text, String charset, String md5) throws Exception {
        String md5Text = md5(text, charset);
        if (md5Text.equalsIgnoreCase(md5)) {
            return true;
        }
        return false;
    }


    /**
     * MD5加密
     *
     * @param str
     * @return
     */
    public static String md5(String str) {
        if (str == null) {
            return null;
        }
        return md5(str, "UTF-8");
    }

    public static String md5(String str, String charset) {
        if (str == null) {
            return null;
        }
        MessageDigest messageDigest;

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes(charset));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            return str;
        }

        byte[] byteArray = messageDigest.digest();
        StringBuilder md5StrBuff = new StringBuilder();
        for (byte pB : byteArray) {
            if (Integer.toHexString(0xFF & pB).length() == 1) {
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & pB));
            } else {
                md5StrBuff.append(Integer.toHexString(0xFF & pB));
            }
        }
        return md5StrBuff.toString();
    }


    /**
     * 解码-解密
     *
     * @param base64EncodedString
     * @return
     */
    public static byte[] decode(String base64EncodedString) {
        byte[] bb = null;
        try {
            bb = Base64.decode(base64EncodedString, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bb;
    }


    public static final int ENCRYPTION_A = 1;
    public static final int ENCRYPTION_B = 2;
    public static String get32bitsMD5(String string, int method) {
        switch (method) {
            case ENCRYPTION_A: {
                MessageDigest messageDigest = null;
                try {
                    messageDigest = MessageDigest.getInstance("MD5");
                    messageDigest.reset();
                    messageDigest.update(string.getBytes("UTF-8"));
                } catch (NoSuchAlgorithmException e) {
                    System.exit(-1);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                byte[] byteArray = messageDigest.digest();
                StringBuilder md5StrBuff = new StringBuilder();
                for (byte aByteArray : byteArray) {
                    if (Integer.toHexString(0xFF & aByteArray).length() == 1) {
                        md5StrBuff.append("0").append(Integer.toHexString(0xFF & aByteArray));
                    } else {
                        md5StrBuff.append(Integer.toHexString(0xFF & aByteArray));
                    }
                }
                return md5StrBuff.toString();
            }
            case ENCRYPTION_B: {
                char[] hexDigits = {
                        '0', '1', '2', '3',
                        '4', '5', '6', '7',
                        '8', '9', 'a', 'b',
                        'c', 'd', 'e', 'f'};
                try {
                    byte[] strTemp = string.getBytes();
                    MessageDigest mdTemp = MessageDigest.getInstance("MD5");
                    mdTemp.update(strTemp);
                    byte[] md = mdTemp.digest();
                    int j = md.length;
                    char[] str = new char[j * 2];
                    int k = 0;
                    for (int i = 0; i < j; i++) {
                        byte b = md[i];
                        str[k++] = hexDigits[b >> 4 & 0xf];
                        str[k++] = hexDigits[b & 0xf];
                    }
                    return new String(str);
                } catch (Exception e) {
                    return null;
                }
            }
            default:
                return null;
        }
    }
}
