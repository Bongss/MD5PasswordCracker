package PasswordCracker;

import com.facebook.presto.spi.ConnectorTableLayoutHandle;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CrackerTableLayoutHandle implements ConnectorTableLayoutHandle
{
    private final CrackerTableHandle table;

    @JsonCreator
    public CrackerTableLayoutHandle(@JsonProperty("table") CrackerTableHandle table)
    {
        this.table = table;
    }
    @JsonProperty
    public CrackerTableHandle getTable()
    {
        return table;
    }
    public String getConnectorId()
    {
        return table.getConnectorId();
    }

    @Override
    public String toString()
    {
        return table.toString();
    }
}
