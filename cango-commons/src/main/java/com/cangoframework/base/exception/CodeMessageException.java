package com.cangoframework.base.exception;

/**
 * Created by cango on 2018/1/11.
 */
public class CodeMessageException extends ResultException{
    private String code;
    private String message;

    public CodeMessageException(String code, String message) {
        super(code,message);
        this.code = code;
        this.message = message;
    }
    public int getIntCode() {
        return Integer.parseInt(code);
    }
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setCode(String code) {
        super.setResult(code);
        this.code = code;
    }

    public void setMessage(String message) {
        super.setDescription(message);
        this.message = message;
    }
}
