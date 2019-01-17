package com.jx.common;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 响应前端的高复用对象
 * */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse <T> {

    private Integer status;
    private String msg;
    private T date;

    private ServerResponse(){}

    private ServerResponse(Integer status) {
        this.status = status;
    }
    private ServerResponse(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }
    private ServerResponse(Integer status, String msg, T date) {
        this.status = status;
        this.msg = msg;
        this.date = date;
    }
    private ServerResponse(Integer status,T date){
        this.status = status;
        this.date = date;
    }
    /**
     * 判断接口是否访问成功
     * */
    @JsonIgnore
    public boolean isSuccess(){
        return this.status==ResponseCode.SUCCESS;
    }
    /**
     * status : 0
     * */
    public static ServerResponse createServerResponseBySuccess(){
        return new ServerResponse(ResponseCode.SUCCESS);
    }
    /**
     * status : 0  ,msg,
     * */
    public static ServerResponse createServerResponseBySuccess(String msg){
        return new ServerResponse(ResponseCode.SUCCESS,msg);
    }

    /**
     * status:0 ,date
     * */
    public static <T> ServerResponse createServerResponseBySuccess(T date){
        return new ServerResponse(ResponseCode.SUCCESS,date);
    }
    /**
     * status : 0  ,msg,date
     * */
    public static <T> ServerResponse createServerResponseBySuccess(String msg,T date){
        return new ServerResponse(ResponseCode.SUCCESS,msg,date);
    }
    /**
     * status : 1
     * */
    public static ServerResponse createServerResponseByError(){
        return new ServerResponse(ResponseCode.ERROR);
    }
    /**
     * status : 自定义,msg,
     * */
    public static ServerResponse createServerResponseByError(Integer status){
        return new ServerResponse(status);
    }
    /**
     * status : 1  ,msg,
     * */
    public static ServerResponse createServerResponseByError(String msg){
        return new ServerResponse(ResponseCode.SUCCESS,msg);
    }
    /**
     * status : 自定义  ,msg,
     * */
    public static ServerResponse createServerResponseByError(Integer status,String msg){
        return new ServerResponse(status,msg);
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getDate() {
        return date;
    }

    public void setDate(T date) {
        this.date = date;
    }
}
