package PasswordCracker;

import com.facebook.presto.spi.ColumnHandle;
import com.facebook.presto.spi.ConnectorSession;
import com.facebook.presto.spi.ConnectorSplit;
import com.facebook.presto.spi.RecordSet;
import com.facebook.presto.spi.connector.ConnectorRecordSetProvider;
import com.facebook.presto.spi.connector.ConnectorTransactionHandle;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static PasswordCracker.Types.checkType;

public class CrackerRecordSetProvider implements ConnectorRecordSetProvider{

    @Override
    public RecordSet getRecordSet(ConnectorTransactionHandle transactionHandle, ConnectorSession session, ConnectorSplit split, List<? extends ColumnHandle> columns)
    {
        CrackerSplit crackerSplit = checkType(split, CrackerSplit.class, "split");
        long candidateRangeBegin = crackerSplit.getRangeBegin();
        long candidateRangeSize = crackerSplit.getRangeEnd() - crackerSplit.getRangeBegin();

        return new CrackerRecordSet(candidateRangeBegin, candidateRangeSize);
    }

}
