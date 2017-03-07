package com.sweet.android.service.job;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;
import com.google.common.base.Preconditions;
import com.sweet.android.util.Log;

/**
 * 管理recycle机制
 * @author fengzihua
 *
 * 1.wifi连接
 * 2.开始重试
 * 3.记录次数为1,2,3
 * 
 * 如果重试次数增加到3，则设置alarm重试.
 *
 * job说明:
 * 1.有网，无delay，使用startService，直接使用person check
 * 2.有网，有delay，设置job + maxtime
 * 3.无网，设置job + fackjob
 * 4.一个小时的重试，使用min:60分钟，max:120分钟的job
 */
public class ScheduleManager {

    private static final String TAG = "P-SM";

    // 有wifi时，3次短重试全部失败，设置重试job的间隔时间，1个小时
    public static final long PULL_RETYR_JOB_MIN_INTERVAL = 3600000l;
    public static final long PULL_RETYR_JOB_MAX_INTERVAL = 7200000l;

    private static ScheduleManager mInstance;

    private Context mContext;
    private JobScheduler mJobScheduler;

    /**
     * singletone
     * @return
     */
    public static ScheduleManager getInstance() {
        if (mInstance == null) {
            synchronized (ScheduleManager.class) {
                if (mInstance == null)
                    mInstance = new ScheduleManager();
            }
        }
        return mInstance;
    }
    
    private ScheduleManager() {
    }

    public void init(Context context){
        mContext = context.getApplicationContext();
    }

    /**
     * 启动job服务
     */
    private final void scheduleJobLocked(RetryData data) {
        // 如果以前有同样reason的任务，取消之前的任务
        // 比如原来就有重试push的任务，现在又来一个，就取消原来的任务
        if(mJobScheduler == null) {
            mJobScheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        }
        
        // 先取消前一个任务
        mJobScheduler.cancel(data.jobId);
        
        ComponentName serviceComponent = new ComponentName(mContext, ScheduleService.class);
        JobInfo.Builder builder = new JobInfo.Builder(data.jobId, serviceComponent);
        // 重试一个小时时打开   延迟多久执行
        builder.setMinimumLatency(data.minLatencyMillis);
        if(data.maxExecutionDelayMillis > 0) {
        	// 最晚什么时候触发
            builder.setOverrideDeadline(data.maxExecutionDelayMillis);
        }
        // 设置网络类型
        builder.setRequiredNetworkType(data.networkType);
        PersistableBundle extra = new PersistableBundle();
        //TODO 添加一些额外的参数
        builder.setExtras(extra);
        
        int result = mJobScheduler.schedule(builder.build());
        Log.d(TAG, "scheduleJobLocked, result: " + result + ", data: " + data);
    }
    
    /**
     * 取消任务
     * @param jobId
     */
    private final void cancelJob(int jobId) {
        if (jobId <= 0) {
            return;
        }
        Log.d(TAG, "cancelJob jobId: " + jobId);
        if(mJobScheduler == null) {
            mJobScheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        }
        mJobScheduler.cancel(jobId);
    }

    /**
     * 取消所有任务
     */
    private final void cancelAllJob() {
        Log.d(TAG, "cancelAllJob");
        if(mJobScheduler == null) {
            mJobScheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        }
        mJobScheduler.cancelAll();
    }

    /**
     * 设置重新拉取job
     * 有网，没有delay，使用startCheckPull拉取数据
     * 有网，有delay，使用job + maxtime
     *
     * @param delay 延迟多久触发
     * @return
     */
    public static final boolean schedulePullJob(boolean cancel, long delay, long maxTime, String from) {
        if(cancel) {
            getInstance().cancelJob(SingleWorker.PULL_JOB_ID);
            getInstance().cancelJob(SingleWorker.FAKE_PULL_JOB_ID);
        } else {
            RetryData data = null;
            if (delay > 0 && maxTime > 0) {
                // delay大于0，设置max time
                data = new RetryData(SingleWorker.PULL_JOB_ID, delay, maxTime, JobInfo.NETWORK_TYPE_ANY, from);
                getInstance().scheduleJobLocked(data);
            } else {
                // 设置pull job
                data = new RetryData(SingleWorker.PULL_JOB_ID, delay, 0,  JobInfo.NETWORK_TYPE_ANY, from);
                getInstance().scheduleJobLocked(data);

                // 设置fake job，以保证push job能够启动
                RetryData fakeData = new RetryData(SingleWorker.FAKE_PULL_JOB_ID, delay, 0, JobInfo.NETWORK_TYPE_ANY);
                getInstance().scheduleJobLocked(fakeData);
            }
        }
        return true;
    }
    
    /**
     * 设置重新注册push job
     * 无网时设置该job
     * pushjob + fakejob
     *
     * @param delay 延迟多久触发
     * @return
     */
    public static final boolean schedulePushJob(boolean cancel, long delay, long maxTime) {
        if(cancel) {
            getInstance().cancelJob(SingleWorker.PUSH_JOB_ID);
            getInstance().cancelJob(SingleWorker.FAKE_PUSH_JOB_ID);
        } else {
            RetryData data = null;
            if (delay > 0 && maxTime > 0) {
                // delay大于0，设置max time
                data = new RetryData(SingleWorker.PUSH_JOB_ID, delay, maxTime, JobInfo.NETWORK_TYPE_ANY);
                getInstance().scheduleJobLocked(data);
            } else {
                // 设置push job
                data = new RetryData(SingleWorker.PUSH_JOB_ID, delay, 0,  JobInfo.NETWORK_TYPE_ANY);
                getInstance().scheduleJobLocked(data);

                // 设置fake job，以保证push job能够启动
                RetryData fakeData = new RetryData(SingleWorker.FAKE_PUSH_JOB_ID, delay, 0, JobInfo.NETWORK_TYPE_ANY);
                getInstance().scheduleJobLocked(fakeData);
            }
        }
        return true;
    }
    
    /**
     * 设置重新注册push
     * 
     * @param delay 延迟多久触发
     * @return
     */
    public static final boolean scheduleTipJob(boolean cancel, long delay) {
        if(cancel) {
            getInstance().cancelJob(SingleWorker.TIP_JOB_ID);
        } else {
            RetryData data = new RetryData(SingleWorker.TIP_JOB_ID, delay, delay, JobInfo.NETWORK_TYPE_NONE);
            getInstance().scheduleJobLocked(data);
        }
        return true;
    }

    /**
     * 清除所有的jobs
     *
     * @return
     */
    public static final boolean cancelAllJobs() {
        getInstance().cancelAllJob();
        return true;
    }
}