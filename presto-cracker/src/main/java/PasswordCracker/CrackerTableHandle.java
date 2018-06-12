package PasswordCracker;

import com.facebook.presto.spi.ConnectorTableHandle;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class CrackerTableHandle implements ConnectorTableHandle
{
    private final String connectorId;
    private final String tableName;

    @JsonCreator
    public CrackerTableHandle(@JsonProperty("connectorId") String connectorId, @JsonProperty("tableName") String tableName)
    {
        this.connectorId = requireNonNull(connectorId, "connectorId is Null");
        this.tableName = requireNonNull(tableName, "tableName is null");
    }
    @JsonProperty
    public String getConnectorId()
    {
        return connectorId;
    }

    @JsonProperty
    public String getTableName()
    {
        return tableName;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(connectorId, tableName);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CrackerTableHandle other = (CrackerTableHandle) obj;
        return Objects.equals(this.tableName, other.tableName) &&
                Objects.equals(this.connectorId, other.connectorId);
    }

    // toString() : pluginName:tableName
    @Override
    public String toString()
    {
        return "cracker:" + tableName;
    }
}
