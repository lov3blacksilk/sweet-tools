package com.sweet.android.http.exception;

/**
 * 参数签名错误
 * 
 * @author fengzihua
 *
 */
public class SignatureException extends Exception {
    private static final long serialVersionUID = -2623309261569821357L;
    
    private int statusCode = -1;
    
    public SignatureException(){
        super();
    }
    public SignatureException(String errMsg){
        super(errMsg);
    }
    public SignatureException(String errMsg, Exception cause){
        super(errMsg, cause);
    }
    
    public SignatureException(String msg, int statusCode) {
        super(msg);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return this.statusCode;
    }
}