
package com.sweet.android.http.utils;

import org.json.JSONException;
import org.json.JSONObject;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 签名工个类
 * wiki: http://wiki.letv.cn/pages/viewpage.action?pageId=37323874
 *
 * @author fengzihua
 * @since 2015.2.3
 *
 */
public class SignatureUtils {
    
    private final static String TAG = "sign";
    
    /*AccessKey： API使用者向API提供方申请的Access Key（或AppId）, 用于标识API使用者的身份。
    SecretKey：由API服务提供方分配，API使用方自己保存，用来做签名时的密钥。
    Signature：根据Key和用户请求计算出的数字签名，用于验证用户身份。*/
    // 签名参数

    // 接口加密码的ak和sk
    public static String APPKEY = "43fb2793322c7890b91a4b3e2f21a0ef";
    public static String APPSEC = "9afc4b726c95541c59b4cc53b0f045ea";

    // HTTP请求方法，如GET、POST、PUT、DELETE，需大写。
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    
    // http请示时，头部信息,作签名验证
    public static final String AUTHORIZATION_KEY = "Authorization";
    public static final String DATE_KEY = "Date";
    public static final String CONTENT_TYPE_KEY = "Content-Type";
    public static final String CONTENT_JSON_VALUE = "application/json";
    // 签名字符串编码
    public static final String CHARSET_NAME = "UTF-8";
    
    /**
     * 填充对应数据
     * @param ak
     * @param sk
     */
    public static void fillInterfaceKey(String ak, String sk) {
        APPKEY = ak;
        APPSEC = sk;
    }

    /**
     *  计算Signature, 只有body部分
     *  
     * POST /api/v1/message HTTP/1.1
     * Host: push.scloud.letv.com
     * Authorization: LETV appid_b515357337f7415ab9275df7a3f92d94 3b635f825d3c34eb6497b636e35e81777ef3c659
     * Date: Tue, 25 Nov 2014 14:00:52 CST
     *  
     *  @return 返回http请求是头部要增加的信息
     */
    public static Map<String, String> getSignature(String method, String path, long time, String jsonBody) {
        Map<String, String> header = new HashMap<String, String>();
        String date = LeSignature.formatDate(time);
        header.put(CONTENT_TYPE_KEY, CONTENT_JSON_VALUE);
        header.put(AUTHORIZATION_KEY, String.format(" LETV %s %s", APPKEY, LeSignature.getSignature(APPSEC, method, path, encodeByte(jsonBody), date, null)));
        header.put(DATE_KEY, date);
        return header;
    }
    
    /**
     *  计算Signature
     *  
     * POST /api/v1/message HTTP/1.1
     * Host: push.scloud.letv.com
     * Authorization: LETV appid_b515357337f7415ab9275df7a3f92d94 3b635f825d3c34eb6497b636e35e81777ef3c659
     * Date: Tue, 25 Nov 2014 14:00:52 CST
     *  
     *  @return 返回http请求是头部要增加的信息
     */
    public static Map<String, String> getSignature(String method, String path, long time, Map<String, String> params, boolean isJson) {
        Map<String, String> header = new HashMap<String, String>();
        String date = LeSignature.formatDate(time);
        
        /*Log.d(TAG, "isJson: " + isJson);
        Log.d(TAG, "APPKEY: " + APPKEY);
        Log.d(TAG, "APPSEC: " + APPSEC);
        Log.d(TAG, "method: " + method);
        Log.d(TAG, "path: " + path);
        Log.d(TAG, "date: " + date);
        for(String key: params.keySet()) {
            Log.d(TAG, key + ": " + params.get(key));
        }*/
        if(isJson) {
            header.put(CONTENT_TYPE_KEY, CONTENT_JSON_VALUE);
            header.put(AUTHORIZATION_KEY, String.format(" LETV %s %s", APPKEY, LeSignature.getSignature(APPSEC, method, path, encodeByte(params), date, null)));
        } else {
            String ss = LeSignature.getSignature(APPSEC, method, path, null, date, params);
            //Log.d(TAG, "ss: " + ss);
            String author_v = String.format(" LETV %s %s", APPKEY, ss);
            //Log.d(TAG, "author: " + author_v);
            
            header.put(AUTHORIZATION_KEY, author_v);
            //header.put(AUTHORIZATION_KEY, String.format(" LETV %s %s", APPKEY, LeSignature.getSignature(APPSEC, method, path, null, date, params)));
        }
        header.put(DATE_KEY, date);
        return header;
    }
    

    /**
     * 根据map生成json字符串
     * @param jsonBody
     * @return
     */
    public static byte[] encodeByte(String jsonBody) {
        return jsonBody.getBytes(Charset.forName(CHARSET_NAME));
    }

    /**
     * 根据map生成json字符串
     * @param params
     * @return
     */
    public static byte[] encodeByte(Map<String, String> params) {
        return encodeJson(params).toString().getBytes();
    }
    
    public static StringBuilder encodeJson(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        if (params == null) {
            return sb;
        }
        
        try {
            JSONObject object = new JSONObject();
            for (String key : params.keySet()) {
                object.put(key, params.get(key));
            }
            sb.append(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sb;
    }
}