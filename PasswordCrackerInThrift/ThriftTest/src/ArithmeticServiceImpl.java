import org.apache.thrift.TException;

import java.util.concurrent.TimeUnit;

public class ArithmeticServiceImpl implements ArithmeticService.Iface {

    public long add(int num1, int num2) throws TException {

        try {
            TimeUnit.SECONDS.sleep(5);
            System.out.println("!!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return num1 + num2;
    }

    public long multiply(int num1, int num2) throws TException {
        return num1 * num2;
    }

}
