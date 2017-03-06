package com.sweet.android.http.client;

import com.sweet.android.http.HttpConstants;
import com.sweet.android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 服务器返回数据
 * 
 * @author fengzihua
 * @since 2016.4.19
 */
public class Response {
    
    private static final boolean DEBUG = true ;
    
    // 服务器返回OK
    public static final int RESPONSE_CODE_OK                                     = 200;
    // 参数签名不正确
    public static final int RESPONSE_CODE_SIGNATURE_ERROR    = 403;
    // 请求方式不对, 如get, post, put, delete
    public static final int RESPONSE_CODE_METHOD_ERROR         = 405;
    
    // 返回状态码
    private int mStatusCode; 
    private String responseAsString = null;
    // stream是否已消费
    private boolean streamConsumed = false;
    private InputStream is;
    
    /**
     * 创建获取InputStream中数据的结果结构。
     * <br/>注意，这个构造方法会直接从输入流中读取数据并且关闭输入流，后续不能再对输入流进行操作（同时，输入流可能在其他地方被关闭，所以不要在构造方法之外操作它）。
     */
    public Response(int statusCode, InputStream contentStream, String contentEncoding) throws IOException{
        mStatusCode = statusCode;
        is = contentStream;
        // 在这里获取stream的数据，因为该方法之后stream会close掉
        fetchResponse();
    }
    public Response(String content, int responseCode) {
        responseAsString = content;
        mStatusCode = responseCode;
    }
    
    /**
     * 解析返回的数据
     * @throws IOException
     */
    private void fetchResponse() throws IOException{
        BufferedReader br;
        try {
            InputStream stream = asStream();
            if (null == stream) {
                return;
            }
            br = new BufferedReader(new InputStreamReader(stream, HttpConstants.DEFAULT_PARAMS_ENCODING));
            StringBuffer buf = new StringBuffer();
            String line;
            while (null != (line = br.readLine())) {
                buf.append(line).append("\n");
            }
            this.responseAsString = buf.toString();
            if(DEBUG) {
                Log.e("[Response] " + responseAsString);
            }
            stream.close();
            streamConsumed = true;
        } catch (NullPointerException npe) {
            // don't remember in which case npe can be thrown
            throw new IOException(npe.getMessage(), npe);
        }
    }

    public int getStatusCode() {
        return mStatusCode;
    }
    /**
     * Returns the response stream.<br>
     * This method cannot be called after calling asString() or asDcoument()<br>
     * It is suggested to call disconnect() after consuming the stream.
     *
     * Disconnects the internal HttpURLConnection silently.
     * @return response body stream
     * @throws TwitterException
     * @see #disconnect()
     */
    private InputStream asStream() {
        if(streamConsumed){
            throw new IllegalStateException("Stream has already been consumed.");
        }
        return is;
    }

    /**
     * Returns the response body as string.<br>
     * Disconnects the internal HttpURLConnection silently.
     * @return response body
     * @throws TwitterException
     */
    public String asString(){
        return responseAsString;
    }

    private static Pattern escaped = Pattern.compile("&#([0-9]{3,5});");

    /**
     * Unescape UTF-8 escaped characters to string.
     * @param original The string to be unescaped.
     * @return The unescaped string
     */
    public static String unescape(String original) {
        Matcher mm = escaped.matcher(original);
        StringBuffer unescaped = new StringBuffer();
        while (mm.find()) {
            mm.appendReplacement(unescaped, Character.toString(
                    (char) Integer.parseInt(mm.group(1), 10)));
        }
        mm.appendTail(unescaped);
        return unescaped.toString();
    }
    
    public String toString() {
        if(null != responseAsString){
            return responseAsString;
        }
        return "Response{" +
                "statusCode=" + mStatusCode + 
                ", responseString='" + responseAsString + '\'' +
                ", is=" + is +
                '}';
    }
}