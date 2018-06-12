package PasswordCracker;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PasswordCrackerMain {
    public static void main(String args[]) {
        if (args.length < 4) {
            System.out.println("Usage: PasswordCrackerMain numThreads passwordLength isEarlyTermination encryptedPassword");
            return;
        }
        
        int numThreads = Integer.parseInt(args[0]);
        int passwordLength = Integer.parseInt(args[1]);
        boolean isEarlyTermination = Boolean.parseBoolean(args[2]);
        String encryptedPassword = args[3];
        
        ExecutorService workerPool = Executors.newFixedThreadPool(numThreads);
        PasswordFuture passwordFuture = new PasswordFuture();
        PasswordCrackerConsts consts = new PasswordCrackerConsts(numThreads, passwordLength, encryptedPassword);

        for (int i = 0; i < numThreads; i++) {
            PasswordCrackerTask task = new PasswordCrackerTask(i, isEarlyTermination, consts, passwordFuture);
			workerPool.submit(task, passwordFuture);
        }

        try {
            System.out.println("Password: " + passwordFuture.get());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerPool.shutdown();
        }
    }
}

class PasswordFuture implements Future<String> {
    String result;
    Lock lock = new ReentrantLock();
    Condition resultSet = lock.newCondition(); // refer to Condition and Lock class in javadoc

    public void set(String result) {
        /** COMPLETE **/
        lock.lock();
        try {
            this.result = result;
            resultSet.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String get() throws InterruptedException, ExecutionException {
        lock.lock();
        try {
            while (result == null) {
                resultSet.await();
            }
            return result;
        } finally {
            lock.unlock();
        }
    }
    @Override
    public boolean isDone() {
        /** COMPLETE **/
        return result != null;
    }


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }
    @Override
    public boolean isCancelled() {
        return false;
    }
    @Override
    public String get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        // no need to implement this. We don't use this...
        return null;
    }
}


