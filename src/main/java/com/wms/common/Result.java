package com.wms.common;

import lombok.Data;

@Data
public class Result {

    private Integer code; //200 400
    private String msg; //成功 失败
    private Long total; //总记录数
    private Object data; //数据

    public static Result fail(){
        return result(400,"失败",0L,null);
    }

    public static Result suc(){
        return result(200,"成功",0L,null);
    }

    public static Result suc(Object data){
        return result(200,"成功",0L,data);
    }

    public static Result suc(Object data,Long total){
        return result(200,"成功",total,data);
    }

    public static Result result(Integer code,String msg,Long total,Object data){
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setTotal(total);
        result.setData(data);
        return result;
    }

}
