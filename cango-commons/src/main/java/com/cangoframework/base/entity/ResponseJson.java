package com.cangoframework.base.entity;

import com.cangoframework.base.exception.ResultException;
import java.io.Serializable;

public class ResponseJson implements Serializable {
	
	private static final long serialVersionUID = 4426290390352986699L;
	
	private int code = 0;   		// 状态码
    private String message = "ok"; 	// 状态短语
    private Object body = ""; 		// 消息主体
    
	public ResponseJson() {
		
	}
	
	public ResponseJson(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public ResponseJson setError(int code, String message){
		this.code = code;
		this.message = message;
		return this;
	}
	
	public ResponseJson setSuccess(int code, String message){
		this.code = code;
		this.message = message;
		return this;
	}

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getBody() {
		return body;
	}
	public void setBody(Object body) {
		this.body = body;
	}

	public static ResponseJson setException(Exception e) {
		return setException(e,9999);
	}
	public static ResponseJson setException(Exception e, int defaultErrorCode) {
		return setException(e,defaultErrorCode,"服务器错误");
	}
	public static ResponseJson setException(Exception e, int defaultErrorCode , String defaultErrorMessage) {
		if(e instanceof ResultException){
			ResultException x = (ResultException) e;
			return new ResponseJson(x.getIntResult(),x.getMessage());
		}else{
			return new ResponseJson(defaultErrorCode,defaultErrorMessage);
		}
	}

}

