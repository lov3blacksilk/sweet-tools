package com.sweet.android.util;

/**
 *  用于配置相关内容
 *  1. log的tag, 开关
 *  2. log的离线文件路径和名字
 *  3. fc文件的路径和名字
 */
public class Configs {
    // 代码控制处
    public static final boolean DEBUG = true;
    
    /**
     * 初始化基本设置
     * @param tag
     * @param pkg
     * @param offlineFile
     * @param openLog
     * @param log2file
     */
    public static final void initConfigs(String tag, String pkg, String offlineFile, boolean openLog, boolean log2file) {
        Log.DEBUG = openLog && DEBUG;
        Log.GLOBAL_TAG = tag;
        Log.WRITE_LOG_FILE = log2file && DEBUG;
        Constants.PKG = pkg;
        Constants.OFFLINE_FILE_NAME = offlineFile;
    }
    
    /**
     * 获取基本配置信息
     * @return
     */
    public static final String configsToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Log: "); sb.append(Log.DEBUG); sb.append(", ");
        sb.append("offline: "); sb.append(Log.WRITE_LOG_FILE); sb.append(", ");
        sb.append("tag: "); sb.append(Log.GLOBAL_TAG); sb.append(",");
        sb.append("pkg: "); sb.append(Constants.PKG); sb.append(",");
        sb.append("file: "); sb.append(Constants.OFFLINE_FILE_NAME);
        return sb.toString();
    }
}