package com.sweet.android.service.job;

import android.app.job.JobInfo;

/**
 * JobScheduler参数类
 */
public class RetryData {

    public int jobId = -1;
    public long minLatencyMillis = 0l;
    public long maxExecutionDelayMillis = 0l;
    public int networkType = JobInfo.NETWORK_TYPE_NONE;
    public String from = "";

    public RetryData(int jobId, long minLatencyMillis, long maxExecutionDelayMillis, int networkType) {
        this.jobId = jobId;
        this.minLatencyMillis = minLatencyMillis;
        this.maxExecutionDelayMillis = maxExecutionDelayMillis;
        this.networkType = networkType;
    }

    public RetryData(int jobId, long minLatencyMillis, long maxExecutionDelayMillis, int networkType, String from) {
        this(jobId, minLatencyMillis, maxExecutionDelayMillis, networkType);
        this.from = from;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("jobId: ");sb.append(jobId);sb.append(", ");
        sb.append("minDelay: ");sb.append(minLatencyMillis);sb.append(", ");
        sb.append("maxDelay: ");sb.append(maxExecutionDelayMillis);sb.append(", ");
        sb.append("netType: ");sb.append(networkType);sb.append(", ");
        sb.append("from: ");sb.append(from);
        sb.append("}");
        return sb.toString();
    }
}
