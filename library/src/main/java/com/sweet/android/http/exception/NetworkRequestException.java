package com.sweet.android.http.exception;

/**
 * 网络请求错误
 * 
 * @author fengzihua
 *
 */
public class NetworkRequestException extends Exception {
    private static final long serialVersionUID = -2623309261327598087L;
    
    public static final int EXCEPTION_CODE_VALIDATE_TIME = -100;
    private int mExceptionTypeCode = -1;
    
    private int statusCode = -1;

    public NetworkRequestException(String msg) {
        super(msg);
    }

    public NetworkRequestException(Exception cause) {
        super(cause);
    }

    public NetworkRequestException(String msg, int statusCode) {
        super(msg);
        this.statusCode = statusCode;

    }

    public NetworkRequestException(String msg, Exception cause) {
        super(msg, cause);
    }

    public NetworkRequestException(String msg, Exception cause, int statusCode) {
        super(msg, cause);
        this.statusCode = statusCode;

    }
    public NetworkRequestException(Exception cause, int statusCode, int exceptionTypeCode){
        super(cause);
        this.statusCode = statusCode;
        mExceptionTypeCode = exceptionTypeCode;
    }
    public NetworkRequestException(int statusCode, int exceptionTypeCode){
        this.statusCode = statusCode;
        mExceptionTypeCode = exceptionTypeCode;
    }
    public int getExceptionTypeCode(){
        return mExceptionTypeCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}