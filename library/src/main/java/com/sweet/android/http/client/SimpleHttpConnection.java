package com.sweet.android.http.client;

import android.text.TextUtils;
import com.sweet.android.http.HttpConstants;
import com.sweet.android.http.exception.MethodRequestException;
import com.sweet.android.http.exception.NetworkRequestException;
import com.sweet.android.http.exception.SignatureException;
import com.sweet.android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 基于HttpHURLConnection的http_client
 * 注：暂不处理代理连接情况，后续修改
 * @author fengzihua
 * 
 * @since 2016.4.19
 *
 */
public class SimpleHttpConnection {
    
    public static final String TAG = "http";
    
    // 公共的头部信息
    private Map<String, String> mRequestHeaders = new HashMap<String, String>();
    
    // https
    private SSLSocketFactory mSslSocketFactory;
    
    public SimpleHttpConnection() {
        this(HttpConstants.USER_AGENT_VALUE);
    }
    
    public SimpleHttpConnection(String ua) {
        addRequestUA(ua);
    }
    
    /**
     * 设置http-ua和ssl
     * @param ua 用于服务器追踪问题
     * @param sslSocketFactory SSL factory to use for HTTPS connections
     */
    public SimpleHttpConnection(String ua, SSLSocketFactory sslSocketFactory) {
        this(ua);
        mSslSocketFactory = sslSocketFactory;
    }
    
    /**
     * 为http请求添加ua
     *
     * @return
     */
    public void addRequestUA(String ua) {
        setRequestHeader(HttpConstants.USER_AGENT_KEY,ua);
    }
    
    /**
     * 是否为Https请求
     * @param protocol 协议类型
     * @return
     */
    private boolean isHttpsRequest(String protocol){
        return TextUtils.equals("https", protocol);
    }
    
    /**
     * 添加依赖证书
     * @return
     */
    private  void ensuerX509TrustManager() {
        if(mSslSocketFactory != null) return;
            try {
//                String type = KeyStore.getDefaultType();
//                Log.d("[HttpClient] ensuerX509TrustManager type: " + type);
//                KeyStore keyStore = KeyStore.getInstance(type);
//                String algorithm = TrustManagerFactory.getDefaultAlgorithm();
//                Log.d("[HttpClient] ensuerX509TrustManager algorithm: " + algorithm);
//                TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
//                Log.d("[HttpClient] ensuerX509TrustManager tmf: " + tmf);
//                tmf.init(keyStore);

                SSLContext sslContext = SSLContext.getInstance("TLS");
//                sslContext.init(null, tmf.getTrustManagers(), null);
                sslContext.init(null, new TrustManager[]{new X509TrustManager(){

                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                     }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }}, null);
                mSslSocketFactory = sslContext.getSocketFactory();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    
    /**
     * 设置必须的http-head
     * @param name
     * @param value
     */
    private void setRequestHeader(String name, String value) {
        mRequestHeaders.put(name, value);
    }

    /**
     * 
     * @param params
     * @return
     * @throws NetworkRequestException
     */
    public Response exec(RequestParam<HashMap<String, String>> params) throws NetworkRequestException, SignatureException, MethodRequestException {
        Response response = null;
        int requestTimes = 1 + params.getRetryTime();
        for(int requestTime = 0; requestTime < requestTimes; requestTime++){
            response = performRequest(params, mRequestHeaders, requestTime == (requestTimes - 1));
            Log.d(TAG, "execRequest requestTime: " + requestTime + ",  requestTimes: " + requestTimes);
            if(response != null){
                break;
            }
        }
        return response;
    }
    
    /**
     * 进行http请求
     * 
     * @param request
     * @param additionalHeaders
     * @param lastTry 是否最后一次重试，如果是: 抛出异常
     * @return
     */
    private Response performRequest(RequestParam<HashMap<String, String>> request, Map<String, String> additionalHeaders, boolean lastTry)
            throws NetworkRequestException, SignatureException, MethodRequestException {
        
        if(request == null) {
            Log.d(TAG, "http request error : params is null....");
            throw new MethodRequestException("params is null..");
        }
        // 不带参数的请求地址
        String url = request.getBaseUrl();
        Log.d(TAG, "performRequest[" + RequestMethod.getHttpTypeStr(request.getMethod()) + "]: " + url);
        //Log.d(TAG, "performRequest:  " + request.getSignatureHeaders().toString() );
        //Log.d(TAG, "performRequest: " + additionalHeaders.toString());
        
        try{
            URL parsedUrl = new URL(url);
            
            // 打开一个httpURL的连接，同时根据url添加证书管理类
            HttpURLConnection connection = openConnection(parsedUrl, request);
            
            // 添加请求头部信息
            setRequestHead(connection, additionalHeaders, request.getSignatureHeaders());
            
            // 设置请求方法，同时写入请求参数
            setParamsAndMethod(connection, request);
            
            // 判断返回结果
            int responseCode = connection.getResponseCode();
            
            Log.d(TAG, "responseCode: " + responseCode);
            
            // 获取请求的数据编码
            final String encode = TextUtils.isEmpty(connection.getContentEncoding()) ? request.getParamsEncoding() : connection.getContentEncoding();
            
            if (responseCode == Response.RESPONSE_CODE_OK) {
                // 返回成功
                InputStream stream = connection.getInputStream();
                return new Response(responseCode, stream, encode);
            } else {
                // 判断是否签名失败或是请求方法错误
                if(responseCode == Response.RESPONSE_CODE_SIGNATURE_ERROR) {
                    // 其它不成功情况
                    String errMsg = responseCode + ",  " + URLDecoder.decode(connection.getResponseMessage(), encode);
                    Log.e(TAG, "performRequest, SignatureException, errorMsg: " + errMsg);
                    // 签名错误，抛出异常
                    throw new SignatureException("signature error", Response.RESPONSE_CODE_SIGNATURE_ERROR);
                } else  if(responseCode == Response.RESPONSE_CODE_METHOD_ERROR) {
                    // 其它不成功情况
                    String errMsg = responseCode + ",  " + URLDecoder.decode(connection.getResponseMessage(), encode);
                    Log.e(TAG, "performRequest,  MethodRequestException, errorMsg: " + errMsg);
                    throw new MethodRequestException("http method error",  Response.RESPONSE_CODE_METHOD_ERROR);
                } else if(lastTry) {
                    // 其它不成功情况
                    String errMsg = responseCode + ",  " + URLDecoder.decode(connection.getResponseMessage(), encode);
                    Log.e(TAG, "performRequest,  errorMsg: " + errMsg);
                    throw new NetworkRequestException(errMsg,  responseCode);
                }
            }
        } catch(NetworkRequestException e){
            Log.e(TAG, "performRequest, NetworkRequestException: " + e.getMessage() + ", class:" + e.getClass());
            throw e;
        }catch(SignatureException e){
            Log.e(TAG, "performRequest, SignatureException: " + e.getMessage() + ", class:" + e.getClass());
            throw e;
        }catch(MethodRequestException e){
            Log.e(TAG, "performRequest, MethodRequestException: ." + e.getMessage() + ", class:" + e.getClass());
            throw e;
        } catch(Exception e){
            Log.e(TAG, "performRequest, Exception: " + e.getMessage() + ", class:" + e.getClass());
            e.printStackTrace();
            throw new NetworkRequestException("http Exception",  111);
        }finally{
        }
        return null;
    }
    
    /**
     * 创建一个http连接
     * Create an {@link HttpURLConnection} for the specified {@code url}.
     */
    protected HttpURLConnection createConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }

    /**
     * 通过参数打开一个连接
     * 
     * Opens an {@link HttpURLConnection} with parameters.
     * @param url
     * @return an open connection
     * @throws IOException
     */
    private HttpURLConnection openConnection(URL url, RequestParam<HashMap<String, String>> request) throws IOException {
        HttpURLConnection connection = createConnection(url);

        connection.setConnectTimeout(request.getConnectTimeoutms());
        connection.setReadTimeout(request.getReadTimeoutms());
        connection.setUseCaches(false);
        connection.setDoInput(true);
        
        // use caller-provided custom SslSocketFactory, if any, for HTTPS
        if(!isHttpsRequest(url.getProtocol())) {
           return connection;
        }
        // 确认证书
        ensuerX509TrustManager();
        Log.d(TAG, "openConnection, mSslSocketFactory=" + mSslSocketFactory);
        if ( mSslSocketFactory != null) {
            // 添加证书管理
            ((HttpsURLConnection)connection).setSSLSocketFactory(mSslSocketFactory);
            //允许所有主机的验证
            //connection.setHostnameVerifier(OkHostnameVerifier);
        }
        return connection;
    }
    
    /**
     * 添加请求头部信息
     * @param conn
     *
     */
    private void setRequestHead(HttpURLConnection conn, Map<String, String> additionalHeaders, Map<String, String> signatureHeaders) {
        //设置请求头 一般没特殊要求， 不需要
        for (String key : additionalHeaders.keySet()) {
            conn.setRequestProperty(key, additionalHeaders.get(key));
            Log.d(TAG, "extraRequestHead, [" + additionalHeaders.get(key) + "]");
        }
        
         // http增加头部信息, 用于签名认证
        if(signatureHeaders != null && signatureHeaders.size() > 0){
            for(Entry<String, String> keyValue : signatureHeaders.entrySet()){
                conn.setRequestProperty(keyValue.getKey(), keyValue.getValue());
                //Log.d(TAG, "setRequestHead2, [" + keyValue.getKey() + ": " + keyValue.getValue() + "]");
            }
        }
    }
    
    /**
     * 根据请求类型设置不同请求方式
     * 
     * @param connection
     * @param request
     * @throws IOException
     */
    private void setParamsAndMethod(HttpURLConnection connection, RequestParam<HashMap<String, String>> request) throws IOException {
        //Log.d(TAG, "setParamsAndMethod: method:  " + RequestMethod.getHttpTypeStr(request.getMethod()));
        switch (request.getMethod()) {
            case RequestMethod.GET:
                // Not necessary to set the request method because connection defaults to GET but
                // being explicit here.
                connection.setRequestMethod("GET");
                break;
            case RequestMethod.DELETE:
                connection.setRequestMethod("DELETE");
                break;
            case RequestMethod.POST:
                connection.setRequestMethod("POST");
                addBodyIfExists(connection, request);
                break;
            case RequestMethod.PUT:
                connection.setRequestMethod("PUT");
                addBodyIfExists(connection, request);
                break;
            case RequestMethod.HEAD:
                connection.setRequestMethod("HEAD");
                break;
            case RequestMethod.OPTIONS:
                connection.setRequestMethod("OPTIONS");
                break;
            case RequestMethod.TRACE:
                connection.setRequestMethod("TRACE");
                break;
            case RequestMethod.PATCH:
                connection.setRequestMethod("PATCH");
                addBodyIfExists(connection, request);
                break;
            default:
                throw new IllegalStateException("Unknown method type." + request.getMethod());
        }
    }
    
    /**
     * 根据请求情况，添加body部分
     * 如post, put, patch
     * @param connection
     * @param request
     * @throws IOException
     */
    private static void addBodyIfExists(HttpURLConnection connection, RequestParam<HashMap<String, String>> request)
            throws IOException {
        byte[] body = request.getBody();
        if (body != null) {
            // 设置是否向connection输出，
            // 如果是post请求，参数要放在http正文内，因此需要设为true
            connection.setDoOutput(true);
            // Post 请求不能使用缓存
            connection.setUseCaches(false);
            // 设置输入参数格式
            connection.addRequestProperty(HttpConstants.HEADER_CONTENT_TYPE_KEY, request.getBodyContentType());
            // 输入参数数据流
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            // 写入http缓存参数
            out.write(body);
            // 清空缓存，进行真正发送
            out.flush();
            // 关闭输出流
            out.close();
        }
    }
    
    /**
     * 添加依赖证书
     * @param conn
     * @return
     */
    public  void addX509TrustManager(HttpsURLConnection conn) {
            try {
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                String algorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
                tmf.init(keyStore);
                
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, tmf.getTrustManagers(), null);
                
                conn.setSSLSocketFactory(sslContext.getSocketFactory());
                //允许所有主机的验证
                //conn.setHostnameVerifier(OkHostnameVerifier);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}