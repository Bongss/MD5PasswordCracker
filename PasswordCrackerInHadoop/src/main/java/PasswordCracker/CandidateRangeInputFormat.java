/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package PasswordCracker;

import static PasswordCracker.PasswordCrackerUtil.TOTAL_PASSWORD_RANGE_SIZE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

public class CandidateRangeInputFormat extends InputFormat<Text, Text> {
    private List<InputSplit> splits;

    @Override
    public RecordReader<Text, Text> createRecordReader(InputSplit split, TaskAttemptContext context)
            throws IOException, InterruptedException {
        return new CandidateRangeRecordReader();
    }

    @Override
    public List<InputSplit> getSplits(JobContext job) throws IOException, InterruptedException {
        int numberOfSplit = job.getConfiguration().getInt("numberOfSplit", 1);    //get map_count
        long subRangeSize = (TOTAL_PASSWORD_RANGE_SIZE + numberOfSplit - 1) / numberOfSplit;
//        String host = job.getConfiguration().get("fs.default.name");
        Path path = new Path("hdfs://mission:50000/user/hduser/NOTICE.txt");
        FileSystem fs = path.getFileSystem(job.getConfiguration());
        BlockLocation[] blockLocations = fs.getFileBlockLocations(path, 0, 0);
        for(String host : blockLocations[0].getHosts()) {
            System.out.println("!!!!!!!!!!!!!!!!!!!" + host + "!!!!!!!!!!!!!!!!!!!!");
        }
        splits = new ArrayList<>();

        for (int i = 0; i < numberOfSplit; i++) {
            long subRangeBegin = i * subRangeSize;
            long subRangeEnd = subRangeSize * (i + 1);

            if (subRangeEnd >= TOTAL_PASSWORD_RANGE_SIZE) {
                subRangeEnd = TOTAL_PASSWORD_RANGE_SIZE;
            }
            String inputRange = subRangeBegin + " " + subRangeEnd;
            splits.add(new CandidateRangeInputSplit(inputRange, inputRange.length(), blockLocations[0].getHosts()));
        }
        return splits;
    }
}