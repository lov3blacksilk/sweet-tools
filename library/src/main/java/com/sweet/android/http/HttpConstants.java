package com.sweet.android.http;

/**
 * http请求想关参数
 * @author fengzihua
 * @since 2016.4.19
 */
public class HttpConstants {
    
    public static final String UTF_8_CODE = "UTF-8";
    
    // UA KEY
    public static final String USER_AGENT_KEY = "User-Agent";
    // UA 默认值
    public static final String USER_AGENT_VALUE = "LETV";
    // 参数格式key
    public static final String HEADER_CONTENT_TYPE_KEY = "Content-Type";
    
    // 计算ua相状参数
    public static final String COOKIE_AUTHORIZATION = "Authorization";
    public static final String TERMINAL_NAME = "Phone";
    public static final String BACK_SLASH = "/";
    public static final String SEMICOLON = ";";
    public static final String ONE_SPACE = " ";
    public static final String TERMINAL_CUSTOM_INFO = "This is personalized";
    public static final String LEFT_PARENTTHESIS = "(";
    public static final String RIGHT_PARENTTHESIS = ")";

    /**
     * post中参数的编码
     * Default encoding for POST or PUT parameters. See {@link #getParamsEncoding()}.
     */
    public static final String DEFAULT_PARAMS_ENCODING = UTF_8_CODE;
}