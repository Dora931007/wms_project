package com.wms.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.wms.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface UserMapper extends BaseMapper<User> {

    /***
     * 简单分页
     */
    IPage<User> queryUserPage(IPage<User> page);

    /***
     * 带条件的分页查询
     */
    IPage<User> queryUserPageByWrapper(IPage<User> page, @Param(Constants.WRAPPER) Wrapper wrapper);
}
