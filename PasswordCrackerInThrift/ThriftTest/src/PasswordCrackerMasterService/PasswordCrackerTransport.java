//package PasswordCrackerService;
//
//import org.apache.thrift.TException;
//import org.apache.thrift.transport.*;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//
//import java.io.IOException;
//import java.net.Socket;
//
//import static PasswordCrackerServer.PasswordCrackerConts.NUMBER_OF_WORKER;
//import static PasswordCrackerServer.PasswordCrackerServiceHandler.addWorker;
//import static PasswordCrackerServer.PasswordCrackerServiceHandler.workersInfo;
//
//public class PasswordCrackerTransport extends TServerTransport {
//    public final TServerSocket serverSocket;
//    public final JSONObject jsonWorkersInfo;
//
//    public PasswordCrackerTransport(int port, JSONObject workerInfo) throws TTransportException {
//        this.serverSocket = new TServerSocket(port);
//        this.jsonWorkersInfo = workerInfo;
//    }
//
//    @Override
//    public void listen() throws TTransportException {
//        serverSocket.listen();
//    }
//
//    @Override
//    public void close() {
//        serverSocket.close();
//    }
//
//    @Override
//    protected TTransport acceptImpl() throws TTransportException {
//        try {
//            Socket socket = this.serverSocket.getServerSocket().accept();
//            String connectedAddress = socket.getInetAddress().getHostAddress();
//            String searchPrefix = "worker";
//
//            // workers : Object List for RPC
//            for (int i = workersInfo.size(); i < NUMBER_OF_WORKER; i++) {
//                JSONArray workerInfo = (JSONArray) jsonWorkersInfo.get(searchPrefix + (i + 1));
//                String ipAddress = (String) workerInfo.get(0);
//                int port = Integer.parseInt((String) workerInfo.get(1));
//
//                if (ipAddress.equals(connectedAddress)) {
//                    TSocket tSocket = new TSocket(socket);
//                    addWorker(new WorkerInfo(ipAddress, port));
//                    return tSocket;
//                }
//            }
//
//            if (workersInfo.size() < NUMBER_OF_WORKER) {
//                socket.close();
//                return null;
//            }
//
//            return new TSocket(socket);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (TException e) {
//            e.printStackTrace();
//        }
//        throw new TTransportException(1, "No underlying server socket.");
//    }
//}
