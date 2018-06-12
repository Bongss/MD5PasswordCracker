package PasswordCrackerServer;

public class PasswordCrackerConts {
    public final static int NUMBER_OF_WORKER = 3;
    private static final String PASSWORD_CHARS = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final int PASSWORD_LEN = 8;
    public static final long TOTAL_PASSWORD_RANGE_SIZE = (long) Math.pow(PASSWORD_CHARS.length(), PASSWORD_LEN);
    public static final long SUB_RANGE_SIZE = (TOTAL_PASSWORD_RANGE_SIZE + NUMBER_OF_WORKER - 1) / NUMBER_OF_WORKER;
    public static final int WORKER_PORT = 8000;
    public static final long HEART_BEAT_INTERVAL = 1l;
    public static final long HEART_BEAT_INITIAL_DELAY = 1l;
}
