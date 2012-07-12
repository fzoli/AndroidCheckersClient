package org.dyndns.fzoli.mill.common;

import java.util.Date;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author zoli
 */
public class InputValidator {
    
    public final static int MIN_USER_LENGTH = 3;
    public final static int MAX_USER_LENGTH = 10;
    public final static int MIN_PASSWORD_LENGTH = 6;
    public final static int MAX_PASSWORD_LENGTH = 15;
    public final static int MD5_LENGTH = 32;
    
    private final static Pattern PATTERN_NAME = Pattern.compile("^[\\p{L}]{1,}[.]{0,1}[ ]{0,1}[\\p{L}-]{1,18}[\\p{L}]{1,1}$");
    private final static Pattern PATTERN_EMAIL = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", Pattern.CASE_INSENSITIVE);
    private final static Pattern PATTERN_CAPTCHA = Pattern.compile("^[qwertzuopasdfghjkyxcvbnm]{6,6}$", Pattern.CASE_INSENSITIVE);
    
    public static boolean isCaptchaValid(String value) {
        return isValid(PATTERN_CAPTCHA, value);
    }
    
    public static boolean isUserIdValid(String value) {
        if (value == null) return false;
        return value.matches(createRule(MIN_USER_LENGTH, MAX_USER_LENGTH));
    }
        
    public static boolean isPasswordValid(String value) {
        if (value == null) return false;
        return value.matches(createRule(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH));
    }
    
    public static boolean isPasswordHashValid(String value) {
        if (value == null) return false;
        return value.matches(createRule(MD5_LENGTH, MD5_LENGTH));
    }
    
    public static boolean isPasswordValid(String value, boolean hash) {
        return hash ? isPasswordHashValid(value) : isPasswordValid(value);
    }
    
    public static boolean isEmailValid(String value) {
        if (value == null) return false;
        if (value.isEmpty()) return true;
        return isValid(PATTERN_EMAIL, value);
    }
    
    public static boolean isNameValid(String value) {
        if(value != null && !value.isEmpty()) {
            if(!value.contains(".") && !Character.isUpperCase(value.charAt(0))) return false;
            if (value.contains(".") && !value.contains(" ")) return false;
        }
        return isValid(PATTERN_NAME, value);
    }
    
    public static boolean isBirthDateValid(Date date) {
        if (date == null) return false;
        Date now = new Date();
        return !(date.after(now) || Math.abs(date.getTime() - now.getTime()) > 150 * 365.24 * 24 * 60 * 60 * 1000);
    }
    
    public static String md5Hex(String s) {
        return new String(Hex.encodeHex(DigestUtils.md5(s)));
    }
    
    private static boolean isValid(Pattern pattern, String value) {
        if (value == null) return false;
        return pattern.matcher(value).matches();
    }
    
    private static String createRule(int min, int max) {
        return "^[a-zA-Z0-9]{" + min + "," + max + "}$";
    }
    
}