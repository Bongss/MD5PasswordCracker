package PasswordCracker;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

public class CandidateRangeRecordReader extends RecordReader<Text, Text> {
    private String rangeBegin;
    private String rangeEnd;
    private boolean done = false;

    @Override
    public Text getCurrentKey() throws IOException, InterruptedException {
        return new Text(rangeBegin);
    }

    @Override
    public Text getCurrentValue() throws IOException, InterruptedException {
        return new Text(rangeEnd);
    }

    @Override
    public void initialize(InputSplit split, TaskAttemptContext context)
            throws IOException, InterruptedException {
        CandidateRangeInputSplit candidataRangeSplit = (CandidateRangeInputSplit) split;
        String[] candidateRange = candidataRangeSplit.getInputRange().split(" "); //split the input value
        rangeBegin = candidateRange[0];
        rangeEnd = candidateRange[1];
    }

    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException {
        if (done) {
            return false;
        } else {
            done = true;
            return true;
        }
    }

    @Override
    public float getProgress() throws IOException, InterruptedException {
        if (done) {
            return 1.0f;
        }
        return 0.0f;
    }

    @Override
    public void close() throws IOException {
    }
}
