package PasswordCracker;

import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;

import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

public class CandidateRangeInputSplit extends InputSplit implements Writable {
    private String candidateRange;
    private long rangeBegin;
    private long rangeEnd;
    private String[] hosts;

    public CandidateRangeInputSplit(String inputRange, long inputRangeStringLength, String[] hosts) {
        this.candidateRange = inputRange;
        this.rangeBegin = 0;
        this.rangeEnd = inputRangeStringLength;
        this.hosts = hosts;
    }
    
    public CandidateRangeInputSplit() {
    	
    }

    public String getInputRange() {
        return candidateRange;
    }

    public long getStart() {
        return rangeBegin;
    }

    @Override
    public long getLength() {
        return rangeEnd;
    }

    @Override
    public String toString() {
        return candidateRange + ":" + rangeBegin + "+" + rangeEnd;
    }

    @Override
    public String[] getLocations() throws IOException {
        if (this.hosts == null) {
            return new String[]{};
        } else {
            return this.hosts;
        }
    }

    public void readFields(DataInput in) throws IOException {
        candidateRange = Text.readString(in);
        rangeBegin = in.readLong();
        rangeEnd = in.readLong();
        hosts = null;
    }

    public void write(DataOutput out) throws IOException {
        Text.writeString(out, candidateRange.toString());
        out.writeLong(rangeBegin);
        out.writeLong(rangeEnd);
    }
}