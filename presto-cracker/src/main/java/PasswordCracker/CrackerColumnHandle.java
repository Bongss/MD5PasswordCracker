package PasswordCracker;

import com.facebook.presto.spi.ColumnHandle;
import com.facebook.presto.spi.type.Type;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class CrackerColumnHandle implements ColumnHandle{
    private final String columnName;
    private final Type type;

    @JsonCreator
    public CrackerColumnHandle(@JsonProperty("columnName") String columnName, @JsonProperty("type") Type type)
    {
        this.columnName = requireNonNull(columnName, "columnName is null");
        this.type = requireNonNull(type, "type is null");
    }

    @JsonProperty
    public String getColumnName(){
        return columnName;
    }

    @JsonProperty
    public Type getType(){
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        CrackerColumnHandle other = (CrackerColumnHandle) obj;
        return Objects.equals(columnName, other.columnName);
    }

    @Override
    public String toString() {
        return "PasswordCracker:"+columnName;
    }
}
