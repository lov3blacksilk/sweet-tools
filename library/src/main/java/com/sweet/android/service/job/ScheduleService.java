package com.sweet.android.service.job;

import java.util.HashMap;
import java.util.Map;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import com.sweet.android.util.Log;

/**
 * 定时工作任务service
 * 
 * @author fengzihua
 *
 */
public class ScheduleService extends JobService implements JobCallback {
    
    private static final String TAG = "PJobS";

    private Map<Integer, SingleWorker> mWorker;
    
    @Override  
    public void onCreate() { 
        Log.d(TAG, "created--------------------------->");
        super.onCreate();
        if(mWorker == null) {
            mWorker = new HashMap<Integer, SingleWorker>(3);
        }
    }

    @Override
    public void onDestroy() {
        if (mWorker != null) {
            mWorker.clear();
            mWorker = null;
        }
        super.onDestroy();
        Log.d(TAG, "destroyed<------------------------------");
    }

    /** 
     * When the app's MainActivity is created, it starts this service. This is so that the 
     * activity and this service can communicate back and forth. See "setUiCalback()" 
     */  
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand..., startId: " + startId);
        return super.onStartCommand(intent, flags, startId);
    }
  
    /**
     * 收到一个新的任务，全表检查，不限于某一个group id：
     * 1.启动upload service，为上传的apk进行上传
     * 2.启动download service，未下载完成的apk进行下载，安装
     */
    @Override public boolean onStartJob(JobParameters params) {
        if (mWorker == null || params == null) {
            Log.d(TAG, "on start job: mWorker or params is null return, t: " + Thread.currentThread().getName());
            return false;
        }
        final int jobId = params.getJobId();
        SingleWorker worker = mWorker.get(jobId);
        if(worker == null) {
            Log.d(TAG, "onStartJob,  job: " + jobId + " worker is null, t: " + Thread.currentThread().getName());
            worker = createWork(jobId);
        }
        if(worker == null) {
            Log.d(TAG, "onStartJob,  job: " + jobId + " job is not support, t: " + Thread.currentThread().getName());
            return false;
        }
        mWorker.put(jobId, worker);
        Log.d(TAG, "onStartJob, job: " + jobId + ", t: " + Thread.currentThread().getName());
        worker.onReceivedStartJob(params);
        return true;  
    }
    


    /**
     * 系统回调结束该任务
     */
    @Override  public boolean onStopJob(JobParameters params) {
        if (mWorker == null || params == null) {
            Log.d(TAG, "on stop job, mWorker or params is null return, t: " + Thread.currentThread().getName());
            return false;
        }
        final int jobId = params.getJobId();
        SingleWorker worker = mWorker.get(jobId);
        if(worker == null) {
            Log.d(TAG, "onStopJob,  job: " + jobId + " worker is null, t: " + Thread.currentThread().getName());
            return false;
        }
        Log.d(TAG, "onStopJob, job: " + jobId + ", t: " + Thread.currentThread().getName());
        worker.onReceivedStopJob(params);
        return false;
    }

    /**
     * Not currently used, but as an exercise you can hook this 
     * up to a button in the UI to finish a job that has landed 
     * in onStartJob(). 
     * 告诉系统结束该任务
     */  
    @Override public boolean callJobFinished(JobParameters params, boolean needReschedule) {
        if (params == null) {  
            return false;  
        } else {
            Log.d(TAG, "callJobFinished...parames="+ params.toString() + ", reschedule=" + needReschedule);
            jobFinished(params, needReschedule);
            return true;  
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    /**
     * 根据jobId构建对应的worker
     * @param jobId
     * @return
     */
    private SingleWorker createWork(int jobId) {
        SingleWorker work = null;
        //TODO 返回一个worker
        return work;
    }
}