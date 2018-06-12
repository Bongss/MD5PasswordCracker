package PasswordCracker;

import static PasswordCracker.PasswordCrackerUtil.findPasswordInRange;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class PasswordCrackerMapper
        extends Mapper<Text, Text, Text, Text> {
    public void map(Text key, Text value, Context context)
            throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();
        String flagFilename = conf.get("terminationFlagFilename");
        FileSystem hdfs = FileSystem.get(conf);

        TerminationChecker terminationChecker = new TerminationChecker(hdfs, flagFilename);

        long rangeBegin = Long.parseLong(key.toString());
        long rangeEnd = Long.parseLong(value.toString());

        String encryptedPassword = conf.get("encryptedPassword");
        String password = findPasswordInRange(rangeBegin, rangeEnd, encryptedPassword, terminationChecker);
        if (password != null) {
            terminationChecker.setTerminated();
            context.write(new Text(encryptedPassword), new Text(password));
        }
    }
}

class TerminationChecker {
    FileSystem fs;
    Path flagPath;

    TerminationChecker(FileSystem fs, String flagFilename) {
        this.fs = fs;
        this.flagPath = new Path(flagFilename);
    }

    public boolean isTerminated() throws IOException {
        return fs.exists(flagPath);
    }

    public void setTerminated() throws IOException {
        fs.create(flagPath);
    }
}
