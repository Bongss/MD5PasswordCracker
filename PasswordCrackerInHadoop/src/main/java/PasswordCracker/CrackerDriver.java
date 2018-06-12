package PasswordCracker;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;


import org.apache.hadoop.mapreduce.Job;
//import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.security.MessageDigest;

import static PasswordCracker.PasswordCrackerUtil.getMessageDigest;

public class CrackerDriver {
    private static final int numberOfSplit = 4;

    public static void main(String[] args)
            throws Exception {
        if (args.length != 2 || args[1].length() != 8) {
            System.err.println("Usage : hadoop jar CrackerInHadoop.jar outputPath password");
            System.exit(1);
        }

        Configuration conf = new Configuration();

        MessageDigest messageDigest = getMessageDigest();

        String encryptedPassword = PasswordCrackerUtil.encrypt(args[1], messageDigest);
        String outputPath = args[0];


        conf.setInt("numberOfSplit", numberOfSplit);
        conf.set("encryptedPassword", encryptedPassword);
        conf.set("terminationFlagFilename", "Found" + System.currentTimeMillis());

        Job job = Job.getInstance(conf);

        job.setJarByClass(CrackerDriver.class);
        job.setMapperClass(PasswordCrackerMapper.class);
        job.setReducerClass(PasswordCrackerReducer.class);
        job.setInputFormatClass(CandidateRangeInputFormat.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileOutputFormat.setOutputPath(job, new Path(outputPath));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
