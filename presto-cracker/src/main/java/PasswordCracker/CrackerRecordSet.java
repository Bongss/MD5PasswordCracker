package PasswordCracker;

import com.facebook.presto.spi.RecordCursor;
import com.facebook.presto.spi.RecordSet;
import com.facebook.presto.spi.type.Type;
import com.google.common.collect.ImmutableList;

import java.util.List;

import static PasswordCracker.CrackerConsts.COLUMN_TYPE;

public class CrackerRecordSet implements RecordSet
{
    private final long candidateRangeBegin;
    private final long candidateRangeSize;

    public CrackerRecordSet(long candidateRangeBegin, long candidateRangeSize)
    {
        this.candidateRangeBegin = candidateRangeBegin;
        this.candidateRangeSize = candidateRangeSize;
    }

    @Override
    public List<Type> getColumnTypes()
    {
        return ImmutableList.of(COLUMN_TYPE);
    }

    @Override
    public RecordCursor cursor()
    {
        return new CrackerRecordCursor(candidateRangeBegin, candidateRangeSize);
    }

}
