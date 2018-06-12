package PasswordCracker;

import com.facebook.presto.spi.*;
import com.facebook.presto.spi.connector.ConnectorPartitioningHandle;
import com.facebook.presto.spi.connector.ConnectorTransactionHandle;

public class CrackerHandleResolver implements ConnectorHandleResolver
{
    @Override
    public Class<? extends ConnectorTableHandle> getTableHandleClass()
    {
        return CrackerTableHandle.class;
    }

    @Override
    public Class<? extends ConnectorTableLayoutHandle> getTableLayoutHandleClass()
    {
        return CrackerTableLayoutHandle.class;
    }

    @Override
    public Class<? extends ColumnHandle> getColumnHandleClass()
    {
        return CrackerColumnHandle.class;
    }

    @Override
    public Class<? extends ConnectorTransactionHandle> getTransactionHandleClass()
    {
        return CrackerTransactionHandle.class;
    }

    @Override
    public Class<? extends ConnectorSplit> getSplitClass()
    {
        return CrackerSplit.class;
    }
}
