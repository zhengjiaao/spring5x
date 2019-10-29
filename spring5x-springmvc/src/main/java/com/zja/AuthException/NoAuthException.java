package com.zja.AuthException;

/**
 * @author ZhengJa
 * @description 全局异常处理：自定义异常
 * @data 2019/10/22
 */
public class NoAuthException extends RuntimeException{
    public NoAuthException() {
        super();
    }

    public NoAuthException(String message) {
        super(message);
    }

    public NoAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoAuthException(Throwable cause) {
        super(cause);
    }
}
