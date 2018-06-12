package PasswordCrackerWorker;

import org.apache.thrift.TException;
import thrift.gen.PasswordCrackerWorkerService.PasswordCrackerWorkerService;
import java.util.concurrent.*;

import static PasswordCrackerWorker.PasswordCrackerUtil.findPasswordInRange;

class TerminationChecker {
    boolean isTerminated;

    TerminationChecker() {
        isTerminated = false;
    }

    public boolean isTerminated() {
        return isTerminated;
    }

    public void setTerminated() {
        isTerminated = true;
    }
}


public class PasswordCrackerWorkerServiceHandler implements PasswordCrackerWorkerService.Iface {
    static ConcurrentHashMap<String, TerminationChecker> terminationCheckerMap = new ConcurrentHashMap<>(); //  <jobId, TerminationChecker>
    int numberOfProcessor = Runtime.getRuntime().availableProcessors();
    ExecutorService workerPool = Executors.newFixedThreadPool(numberOfProcessor);

    /*
     * The startFindPasswordInRange is called by the master.
     * Return the result if findPasswordInRange task terminates.
     *
     */
    @Override
    public String startFindPasswordInRange(long rangeBegin, long rangeEnd, String encryptedPassword) throws TException {
        //xxx : 중간에 하나의 머신이 중지되서 다른 머신에서 하나의 쓰레드를 추가로 돌릴때 termination Checker 클래스 생성 문제
        // Termination Checker는 encryptedPassword 당 하나 생성이 되어야 한다.
        
        String passwordOrNull = null;

        try {
            if (!terminationCheckerMap.containsKey(encryptedPassword)) {
                terminationCheckerMap.put(encryptedPassword, new TerminationChecker());
            }

            TerminationChecker terminationChecker = terminationCheckerMap.get(encryptedPassword);
            Future<String> workerFuture = workerPool.submit(() -> findPasswordInRange(rangeBegin, rangeEnd, encryptedPassword, terminationChecker));

            passwordOrNull = workerFuture.get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return passwordOrNull;
    }

    /*
     * The reportTermination is called by the master.
     * it set the termination signal.
     */
    @Override
    public void reportTermination(String jobId) throws TException {
        /** COMPLETE **/
        //xxx
        TerminationChecker terminationChecker = terminationCheckerMap.get(jobId);
        terminationChecker.setTerminated();
    }

}

