package com.wms.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wms.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;


public interface UserService extends IService<User> {

    IPage queryUserPage(IPage<User> page);

    IPage queryUserPageByWrapper(IPage<User> page, Wrapper wrapper);
}
