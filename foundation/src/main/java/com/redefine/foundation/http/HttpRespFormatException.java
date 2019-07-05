package com.redefine.foundation.http;

/**
 * Created by liubin on 2018/1/6.
 */

public class HttpRespFormatException extends Exception {

    public HttpRespFormatException() {
        super();
    }
    public HttpRespFormatException(String message, Throwable cause) {
        super(message, cause);
    }
    public HttpRespFormatException(String message) {
        super(message);
    }
    public HttpRespFormatException(Throwable cause) {
        super(cause);
    }

}
