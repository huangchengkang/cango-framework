package com.cangoframework.base.exception;

/**
 * Created by cango on 2018/1/11.
 */
public class IntCodeException extends CodeMessageException{

    public IntCodeException(String code, String message) {
        super(code, message);
    }
    public IntCodeException(int code, String message) {
        super(String.valueOf(code), message);
    }
}
