package PasswordCrackerWorker;

import org.apache.thrift.TException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import static PasswordCrackerWorker.PasswordCrackerConts.PASSWORD_CHARS;
import static PasswordCrackerWorker.PasswordCrackerConts.PASSWORD_LEN;

public class PasswordCrackerUtil {

    private static MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot use MD5 Library:" + e.getMessage());
        }
    }

    private static String encrypt(String password, MessageDigest messageDigest) {
        messageDigest.update(password.getBytes());
        byte[] hashedValue = messageDigest.digest();
        return byteToHexString(hashedValue);
    }

    private static String byteToHexString(byte[] bytes) {
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

    /*
     * The findPasswordInRange method finds the password.
     * if it finds the password, it set the termination for transferring signal to master and returns password to caller.
     */
    public static String findPasswordInRange(long rangeBegin, long rangeEnd, String encryptedPassword, TerminationChecker terminationChecker) throws TException, InterruptedException {
        MessageDigest messageDigest = getMessageDigest();
        int[] candidateChars = new int[PASSWORD_LEN];
        transformDecToBase36(rangeBegin, candidateChars);
        System.out.println("PasswordDecrypter START");
        for (long index = rangeBegin; index < rangeEnd; index++) {
            String password = transformIntoStr(candidateChars);
            /*
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(password);
            */
            if (encrypt(password, messageDigest).equals(encryptedPassword)) {
                terminationChecker.setTerminated();
                return password;
            }

            //isAlreadyTerminated
            if (terminationChecker.isTerminated()) {
                break;
            }

            getNextCandidate(candidateChars);
        }
        return null;
    }

    private static void transformDecToBase36(long numInDec, int[] numArrayInBase36) {
        long numPasswordCharsPowToI = (long) Math.pow(PASSWORD_CHARS.length(), PASSWORD_LEN - 1);
        for (int i = 0; i < PASSWORD_LEN; i++) {
            numArrayInBase36[i] = (int) (numInDec / numPasswordCharsPowToI);
            numInDec = numInDec % numPasswordCharsPowToI;
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

    private static String transformIntoStr(int[] chars) {
        char[] password = new char[chars.length];
        for (int i = 0; i < password.length; i++) {
            password[i] = PASSWORD_CHARS.charAt(chars[i]);
        }
        return new String(password);
    }
}
