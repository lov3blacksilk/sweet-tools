package com.sweet.android.service.job;

import android.app.job.JobParameters;

public interface Worker {
    
    /**
     * 打印日志
     * @return
     */
    public String getTag();
    
    /** 
     * 收到工作结束
     */  
    public void onReceivedStartJob(JobParameters params);
  
    /** 
     * 收到工作开始
     */  
    public void onReceivedStopJob(JobParameters params);
}