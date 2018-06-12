package PasswordCrackerServer;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.*;
import org.json.simple.parser.JSONParser;
import thrift.gen.PasswordCrackerMasterService.PasswordCrackerMasterService;

import java.io.FileReader;
import java.util.HashMap;

public class PasswordCrackerServerMain {
    public static void main(String[] args) {
        PasswordCrackerMasterServiceHandler masterServiceHandler = new PasswordCrackerMasterServiceHandler();
        PasswordCrackerMasterService.Processor clientRequestProcessor = new PasswordCrackerMasterService.Processor(masterServiceHandler);
        try {
            JSONParser parser = new JSONParser();
            // JSON FILE -> HASH MAP<String, Integer>
            HashMap<String, Integer> workerInfoMap = (HashMap<String, Integer>) parser.parse(new FileReader("/Users/bong/Documents/Project/PasswordCrackerInThrift/PasswordCrackerServer/WorkerInfoList.json"));

            PasswordCrackerTransport transport = new PasswordCrackerTransport(9000, workerInfoMap);

            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(transport).processor(clientRequestProcessor));
            System.out.println("Starting the Server...");
            server.serve();

        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}