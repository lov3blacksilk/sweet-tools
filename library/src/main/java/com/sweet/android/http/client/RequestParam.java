package com.sweet.android.http.client;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import android.text.TextUtils;
import com.sweet.android.http.HttpConstants;
import com.sweet.android.http.utils.SignatureUtils;

/**
 * 请求参数组装类
 * 
 * 注: 按api加密码规活范2实现
 * http://wiki.letv.cn/pages/viewpage.action?pageId=37323874
 * 
 * @author fengzihua
 * @since 2016.4.19
 */
public class RequestParam<P extends Map<String, String>> {
    // 连接服务器时间
    private static final int CONNECT_HTTP_TIME_OUT = 10 * 1000;
    // 从服务器读取数据时间
    private static final int READ_HTTP_TIME_OUT          =   6 * 1000;
    
    // 默认的重试次数,即为0时为访问一次(注意这里并非尝试次数)
    public static final int RETRY_COUNT = 0;//1;
    
    /**
     * baseUrl请求地址, 不带参数
     */
    private String baseUrl;
    
    /**
     * 是否以json格式传送参数
     */
    public boolean isJson;
    
    /**
     * 参数签名验证时头部信息
     */
    public Map<String, String> headers;
    
    /**
     * 参数
     * */
    public P params ;
    
    /**
     * jsonBody
     * 参数
     */
    public String jsonBody;
    
    /**
     * HttpMethod：HTTP请求方法，如GET、POST、PUT、DELETE，需大写。
     */
    public int mMethod;
    
    /**
     * application/x-javascript text/xml->xml数据
     * application/x-javascript->json对象
     * application/x-www-form-urlencoded->表单数据
     */
    private String mContentType;
    
    // 连接服务器时间
    private  int mConnectTime = CONNECT_HTTP_TIME_OUT;
    
    // 从服务器读取数据时间
    private  int mReadTime = READ_HTTP_TIME_OUT;
    
    // 重试次数
    private int mReTryTime = RETRY_COUNT;

    public RequestParam(String baseUrl, P params, int httpType,  String path) {
        this(baseUrl, params, httpType, path, false);
    }
    
    public RequestParam(String baseUrl, String jsonBody, int method,  String path) {
        this.mMethod = method;
        this.jsonBody = jsonBody;
        this.isJson = true;
        this.mReTryTime = RETRY_COUNT;
        formatUrl(baseUrl, method);
        if(this.isJson) {
            mContentType = "application/x-javascript->json";
        } else {
            mContentType = "application/x-www-form-urlencoded";
        }
        
        // 签名字符串, 添加到http头部
        headers = SignatureUtils.getSignature(RequestMethod.getHttpTypeStr(method), path , System.currentTimeMillis(), jsonBody);
    }
    
    public RequestParam(String baseUrl, P params, int method,  String path, boolean isJson) {
        this.mMethod = method;
        this.params = params;
        this.isJson = isJson;
        this.mReTryTime = RETRY_COUNT;
        formatUrl(baseUrl, method);
        if(this.isJson) {
            mContentType = "application/x-javascript->json";
        } else {
            mContentType = "application/x-www-form-urlencoded";
        }
        
        // 签名字符串, 添加到http头部
        headers = SignatureUtils.getSignature(RequestMethod.getHttpTypeStr(method), path , System.currentTimeMillis(), params, isJson);
    }
    
    /**
     * 根据请求方式，修改url
     * 如果是get或是delete则生成新的url
     * 
     * @param baseUrl
     * @param method
     */
    private void formatUrl(String baseUrl, int method) {
         if (params == null || params.size() == 0) {
                this.baseUrl = baseUrl;
                return;
         }
         
        switch (method) {
        case RequestMethod.GET:
        case RequestMethod.DELETE:
            StringBuilder sb = new StringBuilder(baseUrl);
            boolean first = true;
            for (String key : params.keySet()) {
                if (first) {
                    if(method == RequestMethod.GET || method == RequestMethod.DELETE){
                        sb.append("?");
                    }
                    first = false;
                } else {
                    sb.append("&");
                }
                String value = params.get(key);
                if(value != null){
                    try {
                        sb.append(key + "=" + URLEncoder.encode(value, "UTF-8"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    sb.append(key + "=");
                }
            }
            this.baseUrl = sb.toString();
            break;
        default:
            this.baseUrl = baseUrl;
            break;
        }
    }
    
    /**
     * Returns the URL of this request.
     */
    public String getBaseUrl() {
        return baseUrl;
    }
    
    /**
     * 连接超时
     */
    public final int getConnectTimeoutms() {
        return mConnectTime;
    }
    
    /**
     * 读取超时
     */
    public final int getReadTimeoutms() {
        return mReadTime;
    }
    
    /**
     * 设置重试次数
     * @param retryTime
     */
   public final void setRetryTime(int retryTime) {
       mReTryTime = retryTime;
   }
   
   /**
    * 获取总的retry次数
    * @return
    */
   public final int getRetryTime() {
       return mReTryTime;
   }
    
    /**
     * Return the method for this request.  Can be one of the values in {@link Method}.
     */
    public int getMethod() {
        return mMethod;
    }
    
    /**
     * 返回post和put参数编码方式
     * Returns which encoding should be used when converting POST or PUT parameters returned by
     * {@link #getParams()} into a raw POST or PUT body.
     *
     * <p>This controls both encodings:
     * <ol>
     *     <li>The string encoding used when converting parameter names and values into bytes prior
     *         to URL encoding them.</li>
     *     <li>The string encoding used when converting the URL encoded parameters into a raw
     *         byte array.</li>
     * </ol>
     */
    protected String getParamsEncoding() {
        return HttpConstants.DEFAULT_PARAMS_ENCODING;
    }
    
    /**
     * 内容格式
     * @return
     */
    public String getBodyContentType() {
        return String.format("%s;charset=%s", mContentType,  getParamsEncoding());
    }
    
    /**
     * 参数
     * @return
     */
    protected Map<String, String> getSignatureHeaders() {
        return headers;
    }

    
    /**
     * 参数
     * @return
     */
    protected P getParams() {
        return params;
    }
    /**
     * Returns the raw POST or PUT body to be sent.
     *
     * @throws AuthFailureError in the event of auth failure
     */
    public byte[] getBody() {
        Map<String, String> params = getParams();
        if(isJson) {
            // json格式请求
            return encodeJsonParameters(params, getParamsEncoding());
        }
        if (params == null || params.size() == 0) return null;
        
        // 健值对形式请求
        return encodeParameters(params, getParamsEncoding());
    }
    
    /**
     * 把参数转化成指定格式的数据
     * Converts <code>params</code> into an application/x-www-form-urlencoded encoded string.
     */
    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }
    
    /**
     * 把参数转化成json数据格式
     * Converts <code>params</code> into an application/x-www-form-urlencoded encoded string.
     */
    private byte[] encodeJsonParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            if(TextUtils.isEmpty(jsonBody)) {
                encodedParams.append(SignatureUtils.encodeJson(params));
            } else {
                encodedParams.append(jsonBody);
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    /**
     * 如果是post或是put通过使用urlconnection时传入参数形式 
     * @return
     */
    public StringBuilder encodeParams() {
        StringBuilder sb = new StringBuilder();
        if (params == null || params.size() == 0) {
            return sb ;
        }
        boolean first = true;
        for (String key : params.keySet()) {
            if (first) {
                if(mMethod == RequestMethod.GET || mMethod == RequestMethod.DELETE){
                    sb.append("?");
                }
                first = false;
            } else {
                sb.append("&");
            }
            String value = params.get(key);
            if(value != null){
                try {
                    sb.append(key + "=" + URLEncoder.encode(value, "UTF-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                sb.append(key + "=");
            }
        }
        return sb;
    }
    
    /**
     * 
     * @deprecated
     * 生成json参数
     * 先判断是否有body
     * @param params
     * @return
     */
    public  StringBuilder encodeJson() {
        if(TextUtils.isEmpty(jsonBody)) {
            return SignatureUtils.encodeJson(params);
        } else {
            return new StringBuilder(jsonBody);
        }
    }
    
    /**
     * 把post参数转成json字符串
     * 
     * @param postParameters
     * @return
     */
    public  final String toJsonStr() {
        if(params == null || params.size() == 0) return "";
        StringBuilder sb = new StringBuilder();
        
        // url和请求方式
        sb.append(baseUrl);
        sb.append(" ");
        sb.append(RequestMethod.getHttpTypeStr(mMethod));
        
        //  打印参数信息
        boolean first = true;
        sb.append(" [PARAM] ");
        for (String key : params.keySet()) {
            if(first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append(key + ":" + params.get(key));
        }
        
        first = true;
        //  打印头部信息
        if(headers != null && headers.size() > 0) {
            sb.append(" [HEAD] ");
            for (String key : headers.keySet()) {
                if(first) {
                    first = false;
                } else {
                    sb.append(",");
                }
                sb.append(key + ":" + headers.get(key));
            }
        }
        return sb.toString();
    }
}