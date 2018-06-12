package PasswordCracker;

import com.facebook.presto.spi.ConnectorHandleResolver;
import com.facebook.presto.spi.NodeManager;
import com.facebook.presto.spi.connector.*;
import com.facebook.presto.spi.transaction.IsolationLevel;

import java.util.Map;

import static com.google.common.base.MoreObjects.firstNonNull;

public class CrackerConnectorFactory implements ConnectorFactory
{
    private final int defaultSplitsPerNode;
    public CrackerConnectorFactory()
    {
        this(Runtime.getRuntime().availableProcessors());
    }
    public CrackerConnectorFactory(int defaultSplitsPerNode)
    {
        this.defaultSplitsPerNode = 4;
    }

    @Override
    public String getName()
    {
        return "cracker";
    }

    @Override
    public ConnectorHandleResolver getHandleResolver()
    {
        return new CrackerHandleResolver();
    }

    @Override
    public Connector create(String connectorId, Map<String, String> config, ConnectorContext context)
    {
        NodeManager nodeManager = context.getNodeManager();
        return new Connector()
        {

            @Override
            public ConnectorTransactionHandle beginTransaction(IsolationLevel isolationLevel, boolean readOnly)
            {
                return CrackerTransactionHandle.INSTANSE;
            }

            @Override
            public ConnectorMetadata getMetadata(ConnectorTransactionHandle transactionHandle)
            {
                return new CrackerMetadata(connectorId);
            }

            @Override
            public ConnectorSplitManager getSplitManager()
            {
                return new CrackerSplitManager(nodeManager, defaultSplitsPerNode);
            }

            @Override
            public ConnectorRecordSetProvider getRecordSetProvider()
            {
                return new CrackerRecordSetProvider();
            }

        };
    }

    private int getSplitsPerNode(Map<String, String> properties)
    {
        try{
            return Integer.parseInt(firstNonNull(properties.get("cracker.splits-per-node"), String.valueOf(defaultSplitsPerNode)));
        }
        catch(NumberFormatException e){
            throw new IllegalArgumentException("Invalid property cracker.splits-per-node");
        }
    }
}
