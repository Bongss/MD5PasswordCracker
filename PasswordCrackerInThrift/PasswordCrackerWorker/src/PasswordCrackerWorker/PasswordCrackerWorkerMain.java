package PasswordCrackerWorker;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.*;
import thrift.gen.PasswordCrackerMasterService.PasswordCrackerMasterService;
import thrift.gen.PasswordCrackerWorkerService.PasswordCrackerWorkerService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.*;

import static PasswordCrackerWorker.PasswordCrackerConts.INITIAL_DELAY;
import static PasswordCrackerWorker.PasswordCrackerConts.INTERVAL;
import static PasswordCrackerWorker.PasswordCrackerConts.MASTER_PORT;


public class PasswordCrackerWorkerMain {
    public static ScheduledExecutorService transferPool = Executors.newScheduledThreadPool(2);
    public static String workerHostAddress;

    static {
        try {
            workerHostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                System.err.println("USAGE : PasswordCrackerWorker MasterAddress");
            }
            String masterAddress = args[0];

            TTransport workerTransport = new TSocket(masterAddress, MASTER_PORT);
            workerTransport.open();    // Connect to master

            // After connecting to Master...
            TProtocol protocol = new TBinaryProtocol(workerTransport);
            PasswordCrackerMasterService.Client masterService = new PasswordCrackerMasterService.Client(protocol);

            transferPool.scheduleAtFixedRate(() -> {
                transferHeartBeat(masterService);
            }, INITIAL_DELAY, INTERVAL, TimeUnit.SECONDS);

            executeWorkerServer();

        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }

    // Service Direction : worker -> master
    public static void executeWorkerServer() {
        try {
            PasswordCrackerWorkerServiceHandler workerServiceHandler = new PasswordCrackerWorkerServiceHandler();
            PasswordCrackerWorkerService.Processor masterRequestProcessor = new PasswordCrackerWorkerService.Processor(workerServiceHandler);

            TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(9000);

            TThreadedSelectorServer.Args workerServerArgs = new TThreadedSelectorServer.Args(serverTransport);
            workerServerArgs.transportFactory(new TFramedTransport.Factory());
            workerServerArgs.protocolFactory(new TBinaryProtocol.Factory());
            workerServerArgs.processor(masterRequestProcessor);
            workerServerArgs.selectorThreads(4);
            workerServerArgs.workerThreads(32);

            TServer server = new TThreadedSelectorServer(workerServerArgs);

            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }

    // Transfer heartbeat to master
    public static void transferHeartBeat(PasswordCrackerMasterService.Client masterService) {
        try {
            masterService.reportHeartBeat(workerHostAddress);
        } catch (TException e) {
            System.err.println("transferHeartBeat() : Master RPC Call Error");
        }

    }
}
