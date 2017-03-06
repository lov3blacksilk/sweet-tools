package com.sweet.android.http.exception;

/**
 * 请求方法错误
 * @author fengzihua
 *
 */
public class MethodRequestException extends Exception {
    private static final long serialVersionUID = -2623309261327598087L;
    
    private int statusCode = -1;

    public MethodRequestException(String msg) {
        super(msg);
    }

    public MethodRequestException(Exception cause) {
        super(cause);
    }

    public MethodRequestException(String msg, int statusCode) {
        super(msg);
        this.statusCode = statusCode;
    }

    public MethodRequestException(String msg, Exception cause) {
        super(msg, cause);
    }

    public MethodRequestException(String msg, Exception cause, int statusCode) {
        super(msg, cause);
        this.statusCode = statusCode;
    }
    
    public MethodRequestException(int statusCode){
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return this.statusCode;
    }
}