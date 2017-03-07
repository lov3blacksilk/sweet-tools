package com.sweet.android.service.job;

import android.app.job.JobParameters;
import com.sweet.android.util.Log;


/**
 * 异步工作实现者
 * @author fengzihua
 *
 */
public abstract class SingleWorker implements Worker {
    
    private static final String TAG = "S-work";

    //JobScheduler的相关job id
    public static final int PULL_JOB_ID = 1000;
    public static final int PUSH_JOB_ID = 1001;
    public static final int TIP_JOB_ID     = 1002;
    public static final int FAKE_JOB_ID     = 1003;
    public static final int FAKE_PULL_JOB_ID     = 1004;
    public static final int FAKE_PUSH_JOB_ID     = 1005;
    
    protected JobCallback mJobCallback;
    protected int mJobId = -1;


    public SingleWorker(JobCallback jobCallback) {
        mJobCallback = jobCallback;
    }

    public SingleWorker(JobCallback jobCallback, int jobId){
        mJobCallback = jobCallback;
        mJobId = jobId;
    }

    @Override
    public String getTag() {
        return TAG;
    }
    
    /**
     * 对应的jobId
     * @return
     */
    public abstract int getJobId();
    
    /** 
     * Receives callback from the service when a job has landed 
     * on the app. Colours the UI and post a message to 
     * uncolour it after a second. 
     */  
    public void onReceivedStartJob(JobParameters params) {
        Log.d(getTag(), "onReceivedStartJob, jobId: " + params.getJobId() + ", " + params.getExtras() + " Thread: " + Thread.currentThread().getName());
        start(params);
    }
    
    /**
     * 对应的jobId
     * 
     * @return
     */
    public abstract int start(JobParameters params);

  
    /** 
     * Receives callback from the service when a job that 
     * previously landed on the app must stop executing. 
     * Colours the UI and post a message to uncolour it after a 
     * second. 
     */  
    public void onReceivedStopJob(JobParameters params) {  
        Log.d(getTag(), "onReceivedStopJob, jobId: " + params.getJobId() + ",  " + params.getExtras() + " Thread: " + Thread.currentThread().getName());
    }
}