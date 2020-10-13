package com.feijian.response;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * @Title: ResponseResult
 * @Description: 前端请求响应结果,code:编码,message:描述,obj对象，可以是单个数据对象，数据列表或者PageInfo
 * @author: youqing
 * @version: 1.0
 * @date: 2018/11/23 9:48
 */
public class ResponseResult implements Serializable{

    private String code;
    private String message;
    private Object obj;

    public ResponseResult() {
        this.code = ResponseCode.SUCCESS.getCode();
        this.message = ResponseCode.SUCCESS.getMessage();
    }

    public ResponseResult(String code, String msg){
        this.code = code;
        this.message = msg;
    }

    public ResponseResult(ResponseCode statusMessage, Object data){
        this.code = statusMessage.getCode();
        this.message = statusMessage.getMessage();
        this.obj = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    @Override public String toString() {
        return "ResponseResult{" + "code='" + code + '\'' + ", message='"
                + message + '\'' + ", obj=" + obj + '}';
    }

}
