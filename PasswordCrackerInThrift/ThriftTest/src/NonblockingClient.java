import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.*;

public class NonblockingClient {

    private void invoke() {
        TTransport transport;
        try {
            transport = new TFramedTransport(new TSocket("localhost", 7911));
            TProtocol protocol = new TBinaryProtocol(transport);

            ArithmeticService.Client client = new ArithmeticService.Client(protocol);
            transport.open();

            long addResult = client.add(100, 200);
            System.out.println("Add result: " + addResult);
            long multiplyResult = client.multiply(20, 40);
            System.out.println("Multiply result: " + multiplyResult);

            transport.close();
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Integer a = 100, b = 100;
        Integer c = 1000, d = 1000;
        Integer e = new Integer(100);

        ArrayList<Integer> cacheTestArray = new ArrayList<>();
        cacheTestArray.add(100);

        Integer testCalue = cacheTestArray.get(0);
        System.out.println("Less than 128 Integer Object Compare : " + (a == b));
        System.out.println("More than 1000 Integer Compare : " + (c == d));
        System.out.println("New Less than 128 Integer Object Compare : " + (b == e));
        System.out.println("Compare ArrayList Integer and Less than 128 Integer Object : " + (testCalue == a));

        //NonblockingClient c = new NonblockingClient();
        //c.invoke();
    }

}