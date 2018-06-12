package PasswordCracker;

import com.facebook.presto.spi.*;
import com.facebook.presto.spi.connector.ConnectorMetadata;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static PasswordCracker.CrackerConsts.*;
import static java.util.Objects.requireNonNull;
import static PasswordCracker.Types.checkType;

public class CrackerMetadata implements ConnectorMetadata
{
    private final String connectorId;

    public CrackerMetadata(String connectorId)
    {
        this.connectorId = connectorId;
    }

    @Override
    public List<String> listSchemaNames(ConnectorSession session)
    {
        return ImmutableList.of(SCHEMA_NAME);
    }

    @Override
    public ConnectorTableHandle getTableHandle(ConnectorSession session, SchemaTableName schemaTableName)
    {
        if (!schemaTableName.getTableName().equals(TABLE_NAME)) {
            return null;
        }
        return new CrackerTableHandle(connectorId, schemaTableName.getTableName());
    }

    @Override
    public List<ConnectorTableLayoutResult> getTableLayouts(ConnectorSession session, ConnectorTableHandle tableHandle, Constraint<ColumnHandle> constraint, Optional<Set<ColumnHandle>> desiredColumns)
    {
        CrackerTableHandle crackerTableHandle = checkType(tableHandle, CrackerTableHandle.class, "tableHandle");
        ConnectorTableLayout layout = new ConnectorTableLayout(new CrackerTableLayoutHandle(crackerTableHandle));
        return ImmutableList.of(new ConnectorTableLayoutResult(layout, constraint.getSummary()));
    }

    @Override
    public ConnectorTableLayout getTableLayout(ConnectorSession session, ConnectorTableLayoutHandle handle)
    {
        return new ConnectorTableLayout(handle);
    }

    @Override
    public ConnectorTableMetadata getTableMetadata(ConnectorSession session, ConnectorTableHandle tableHandle)
    {
        CrackerTableHandle crackerTableHandle = checkType(tableHandle, CrackerTableHandle.class, "tableHandle");
        SchemaTableName schemaTableName = new SchemaTableName(SCHEMA_NAME, crackerTableHandle.getTableName());

        return new ConnectorTableMetadata(schemaTableName, ImmutableList.of(new ColumnMetadata(COLUMN_NAME, COLUMN_TYPE)));
    }

    @Override
    public List<SchemaTableName> listTables(ConnectorSession session, String schemaNameOrNull)
    {
        if (!schemaNameOrNull.equals(SCHEMA_NAME)) {
            return null;
        }

        return ImmutableList.of(new SchemaTableName(schemaNameOrNull, TABLE_NAME));
    }

    @Override
    public Map<String, ColumnHandle> getColumnHandles(ConnectorSession session, ConnectorTableHandle tableHandle)
    {
        return ImmutableMap.of(COLUMN_NAME, new CrackerColumnHandle(COLUMN_NAME, COLUMN_TYPE));
    }

    @Override
    public ColumnMetadata getColumnMetadata(ConnectorSession session, ConnectorTableHandle tableHandle, ColumnHandle columnHandle)
    {
        CrackerColumnHandle crackerColumnHandle = checkType(columnHandle, CrackerColumnHandle.class, "columnHandle");
        return new ColumnMetadata(crackerColumnHandle.getColumnName(), crackerColumnHandle.getType());
    }

    @Override
    public Map<SchemaTableName, List<ColumnMetadata>> listTableColumns(ConnectorSession session, SchemaTablePrefix prefix)
    {
        return null;
    }
}
