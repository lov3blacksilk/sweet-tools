
package com.sweet.android.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.IllegalFormatException;
import java.util.Locale;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

/**
 * 日志文件，旨在实现一个编译一的，容易处理的日志
 * @author dingding
 *
 */
public class Log {
    public static String GLOBAL_TAG = "personalized";
    
    // 对各个级别的log进行控制
    public static final boolean LOG_V = true;
    public static final boolean LOG_D = true;
    public static final boolean LOG_I = true;
    public static final boolean LOG_W = true;
    public static final boolean LOG_E = true;
    public static final boolean LOG_WTF = true;
    // 是否打印log
    public static  boolean DEBUG = true;
    // 把log写入文件
    public static boolean WRITE_LOG_FILE = true;

    // 字符串格式format
    private static final String LOG_FORMAT = "%1$s\n%2$s";
    // log文件地址
    private static String LOG_FILE_PATH = Constants.LOG_PATH;
    // log文件大小上限，超过后，会先删除此文件，再创建
    private static final long   LOG_FILE_MAX_SIZE = 10L * 1024 * 1024;
    // 构建目录的context
    private static Context mGlobalContext = null;

    public static boolean isLogEnable(){
        return DEBUG;
    }

    public static boolean isWriteLogToFileEnable(){
        return WRITE_LOG_FILE;
    }

    /**
     * 初始化context
     * @param context
     */

    public static void init(Context context) {
        if (mGlobalContext == null) {
            if (context != null) {
                if (context instanceof Application) {
                    mGlobalContext = context;
                } else {
                    Context appContext = context.getApplicationContext();
                    if (appContext != null) {
                        mGlobalContext = appContext;
                    }
                }
            }
        }
    }

    /**
     * 创建缓存目录
     * @return
     */
    private boolean creatCacheDir(){
        if (mGlobalContext != null) {
            File file = mGlobalContext.getExternalCacheDir();
            if (file != null) {
                return true;
            }
        }
        return false;
    }

    /* i level log */
    public static void i(String tag, String msg) {
        if (LOG_I) {
            msg= "[" + tag + "]   " + msg;
            i(msg);
        }
    }

    public static void i(String message, Object... args) {
        log(android.util.Log.INFO, null, message, args);
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (LOG_I) {
            msg= "[" + tag + "]   " + msg;
            i(tr, msg);
        }
    }

    public static void i(Throwable ex, String message, Object... args) {
        log(android.util.Log.INFO, ex, message, args);
    }

    public static void i(Object objectPrefix, String format, Object... args) {
        if (LOG_I) {
            i(getPrefixFromObject(objectPrefix), buildMessage(getPrefixFromObject(objectPrefix), format, args));
        }
    }

    /* v level log */
    public static void v(String tag, String msg) {
        if(LOG_V) {
            msg= "[" + tag + "]   " + msg;
            v(msg);
        }
    }

    public static void v(String message, Object... args) {
        log(android.util.Log.VERBOSE, null, message, args);
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (LOG_V) {
            msg= "[" + tag + "]   " + msg;
            v(tr, msg);
        }
    }

    public static void v(Throwable ex, String message, Object... args) {
        log(android.util.Log.VERBOSE, ex, message, args);
    }

    public static void v(Object objectPrefix, String format, Object... args) {
        if (LOG_V) {
            v(getPrefixFromObject(objectPrefix), buildMessage(getPrefixFromObject(objectPrefix), format, args));
        }
    }

    /* d level log */
    public static void d(String tag, String msg) {
        if (LOG_D) {
            msg= "[" + tag + "]   " + msg;
            d(msg);
        }
    }

    public static void d(String message, Object... args) {
        log(android.util.Log.DEBUG, null, message, args);
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (LOG_D) {
            msg= "[" + tag + "]   " + msg;
            d(tr, msg);
        }
    }

    public static void d(Throwable ex, String message, Object... args) {
        log(android.util.Log.DEBUG, ex, message, args);
    }

    public static void d(Object objectPrefix, String format, Object... args) {
        if (LOG_D) {
            d(getPrefixFromObject(objectPrefix), buildMessage(getPrefixFromObject(objectPrefix), format, args));
        }
    }

    /* e level log */
    public static void e(String tag, String msg) {
        if(LOG_E) {
            msg= "[" + tag + "]   " + msg;
            e(msg);
        }
    }

    public static void e(String message, Object... args) {
        log(android.util.Log.ERROR, null, message, args);
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (LOG_E) {
            msg= "[" + tag + "]   " + msg;
            e(tr, msg);
        }
    }

    public static void e(Throwable ex, String message, Object... args) {
        log(android.util.Log.ERROR, ex, message, args);
    }

    public static void e(Object objectPrefix, String format, Object... args) {
        if (LOG_E) {
            e(getPrefixFromObject(objectPrefix), buildMessage(getPrefixFromObject(objectPrefix), format, args));
        }
    }

    /* w level log */
    public static void w(String tag, String msg) {
        if(LOG_W) {
            msg= "[" + tag + "]   " + msg;
            w(msg);
        }
    }

    public static void w(String message, Object... args) {
        log(android.util.Log.WARN, null, message, args);
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (LOG_W) {
            msg= "[" + tag + "]   " + msg;
            w(tr, msg);
        }
    }
    
    public static void w(Throwable ex, String message, Object... args) {
        log(android.util.Log.WARN, ex, message, args);
    }

    public static void w(Object objectPrefix, String format, Object... args) {
        if (LOG_W) {
            w(getPrefixFromObject(objectPrefix), buildMessage(getPrefixFromObject(objectPrefix), format, args));
        }
    }

    /* wtf level log */
    public static void wtf(String tag, String msg) {
        if(LOG_WTF) {
            msg= "[" + tag + "]   " + msg;
            wtf(msg);
        }
    }

    public static void wtf(String message, Object... args) {
        log(android.util.Log.WARN, null, message, args);
    }

    public static void wtf(String tag, String msg, Throwable tr) {
        if (LOG_WTF) {
            msg= "[" + tag + "]   " + msg;
            wtf(tr, msg);
        }
    }

    public static void wtf(Throwable ex, String message, Object... args) {
        log(android.util.Log.WARN, ex, message, args);
    }

    public static void wtf(Object objectPrefix, String format, Object... args) {
        if (LOG_WTF) {
            wtf(getPrefixFromObject(objectPrefix), buildMessage(getPrefixFromObject(objectPrefix), format, args));
        }
    }

    private static void log(int priority, Throwable ex, String message, Object... args) {
        if (!isLogEnable() && !isWriteLogToFileEnable() ) return;
        String log;
        if (args.length > 0) {
            message = String.format(message, args);
        }
        if (ex == null) {
            log = message;
        } else {
            String logMessage = message == null ? ex.getMessage() : message;
            String logBody = android.util.Log.getStackTraceString(ex);
            log = String.format(LOG_FORMAT, logMessage, logBody);
        }
        if(isLogEnable()) {
            android.util.Log.println(priority, GLOBAL_TAG, log);
        }

        // 初始化日志路径
        if (!LOG_FILE_PATH.equals(Constants.LOG_PATH)) {
            LOG_FILE_PATH = Constants.LOG_PATH;
        }

        if(isWriteLogToFileEnable()) {
            writeToFile(log);
        };
    }
    
    /**
     * 生成格式文件
     * @param logString
     */
    private static void writeToFile(String logString) {
        StringBuilder builder = new StringBuilder();
        //builder.append("----------------------------------------\n");
        builder.append("[" + Tools.formatTimeCN(System.currentTimeMillis()) + "] ");builder.append(logString);builder.append("\n");
        // 打印线程处理
        Message.obtain(getInstance().mWorkHandler, 1, builder.toString()).sendToTarget();
    }
    
    private static Log mLog = new Log();
    // 后台处理的工作线程
    private WorkHandler mWorkHandler;
    
    /**
     * 单例对像
     * @param context
     */
    public static Log getInstance() {
        return mLog;
    }
    
    private Log() {
        if(isWriteLogToFileEnable()) {
            initWorkHandler();
        }
        //LogHelper.d("[LogHelper] init...");
    }
    
    /**
     * 初始化后打印log线程
     */
    private void initWorkHandler() {
        HandlerThread thread = new HandlerThread(Log.GLOBAL_TAG, Process.THREAD_PRIORITY_BACKGROUND);
        // log线程设置为守护线程
        thread.setDaemon(true);
        thread.start();
        mWorkHandler = new WorkHandler(thread.getLooper());
    }
    
    /**
     * 子线程处理log文件打印
     */
    private final class WorkHandler extends Handler {
        public WorkHandler(Looper looper) {
            super(looper);
        }
        
        @Override
        public void handleMessage(Message msg) {
            writeLogToFile((String)msg.obj, LOG_FILE_PATH, LOG_FILE_MAX_SIZE);
        }
    }

    /**
     * 把当前log添加到文件中
     * @param logString
     * @param filePath
     * @param maxSize
     */
    private void writeLogToFile(String logString, String filePath, long maxSize){
        FileOutputStream out = null;
        OutputStreamWriter writer = null;

        try {
            File file = ensureFile(filePath, maxSize);
            if(file == null) return;

            out = new FileOutputStream(file, true);
            writer = new OutputStreamWriter(out);
            writer.write(logString);
            // 释放资源
            writer.flush();
            out.flush();
        } catch(Exception e){
            Log.e(GLOBAL_TAG, "writeLogToFile, write log exception: " + e.getMessage() + ", class:" + e.getClass());
        } finally {
            try {
                if (null != writer) {
                    writer.close();
                    writer = null;
                }
            } catch (Exception e) {
                Log.e(GLOBAL_TAG, "writeLogToFile, OutputStreamWriter close exception: " + e.getMessage() + ", class:" + e.getClass());
            }

            try {
                if (null != out) {
                    out.close();
                    out = null;
                }
            } catch (Exception e) {
                Log.e(GLOBAL_TAG, "writeLogToFile, FileOutputStream close exception: " + e.getMessage() + ", class:" + e.getClass());
            }
        }
    }

    /**
     * 1. 确认文件是否存在.
     * 2. 确认文件大小
     * @param filePath
     * @param maxSize
     * @return
     * @throws IOException
     */
    private File ensureFile(String filePath, long maxSize) throws IOException {
        File file = new File(filePath);
        File dir = file.getParentFile();
        if(dir != null && !dir.exists()){
            if (!dir.mkdirs()) {
                if (!creatCacheDir()){
                    return null;
                } else if (!dir.exists()){
                    return null;
                }
            }
        }
        if(file.exists() && file.length() >= maxSize){
            file.delete();
        }
        if(!file.exists()){
            if(!file.createNewFile()){
                return null;
            }
        }
        return file;
    }

    private static String getPrefixFromObject(Object obj) {
        return obj == null ? "<null>" : obj.getClass().getSimpleName();
    }

    private static String buildMessage(String prefix, String format, Object... args) {
        String msg;
        try {
            msg = (args == null || args.length == 0) ? format
                    : String.format(Locale.US, format, args);
        } catch (IllegalFormatException ife) {
            e("Log", ife, "IllegalFormatException: formatString='%s' numArgs=%d", format,
                    args.length);
            msg = format + " (An error occurred while formatting the message.)";
        }
        return String.format(Locale.US, "%s: %s", prefix, msg);
    }
}