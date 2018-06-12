package PasswordCracker;

import com.facebook.presto.spi.RecordCursor;
import com.facebook.presto.spi.type.Type;
import io.airlift.slice.Slice;
import io.airlift.slice.Slices;

import static PasswordCracker.CrackerConsts.COLUMN_TYPE;
import static PasswordCracker.CrackerUtil.*;

public class CrackerRecordCursor implements RecordCursor
{
    private final long candidateRangeBegin;
    private long candidateRangeSize;
    private boolean closed = false;

    private int[] candidateChars = new int[PASSWORD_LEN];
    private String candidatePassword;

    private long testRangeSize;

    public CrackerRecordCursor(long candidateRangeBegin, long candidateRangeSize)
    {
        this.candidateRangeBegin = candidateRangeBegin;
        this.candidateRangeSize = candidateRangeSize;

        initCandidateChars(candidateRangeBegin - 1, candidateChars);
        candidatePassword = transformIntoStr(candidateChars);

        testRangeSize = candidateRangeSize - 4;
    }
    @Override
    public long getTotalBytes()
    {
        return 0;
    }

    @Override
    public long getCompletedBytes()
    {
        return 0;
    }

    @Override
    public long getReadTimeNanos()
    {
        return 0;
    }

    @Override
    public Type getType(int field)
    {
        return COLUMN_TYPE;
    }

    @Override
    public synchronized boolean advanceNextPosition()
    {
        if (closed || (candidateRangeSize == testRangeSize)) {
            return false;
        }
        getNextCandidate(candidateChars);
        candidatePassword = transformIntoStr(candidateChars);
        candidateRangeSize--;

        return true;
    }

    @Override
    public boolean getBoolean(int field)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getLong(int field)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getDouble(int field)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Slice getSlice(int field)
    {
        return Slices.utf8Slice(candidatePassword);
    }

    @Override
    public Object getObject(int field)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isNull(int field)
    {
        return false;
    }

    @Override
    public void close()
    {
        closed = true;
    }
}
