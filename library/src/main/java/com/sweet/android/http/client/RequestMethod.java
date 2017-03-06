package com.sweet.android.http.client;

import com.sweet.android.http.utils.SignatureUtils;

/**
 * http请求方法定义
 * @author fengzihua
 *
 */
public final class  RequestMethod {
    
    public static int DEPRECATED_GET_OR_POST = -1;
    public static final int GET = 0;
    public static final int POST = 1;
    public static final int PUT = 2;
    public static final int DELETE = 3;
    public static final int HEAD = 4;
    public static final int OPTIONS = 5;
    public static final int TRACE = 6;
    public static final int PATCH = 7;
    
    /**
     * 根据类型转换成对应类型字符串
     * @param method
     * @return
     */
    public static String getHttpTypeStr(int method) {
        switch(method) {
            case GET:
                return SignatureUtils.GET;
            case POST:
                return SignatureUtils.POST;
            case PUT:
                return  SignatureUtils.PUT;
            case DELETE:
                return  SignatureUtils.DELETE;
        }
        return "GET";
    }
}
