package com.cangoframework.base.exception;

/**
 * Created by cango on 2018/1/10.
 */
public class ResultException extends  Exception{
    private Object result;
    private Object description;

    public ResultException() {
    }

    public ResultException(Object result, Object description) {
        this.result = result;
        this.description = description;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public int getIntResult(){
        return Integer.parseInt(String.valueOf(result));
    }
    public String getStringResult(){
        return String.valueOf(result);
    }
    public String getStringDescription(){
        return String.valueOf(description);
    }
}
