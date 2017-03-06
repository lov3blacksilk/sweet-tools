package com.sweet.android.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

/**
 * 工具类
 * @author dingding
 *
 */
public final class Tools {
    
    private static final String TAG = "Tool";
    /**
     * 杀死应用
     */
    public static void killApp() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "kill app ...");
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        },1000);
    }
    
    /**
     * 重启手机
     */
    public static void restart(Context context) {
        if(context == null) {
            Log.d(TAG, "restart, failed, context is null, " + Tools.formatTime(System.currentTimeMillis()));
            return;
        }
        Log.d(TAG, "restart...." + Tools.formatTime(System.currentTimeMillis()));
        PowerManager power = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        power.reboot("personalized");
    }
    
    /**
     * 根据条件发送message
     * 
     * @param handler
     * @param what
     */
    public static boolean handleAsyncMessage(Handler handler, int what){
        return handleAsyncMessage(handler,what, "");
    }
    
    /**
     * 根据条件发送message
     * 
     * @param handler
     * @param what
     * @param event
     */
    public static boolean handleAsyncMessage(Handler handler, int what, Object event){
        return handleAsyncMessage(handler, what, event, 0);
    }
    
    /**
     * 发送消息到handler所处线程
     * 
     * @param handler
     * @param what
     * @param event
     * @param delayMillis
     */
    public static boolean handleAsyncMessage(Handler handler, int what, Object event, long delayMillis) {
        if(handler == null) {
            Log.i(TAG, "handleAsyncMessage, uihandler is null, what: " + what);
            return false;
        }
        //Log.d(TAG, "handleAsyncMessage... thread: " + handler.getLooper().getThread().getName() + ", what: " + what + ", event: " + event + ", delayMillis: " + delayMillis);
        if(delayMillis <= 0) {
            Message.obtain(handler, what, event).sendToTarget();
        } else {
            Message msg = Message.obtain(handler, what, event);
            handler.sendMessageDelayed(msg, delayMillis);
        }
        return true;
    }
    
    /**
     * 发送消息到handler所处线程
     * 
     * @param handler
     * @param what
     * @param event
     * @param delayMillis
     */
    public static boolean handleAsyncMessage(Handler handler, int what, int arg1, int arg2, Object event, long delayMillis) {
        if(handler == null) {
            Log.i(TAG, "handleAsyncMessage, uihandler is null, what: " + what);
            return false;
        }
        //Log.d(TAG, "handleAsyncMessage... thread: " + handler.getLooper().getThread().getName() + ", what: " + what + ", event: " + event + ", delayMillis: " + delayMillis);
        if(delayMillis <= 0) {
            Message.obtain(handler, what, arg1, arg2, event).sendToTarget();
        } else {
            Message msg = Message.obtain(handler, what, arg1, arg2, event);
            handler.sendMessageDelayed(msg, delayMillis);
        }
        return true;
    }
    
    /**
     * 进行操作时，防止cpu休眠, cpu进行加锁
     * 
     * @param wakelock
     */
    public static void wakeupCpu(WakeLock wakelock) {
        if(wakelock != null && !wakelock.isHeld()) {
            wakelock.acquire();
        }
    }
    
    /**
     * 进行操作时，防止cpu休眠, cpu进行加锁
     * 
     * @param wakelock
     * @param timeOut 最长时间
     */
    public static void wakeupCpu(WakeLock wakelock, long timeOut) {
        if(wakelock != null && !wakelock.isHeld()) {
            if(timeOut <= 0) {
                wakelock.acquire();
            } else {
                wakelock.acquire(timeOut);
            }
        }
    }
    
    /**
     * 释放锁
     * @param
     */
    public static void realseWakeupCpu(WakeLock wakelock) {
        if(wakelock != null && wakelock.isHeld()) {
            wakelock.release();
        }
    }
    
    /**
     * 格式化时间
     * @param time 
     * @return
     */
    public static String formatTime(long time) {
        Date date = new Date();
        date.setTime(time);
        SimpleDateFormat sdf=new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z" , Locale.getDefault());
        return sdf.format(date);
    }
    
    /**
     * 格式化时间
     * @param time 
     * @return
     */
    public static String formatTimeCN(long time) {
        Date date = new Date();
        date.setTime(time);
        SimpleDateFormat sdf=new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss" , Locale.getDefault());
        return sdf.format(date);
    }
    
    /**
     * 测试用
     * @param max
     * @param
     * @return
     */
    public static int getRandomTime(int max) {
        // 定义随机类
        Random ra=new Random();
        int random=ra.nextInt(max);
        return random;
    }
}