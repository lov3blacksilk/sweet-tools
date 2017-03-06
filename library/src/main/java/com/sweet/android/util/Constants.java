package com.sweet.android.util;

import android.content.Context;
import android.os.Environment;
import android.text.format.DateUtils;

import java.io.File;

/**
 * Created by dingding on 04/12/15.
 */
public class Constants {
    // 包名
    public static String PKG = "com.sweet.android.util";
    // 离线日志文件名
    public static String OFFLINE_FILE_NAME = "simple";
    // crash日志文件名
    public static String CRASH_FILE_NAME = "crash";

    /**
     * 离线log的存储路径
     */
    public static String LOG_PATH = "";

    // 应用日志路径
    public static String CACHE_DIR = "";

    /**
     * crash文件的存储根路径
     */
    public static String CRASH_ROOT_PATH = "";

    /**
     * 离线log和crash文件的存储根路径
     */
    private static final String FILE_ROOT_PATH =
            Environment.getExternalStorageDirectory() + File.separator
            + "Android" + File.separator
            + "data" + File.separator
            + PKG;

    /**
     * 初始化cache目录
     */
    public static void initCacheDir(Context context) {
        File file = context.getExternalCacheDir();
        if (file != null){
            CACHE_DIR = file.getAbsolutePath();
        } else {
            CACHE_DIR = FILE_ROOT_PATH + File.separator + "cache";
            File cacheDir = new File(CACHE_DIR);
            if (!cacheDir.exists()){
                boolean res = cacheDir.mkdir();
                Log.d("initCacheDir", "result: " + res);
            }
        }
        LOG_PATH = CACHE_DIR + File.separator + OFFLINE_FILE_NAME + ".log";
        CRASH_ROOT_PATH = CACHE_DIR + File.separator + CRASH_FILE_NAME + File.separator;
    }

    /**
     * 最多存放2天，10个crash文件
     */
    public static final long CRASH_MIN_KEEP_AGE = DateUtils.DAY_IN_MILLIS * 2;
    public static final int CRASH_MIN_KEEP_COUNT = 10;
}