import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.HashMap;

public class NonblockingServer {

    private void start() {
        try {
            TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(7911);
            TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(serverTransport);
            args.transportFactory(new TFramedTransport.Factory());
            args.protocolFactory(new TBinaryProtocol.Factory());
            ArithmeticService.Processor processor = new ArithmeticService.Processor(new ArithmeticServiceImpl());
            args.processor(processor);
            args.selectorThreads(4);
            args.workerThreads(32);
            TServer server = new TThreadedSelectorServer(args);

            System.out.println("Starting server on port 7911 ...");
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        HashMap<String, Integer> hashMapTest = new HashMap<>();
        hashMapTest.put("in", 1);
        hashMapTest.put("out", 2);

        //NonblockingServer srv = new NonblockingServer();
        //srv.start();
    }
}

