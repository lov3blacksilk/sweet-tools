package com.sweet.android.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.FileOutputStream;
import java.io.File;
import java.io.StringWriter;
import android.os.Environment;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Crash捕捉工具类
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashH";

    //crash文件存储根路径
    private static String CRASH_ROOT_PATH = Constants.CRASH_ROOT_PATH;

    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    // CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();

    // 程序的Context对象
    private Context mContext;

    /** 保证只有一个CrashHandler实例 */
    private CrashHandler() {
    }

    /** 获取CrashHandler实例 ,单例模式 */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // 打印异常
        handleException(ex);

        if (mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // 退出程序
            Tools.killApp();
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        // 初始化日志路径
        if (!CRASH_ROOT_PATH.equals(Constants.CRASH_ROOT_PATH)) {
            CRASH_ROOT_PATH = Constants.CRASH_ROOT_PATH;
        }

        // 保存日志文件
        String fileName = savaCrashInfoToSD(mContext, ex);
        Log.d(TAG, "crash file name: " + fileName);
        return false;
    }

    /**
     * 保存获取的 软件信息，设备信息和出错信息保存在SDcard中
     * @param context
     * @param ex
     * @return 返回文件名称,便于将文件传送到服务器
     */
    private String savaCrashInfoToSD(Context context, Throwable ex){
        // 删除一周前的旧文件
        handleDeleteOldeFiles(CRASH_ROOT_PATH);

        String fileName = null;
        StringBuffer sb = new StringBuffer();

        for (Map.Entry<String, String> entry : collectDeviceInfo(context).entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append(" = ").append(value).append("\n");
        }

        sb.append(obtainExceptionInfo(ex));
        FileOutputStream fos = null;
        File dir = null;

        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            dir = new File(CRASH_ROOT_PATH);
            if(! dir.exists()){
                dir.mkdir();
            }

            try{
                fileName = dir.toString() + File.separator + paserTime(System.currentTimeMillis()) + ".cr";
                fos = new FileOutputStream(fileName);
                fos.write(sb.toString().getBytes());
                // 释放资源
                fos.flush();
            }catch(Exception e){
                Log.e(TAG, "savaCrashInfoToSD, write file exception: " + e.getMessage() + ", class:" + e.getClass());
            }  finally {
                try {
                    if (null != fos) {
                        fos.close();
                        fos = null;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "savaCrashInfoToSD, file output stream close exception: " + e.getMessage() + ", class:" + e.getClass());
                }
            }
        }

        return fileName;
    }

    /**
     * 获取系统未捕捉的错误信息
     * @param throwable
     * @return
     */
    private String obtainExceptionInfo(Throwable throwable) {
        Writer mStringWriter = null;
        PrintWriter mPrintWriter = null;
        String exception = null;
        try {
            mStringWriter = new StringWriter();
            mPrintWriter = new PrintWriter(mStringWriter);
            throwable.printStackTrace(mPrintWriter);
            Throwable cause = throwable.getCause();
            while (cause != null) {
                cause.printStackTrace(mPrintWriter);
                cause = cause.getCause();
            }
            exception = mStringWriter.toString();
            Log.e(TAG, exception);
            // 释放资源
            mPrintWriter.flush();
            mStringWriter.flush();
        } catch (Exception e) {
            Log.e(TAG, "obtainExceptionInfo, writer exception: " + e.getMessage() + ", class:" + e.getClass());
        } finally {
            try {
                if (mPrintWriter != null) {
                    mPrintWriter.close();
                    mPrintWriter = null;
                }
            } catch (Exception e) {
                Log.e(TAG, "obtainExceptionInfo, print writer close exception: " + e.getMessage() + ", class:" + e.getClass());
            }

            try {
                if (mStringWriter != null) {
                    mStringWriter.close();
                    mStringWriter = null;
                }
            } catch (Exception e) {
                Log.e(TAG, "obtainExceptionInfo, string writer exception: " + e.getMessage() + ", class:" + e.getClass());
            }
        }
        return exception;
    }

    /**
     * 收集设备参数信息
     * 获取一些简单的信息,软件版本，手机版本，型号等信息存放在HashMap中
     * @param context
     * @return
     */
    private Map<String, String> collectDeviceInfo(Context context){
        Map<String, String> map = new HashMap<String, String>();
        PackageManager mPackageManager = context.getPackageManager();
        PackageInfo mPackageInfo = null;
        try {
            mPackageInfo = mPackageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "an error occured when collect package info", e);
        }

        map.put("versionName", mPackageInfo.versionName);
        map.put("versionCode", "" + mPackageInfo.versionCode);

        map.put("MODEL", "" + Build.MODEL);
        map.put("SDK_INT", "" + Build.VERSION.SDK_INT);
        map.put("PRODUCT", "" + Build.PRODUCT);

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                map.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() +  ":"  + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "collectDeviceInfo, exception: " + e.getMessage() + ", class:" + e.getClass());
            }
        }

        return map;
    }

    /**
     * 将毫秒数转换成yyyy-MM-dd-HH-mm-ss的格式
     * @param milliseconds
     * @return
     */
    private String paserTime(long milliseconds) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String times = format.format(new Date(milliseconds));

        return times;
    }

    /**
     * 删除旧文件
     * @param fileName
     */
    private void handleDeleteOldeFiles(final String fileName) {
        new Thread() {
            public void run() {
                File crashDir = null;
                try {
                    crashDir = new File(fileName);
                    if(! crashDir.exists()){
                        return;
                    }
                    // 最多存放2天，10个crash文件
                    FileUtils.deleteOlderFiles(crashDir, Constants.CRASH_MIN_KEEP_COUNT, Constants.CRASH_MIN_KEEP_AGE);
                } catch(Exception e) {
                    Log.e(TAG, "handleDeleteOldeFiles, exception: " + e.getMessage() + ", class:" + e.getClass());
                }
            }
        }.start();
    }
}
