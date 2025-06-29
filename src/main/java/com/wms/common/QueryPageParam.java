package com.wms.common;

import lombok.Data;

import java.util.HashMap;


//分页查询参数封装类  统一管理页码和每页条数
@Data
public class QueryPageParam {
    //默认
    private static int PAGE_SIZE = 20; //默认每页返回20条数据
    private static int PAGE_NUM = 1;//默认查询第1页

    private  int pageSize = PAGE_SIZE;
    private  int pageNum = PAGE_NUM;

    private HashMap param= new HashMap(); //通过HashMap承载动态查询条件

}
