package PasswordCracker;

import com.facebook.presto.spi.type.Type;

import static com.facebook.presto.spi.type.VarcharType.VARCHAR;

final class CrackerConsts
{
    public static final String SCHEMA_NAME = "cracker";
    public static final String TABLE_NAME = "password";
    public static final String COLUMN_NAME = "password";
    public static final Type COLUMN_TYPE = VARCHAR;
}
