//************************************************************************************
//
// SYSTEM        : OCJ MALL system
//
// PROGRAM NAME  : ResultCode.java
//
// Outline       :
//
//  (c) Copyright OCJ 2017
//
// Modification history:
//
// DATE           LEVEL  NAME             COMMENT
// -------------  -----  ---------------  --------------------------------------------
// 2017年9月29日      A0.00  Administrator            初始化
//************************************************************************************

package com.cloud.fmnode.common;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * @author Administrator
 *
 */
public class ResultCode implements Serializable{
	private static final long serialVersionUID = 3746649834906235380L;
	
	public static final String CODE_SUCCESS = "200";
	public static final String MSG_SUCCESS = "操作成功！";
	public static final String CODE_UNKNOWN_ERROR = "10086";
	public static final String MSG_UNKNOWN_ERROR = "系统异常！";
	
	@JsonProperty("code")
	private String code;
	
	@JsonProperty("message")
	private String message;
	
	@JsonProperty("data")
	private Object data;
	
	@JsonIgnore
	private boolean success = false;
	
	protected ResultCode(String code, String message, Object data){
		this.code = code;
		this.message = message;
		this.data = data;
		this.success = StringUtils.equals(code, CODE_SUCCESS);
	}
	
	public boolean isSuccess(){
		return success;
	}
	
	public static ResultCode getFailure(){
		return new ResultCode(CODE_UNKNOWN_ERROR, MSG_UNKNOWN_ERROR, null);
	}
	
	public static ResultCode getFailure(String msg){
		return new ResultCode(CODE_UNKNOWN_ERROR, msg, null);
	}
	
	public static ResultCode getFailure(String code, String msg){
		return new ResultCode(code, msg, null);
	}
	
	public static ResultCode getFailureReturn(String code, String msg, Object data){
		return new ResultCode(code, msg, data);
	}
	
	public static ResultCode getSuccess(){
		return new ResultCode(CODE_SUCCESS, MSG_SUCCESS, null);
	}
	
	public static ResultCode getSuccess(String msg){
		return new ResultCode(CODE_SUCCESS, msg, null);
	}
	
	public static ResultCode getSuccessReturn(Object data){
		return new ResultCode(CODE_SUCCESS, MSG_SUCCESS, data);
	}
	
	public static ResultCode getSuccessReturn(String msg, Object data){
		return new ResultCode(CODE_SUCCESS, msg, data);
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	public void setData(Object data){this.data = data;}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() { return data; }

	/**
	 * @return the data
	 */
	/*public Object getData() {
		return data;
	}*/

}
