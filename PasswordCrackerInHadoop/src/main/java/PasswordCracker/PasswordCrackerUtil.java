package PasswordCracker;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordCrackerUtil {
    private static final String PASSWORD_CHARS = "0123456789abcdefghijklmnopqrstuvwxyz";    // Possible Password symbol (NUMBER(0~9) + CHARACTER(A to Z))
    private static final int PASSWORD_LEN = 8;
    public static final long TOTAL_PASSWORD_RANGE_SIZE = (long) Math.pow(PASSWORD_CHARS.length(), PASSWORD_LEN);

    public static MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot use MD5 Library:" + e.getMessage());
        }
    }

    public static String encrypt(String password, MessageDigest messageDigest) {
        messageDigest.update(password.getBytes());
        byte[] hashedValue = messageDigest.digest();
        return byteToHexString(hashedValue);
    }

    public static String byteToHexString(byte[] bytes) {
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

    // Tries i'th candidate (rangeBegin < i < rangeEnd) and compares against encryptedPassword
    // If original password is found, return the password;
    // if not, return null.

    public static String findPasswordInRange(long rangeBegin, long rangeEnd, String encryptedPassword, TerminationChecker checker)
            throws IOException {
        MessageDigest messageDigest = getMessageDigest();
        int[] candidateChars = new int[PASSWORD_LEN];

        initPasswordChars(rangeBegin, candidateChars);    // According to thread, set the digit value to execute in different order
        for (long l = rangeBegin; l < rangeEnd; l++) {
            String password = transformIntoStr(candidateChars);
            if (encrypt(password, messageDigest).equals(encryptedPassword)) {
                return password;
            }

            if (checker.isTerminated()) {
                break;
            }

            getNextCandidate(candidateChars);
        }
        return null;
    }

    private static void initPasswordChars(long targetNum, int[] passwordChars) {
        long numPasswordCharsPowToI = (long) Math.pow(PASSWORD_CHARS.length(), passwordChars.length - 1);
        for (int i = passwordChars.length - 1; i >= 0; i--) {
            passwordChars[i] = (int) (targetNum / numPasswordCharsPowToI);
            targetNum = targetNum % numPasswordCharsPowToI;
            numPasswordCharsPowToI = numPasswordCharsPowToI / PASSWORD_CHARS.length();
        }
    }

    private static void getNextCandidate(int[] candidateChars) {
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

    private static String transformIntoStr(int[] candidateChars) {
        char[] password = new char[candidateChars.length];
        for (int i = 0; i < password.length; i++) {
            password[i] = PASSWORD_CHARS.charAt(candidateChars[i]);
        }
        return new String(password);
    }
}
