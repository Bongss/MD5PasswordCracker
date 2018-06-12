package PasswordCrackerServer;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TSocket;
import thrift.gen.PasswordCrackerMasterService.PasswordCrackerMasterService;
import thrift.gen.PasswordCrackerWorkerService.PasswordCrackerWorkerService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import static PasswordCrackerServer.PasswordCrackerConts.SUB_RANGE_SIZE;
import static PasswordCrackerServer.PasswordCrackerConts.WORKER_PORT;
import static PasswordCrackerServer.PasswordCrackerMasterServiceHandler.jobInfoMap;
import static PasswordCrackerServer.PasswordCrackerMasterServiceHandler.workersAddressList;

public class PasswordCrackerMasterServiceHandler implements PasswordCrackerMasterService.Iface {
    public static List<TSocket> workersSocketList = new LinkedList<>();  //Connected Socket
    public static List<String> workersAddressList = new LinkedList<>(); // Connected WorkerAddress
    public static ConcurrentHashMap<String, PasswordDecrypterJob> jobInfoMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Long> latestHeartbeatInMillis = new ConcurrentHashMap<>();
    public static ExecutorService workerPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    public static ScheduledExecutorService heartBeatCheckPool = Executors.newScheduledThreadPool(1);
    
    /*
     * The decrypt method create the job and put the job with jobId (encrypted Password) in map.
     * And call the requestFindPassword and if it finds the password, it return the password to the client.
     */
    @Override
    public String decrypt(String encryptedPassword) throws TException {
        PasswordDecrypterJob decryptJob = new PasswordDecrypterJob();
        jobInfoMap.put(encryptedPassword, decryptJob);
        
        requestFindPassword(encryptedPassword, 0, SUB_RANGE_SIZE);
        return decryptJob.getPassword();
    }
    
    /*
     * The reportHeartBeat receives the heartBeat from workers.
     * Consider the checkHeartBeat method and use latestHeartbeatInMillis map.
    */
    @Override
    public void reportHeartBeat(String workerAddress) throws TException {
        latestHeartbeatInMillis.put(workerAddress, System.currentTimeMillis());
    }
    
    
    /*
     * The requestFindPassword requests workers to find password using RPC in asynchronous way.
    */
    public static void requestFindPassword(String encryptedPassword, long rangeBegin, long subRangeSize) {
        PasswordCrackerWorkerService.AsyncClient worker = null;
        FindPasswordMethodCallback findPasswordCallBack = new FindPasswordMethodCallback(encryptedPassword);
        try {
            int workerId = 0;
            for (String workerAddress : workersAddressList) {
                
                long subRangeBegin = rangeBegin + (workerId * subRangeSize);
                long subRangeEnd = subRangeBegin + subRangeSize;
                
                worker = new PasswordCrackerWorkerService.AsyncClient(
                        new TBinaryProtocol.Factory(),
                        new TAsyncClientManager(),
                        new TNonblockingSocket(workerAddress, WORKER_PORT));
                worker.startFindPasswordInRange(subRangeBegin, subRangeEnd, encryptedPassword, findPasswordCallBack);
                workerId++;
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
    }
    
    /*
     * The redistributeFailedTask distributes the dead workers's job (or a set of possible password) to active workers.
     */
    public static void redistributeFailedTask(ArrayList<Integer> failedWorkerIdList) {
        int numberOfWorker = workersAddressList.size() - failedWorkerIdList.size();
        TSocket workerSocket = null;
        for (int failWorkerId : failedWorkerIdList) {
            workerSocket = workersSocketList.get(failWorkerId);
            workersAddressList.remove(failWorkerId);
            workersSocketList.remove(failWorkerId);
            workerSocket.close();
            
            // ### ### --- redistribute
            for (String jobId : jobInfoMap.keySet()) {
                long failedRangeBegin = failWorkerId * SUB_RANGE_SIZE;
                long failedSubRangeSize = (SUB_RANGE_SIZE + numberOfWorker - 1) / numberOfWorker;
                requestFindPassword(jobId, failedRangeBegin, failedSubRangeSize);
            }
            // ---
        }
    }
    
    /*
     *  If the master didn't receive the "HeartBeat" in 5 seconds from any workers,
     *  it considers the workers that didn't send the "HeartBeat" as dead.
     *  And then, it redistributes the dead workers's job in other alive workers
     *
     *  hint : use latestHeartbeatinMillis
     *
     *  you must think about when several workers is dead.
     */
    public static void checkHeartBeat() {
        int workerId = 0;
        final long thresholdAge = 5_000;
        
        ArrayList<Integer> failedWorkerIdList = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        System.out.println("HEART");
        for (String workerAddress : workersAddressList) {
            long currentHeartBeatValue = latestHeartbeatInMillis.get(workerAddress);
            long age = currentTime - currentHeartBeatValue;
            if (age > thresholdAge) {
                failedWorkerIdList.add(workerId);
                System.out.println("workerId " + workerId + " : HEART BEAT ERROR");
            }
            workerId++;
        }
        if (failedWorkerIdList.size() > 0) {
            workerPool.submit(() -> redistributeFailedTask(failedWorkerIdList));
        }
    }
}

//CallBack
class FindPasswordMethodCallback implements AsyncMethodCallback<PasswordCrackerWorkerService.AsyncClient.startFindPasswordInRange_call> {
    private String jobId;
    
    FindPasswordMethodCallback(String jobId) {
        this.jobId = jobId;
    }
    
    /*
     *  if the returned result from worker is not null, it completes the job.
     *  and call the jobTermination method
     */
    @Override
    public void onComplete(PasswordCrackerWorkerService.AsyncClient.startFindPasswordInRange_call startFindPasswordInRange_call) {
        try {
            String findPasswordResult = startFindPasswordInRange_call.getResult();
            if (findPasswordResult != null) {
                PasswordDecrypterJob decrypterJob = jobInfoMap.get(jobId);
                decrypterJob.setPassword(findPasswordResult);
                jobTermination(jobId);
            }
        } catch (TException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onError(Exception e) {
        System.out.println("Error : startFindPasswordInRange of FindPasswordMethodCallback");
    }
    
    /*
     *  The jobTermination transfer the termination signal to workers in asynchronous way
     */
    private void jobTermination(String jobId) {
        try {
            PasswordCrackerWorkerService.AsyncClient worker = null;
            for (String workerAddress : workersAddressList) {
                worker = new PasswordCrackerWorkerService.
                        AsyncClient(new TBinaryProtocol.Factory(), new TAsyncClientManager(),
                        new TNonblockingSocket(workerAddress, WORKER_PORT));
                worker.reportTermination(jobId, null);
            }
        } catch (TException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
