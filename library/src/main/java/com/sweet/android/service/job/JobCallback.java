package com.sweet.android.service.job;

import android.app.job.JobParameters;
import android.content.Context;

public interface JobCallback {
    
    /**
     * 获取上下文 
     * @return
     */
    public Context getContext();

    /**
     * Not currently used, but as an exercise you can hook this 
     * up to a button in the UI to finish a job that has landed 
     * in onStartJob(). 
     * 取消其中一个任务
     */  
    public boolean callJobFinished(JobParameters params, boolean needReschedule);
}
