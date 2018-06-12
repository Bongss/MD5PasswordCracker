package PasswordCracker;

import com.facebook.presto.spi.*;
import com.facebook.presto.spi.connector.ConnectorSplitManager;
import com.facebook.presto.spi.connector.ConnectorTransactionHandle;
import com.google.common.collect.ImmutableList;

import java.util.Set;

import static PasswordCracker.CrackerUtil.TOTAL_PASSWORD_RANGE_SIZE;
import static com.google.common.base.Preconditions.checkArgument;
import static PasswordCracker.Types.checkType;

public class CrackerSplitManager implements ConnectorSplitManager
{
    private final NodeManager nodeManager;
    private final int splitsPerNode;

    public CrackerSplitManager(NodeManager nodeManager, int splitsPerNode)
    {
        this.nodeManager = nodeManager;
        checkArgument(splitsPerNode > 0, "splitsPerNode must be at least 1");
        this.splitsPerNode = splitsPerNode;
    }

    @Override
    public ConnectorSplitSource getSplits(ConnectorTransactionHandle transactionHandle, ConnectorSession session, ConnectorTableLayoutHandle layout)
    {
        CrackerTableHandle crackerTableHandle = checkType(layout, CrackerTableLayoutHandle.class, "layout").getTable();

        Set<Node> nodes = nodeManager.getRequiredWorkerNodes();

        int numberOfSplit = nodes.size() * splitsPerNode;
        long subRangeSize = (TOTAL_PASSWORD_RANGE_SIZE + numberOfSplit - 1) / numberOfSplit;

        ImmutableList.Builder<ConnectorSplit> splits = ImmutableList.builder();

        for (Node node : nodes) {
            for (int i = 0; i < splitsPerNode * nodes.size(); i++) {
                long subRangeBegin = i * subRangeSize;
                long subRangeEnd = (i + 1) * subRangeSize;

                if (subRangeEnd >= TOTAL_PASSWORD_RANGE_SIZE) {
                    subRangeEnd = TOTAL_PASSWORD_RANGE_SIZE;
                }

                splits.add(new CrackerSplit(crackerTableHandle, subRangeBegin, subRangeEnd, ImmutableList.of(node.getHostAndPort())));
            }
        }
        return new FixedSplitSource(splits.build());
    }
}
