package com.sweet.android.http.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import com.sweet.android.util.Log;

/**
 * 根据云平台生成user-agent, 用于问题跟踪
 * @author fengzihua
 * @see http://jira.letv.cn/browse/LEUI-7730?filter=-1
 */
public class LeuiUserAgent {
    
    private static final String TAG  = "UA";
    
    // 相关key值
    public static final String TERMINAL_NAME = "Phone";
    public static final String BACK_SLASH = "/";
    public static final String SEMICOLON = ";";
    public static final String ONE_SPACE = " ";
    public static final String TERMINAL_CUSTOM_INFO = "active.v1";
    public static final String LEFT_PARENTTHESIS = "(";
    public static final String RIGHT_PARENTTHESIS = ")";

    // key值进行备份
    public static String USER_AGENT_VALUE = "";

    /**
     * 只计算一次
     * Build user-agent for http request as the format <code><Product>;<Build_ID>;<package>[;interface_version]</code><br/>
     * See <code>http://wiki.letv.cn/pages/viewpage.action?pageId=46183570</code>
     * @param context
     * @return
     */
    public static String ensureUserAgent(Context context) {
        if (TextUtils.isEmpty(USER_AGENT_VALUE)) {
            String defaultPackageName = "android";

            StringBuilder sb = new StringBuilder();
            sb.append(TERMINAL_NAME)
                .append(BACK_SLASH)
                .append(Build.DEVICE)
                .append(ONE_SPACE)
                .append(LEFT_PARENTTHESIS)
                .append(Build.ID)
                .append(SEMICOLON)
                .append(ONE_SPACE);

            if (context == null) {
                sb.append(defaultPackageName);
            } else {
                PackageInfo info = null;
                try {
                    info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                    sb.append(info.packageName)
                        .append(ONE_SPACE)
                        .append(info.versionName);
                } catch (PackageManager.NameNotFoundException e) {
                    sb.append(defaultPackageName);
                    Log.d(TAG, "ensureUserAgen, error:  " + e.getMessage());
                }
            }
            sb.append(SEMICOLON)
                .append(ONE_SPACE)
                .append(TERMINAL_CUSTOM_INFO)
               .append(RIGHT_PARENTTHESIS);
            
            USER_AGENT_VALUE = sb.toString();
            //Log.d(TAG, "ensureUserAgen: " + USER_AGENT_VALUE);
        }
        return USER_AGENT_VALUE;
    }
}