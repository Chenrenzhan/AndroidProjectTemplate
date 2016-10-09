package com.drumge.template.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PasswordUtil {

    // <= WEAK
    private static final String REG_ONLY_DIGIT = "\\d{6,}";
    private static final String REG_ONLY_LETTER = "[a-zA-Z]{6,}";
    private static final String REG_SPECIAL_CHARACTER = "-`=\\\\\\[\\];',./~!@#$%^&*\\(\\)_+|\\{\\}:\"<>?]{6,}";
    // Good
    private static final String REG_DIGIT_MIX_LETTER = "[\\da-zA-Z]*\\d+[a-zA-Z]+[\\da-zA-Z]*";
    private static final String REG_DIGIT_MIX_SPECIAL_CHARACTER = "[-\\d`=\\\\\\[\\];',./~!@#$%^&*()_+|\\{}:\"<>?]*\\d+[-`=\\\\\\[\\];',./~!@#$%^&*()_+|\\{}:\"<>?]+[-\\d`=\\\\\\[\\];',./~!@#$%^&*()_+|\\{}:\"<>?]*";
    private static final String REG_LETTER_MIX_SPECIAL_CHARACTER = "[-a-zA-Z`=\\\\\\[\\];',./~!@#$%^&*()_+|\\{}:\"<>?]*[a-zA-Z]+[-`=\\\\\\[\\];',./~!@#$%^&*()_+|\\{}:\"<>?]+[-a-zA-Z`=\\\\\\[\\];',./~!@#$%^&*()_+|\\{}:\"<>?]*";
    // >= Strong
    private static final String REG_STRONG = "[-\\da-zA-Z`=\\\\\\[\\];',./~!@#$%^&*()_+|{}:\"<>?]*((\\d+[a-zA-Z]+[-`=\\\\\\[\\];',./~!@#$%^&*()_+|{}:\"<>?]+)|(\\d+[-`=\\\\\\[\\];',./~!@#$%^&*()_+|\\{}:\"<>?]+[a-zA-Z]+)|([a-zA-Z]+\\d+[-`=\\\\\\[\\];',./~!@#$%^&*()_+|\\{}:\"<>?]+)|([a-zA-Z]+[-`=\\\\\\[\\];',./~!@#$%^&*()_+|\\{}:\"<>?]+\\d+)|([-`=\\\\\\[\\];',./~!@#$%^&*()_+|\\{}:\"<>?]+\\d+[a-zA-Z]+)|([-`=\\\\\\[\\];',./~!@#$%^&*()_+|\\{}:\"<>?]+[a-zA-Z]+\\d+))[-\\da-zA-Z`=\\\\\\[\\];',./~!@#$%^&*()_+|\\{}:\"<>?]*";

    public static enum PasswordStrength{
        TooShort,TooObvious,Weak,Good,Strong,VeryStrong
    }

    /*
    *  warning: the below rules are not by P.M., it's still under testing
    * */
    public static PasswordStrength checkPasswordStrength(CharSequence pw){
        int lng = pw.length();
        if(lng == 6){
            if (isOnlyDigit(pw)||isOnlyLetter(pw)||isOnlySpecialCharacter(pw)){
                return PasswordStrength.TooObvious;
            }else if(isDigitMixLetter(pw)||isDigitMixSpecialCharacter(pw)||isLetterMixSpecialCharacter(pw)){
                return PasswordStrength.Weak;
            }
        }else if(lng >6 && lng <= 8){
            if (isOnlyDigit(pw)||isOnlyLetter(pw)||isOnlySpecialCharacter(pw)){
                return PasswordStrength.TooObvious;
            }else if(isDigitMixLetter(pw)||isDigitMixSpecialCharacter(pw)||isLetterMixSpecialCharacter(pw)){
                return PasswordStrength.Good;
            }else if(isStrong(pw)){
                return PasswordStrength.Strong;
            }
        }else if(lng > 8){
            if (isOnlyDigit(pw)||isOnlyLetter(pw)||isOnlySpecialCharacter(pw)){
                return PasswordStrength.Weak;
            }else if(isDigitMixLetter(pw)||isDigitMixSpecialCharacter(pw)||isLetterMixSpecialCharacter(pw)){
                return PasswordStrength.Strong;
            }else if(isStrong(pw)){
                return PasswordStrength.VeryStrong;
            }
        }
        return PasswordStrength.TooShort;
    }

    public static boolean isOnlyDigit(CharSequence pw){
        Pattern weak = Pattern.compile(REG_ONLY_DIGIT);
        Matcher matcher = weak.matcher(pw);
        return matcher.matches();
    }

    public static boolean isOnlyLetter(CharSequence pw){
        Pattern weak = Pattern.compile(REG_ONLY_LETTER);
        Matcher matcher = weak.matcher(pw);
        return matcher.matches();
    }

    public static boolean isOnlySpecialCharacter(CharSequence pw){
        Pattern weak = Pattern.compile(REG_SPECIAL_CHARACTER);
        Matcher matcher = weak.matcher(pw);
        return matcher.matches();
    }

    public static boolean isDigitMixSpecialCharacter(CharSequence pw){
        Pattern good = Pattern.compile(REG_DIGIT_MIX_SPECIAL_CHARACTER);
        Matcher matcher = good.matcher(pw);
        return matcher.matches();
    }

    public static boolean isDigitMixLetter(CharSequence pw){
        Pattern good = Pattern.compile(REG_DIGIT_MIX_LETTER);
        Matcher matcher = good.matcher(pw);
        return matcher.matches();
    }

    public static boolean isLetterMixSpecialCharacter(CharSequence pw){
        Pattern good = Pattern.compile(REG_LETTER_MIX_SPECIAL_CHARACTER);
        Matcher matcher = good.matcher(pw);
        return matcher.matches();
    }

    public static boolean isStrong(CharSequence pw){
        Pattern strong = Pattern.compile(REG_STRONG);
        Matcher matcher = strong.matcher(pw);
        return matcher.matches();
    }

}
