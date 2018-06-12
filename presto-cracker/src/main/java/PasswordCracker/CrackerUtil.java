package PasswordCracker;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CrackerUtil {
    private static final String PASSWORD_CHARS = "0123456789abcdefghijklmnopqrstuvwxyz";    // Possible Password symbol (NUMBER(0~9) + CHARACTER(A to Z))
    public static final int PASSWORD_LEN = 8;
    public static final long TOTAL_PASSWORD_RANGE_SIZE = (long) Math.pow(PASSWORD_CHARS.length(), PASSWORD_LEN);

    public static MessageDigest getMessageDigest()
    {
        try {
            return MessageDigest.getInstance("MD5");
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot use MD5 Library:" + e.getMessage());
        }
    }

    public static String encryptPassword(String password, MessageDigest messageDigest)
    {
        messageDigest.update(password.getBytes());
        byte[] hashedValue = messageDigest.digest();
        return byteToHexString(hashedValue);
    }

    public static String byteToHexString(byte[] bytes)
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                builder.append('0');
            }
            builder.append(hex);
        }
        return builder.toString();
    }


    public static void initCandidateChars(long targetNum, int[] passwordChars)
    {
        long numPasswordCharsPowToI = (long) Math.pow(PASSWORD_CHARS.length(), PASSWORD_LEN - 1);
        for (int i = 0; i < PASSWORD_LEN; i++) {
            passwordChars[i] = (int) (targetNum / numPasswordCharsPowToI);
            targetNum = targetNum % numPasswordCharsPowToI;
            numPasswordCharsPowToI = numPasswordCharsPowToI / PASSWORD_CHARS.length();
        }
    }

    public static void getNextCandidate(int[] candidateChars)
    {
        candidateChars[candidateChars.length - 1]++;
        for (int i = candidateChars.length - 1; i > 0; i--) {
            if (candidateChars[i] == PASSWORD_CHARS.length()) {
                candidateChars[i] = 0;
                candidateChars[i - 1]++;
            } else {
                break;
            }
        }
    }

    public static String transformIntoStr(int[] chars)
    {
        if (chars[chars.length - 1] == -1) {
            return "";
        }
        char[] password = new char[chars.length];
        for (int i = 0; i < password.length; i++) {
            password[i] = PASSWORD_CHARS.charAt(chars[i]);
        }
        return new String(password);
    }
}
