package PasswordCrackerClient;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.*;
import thrift.gen.PasswordCrackerMasterService.PasswordCrackerMasterService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PasswordCrackerClient {
    public final static int MASTER_PORT = 8000;

    public static void main(String[] args) throws TException {
        if (args.length != 2) {
            System.err.println("USAGE : PasswordCrackerClient MasterAddress encryptedPassword");
            return;
        }
        String masterAddress = args[0];
        String encryptedPassword = args[1];

        try {
            TTransport transport;
            transport = new TSocket(masterAddress, MASTER_PORT);
            transport.open();   // Connect Socket (ipAddress, port) to master

            TProtocol protocol = new TBinaryProtocol(transport);
            
            PasswordCrackerMasterService.Client passwordCrackerService = new PasswordCrackerMasterService.Client(protocol);
            String password = passwordCrackerService.decrypt(encryptedPassword);    // RPC decrypt Call
            System.out.println("encryptedPassword : " + encryptedPassword + "\npassword : " + password);

            transport.close();
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
