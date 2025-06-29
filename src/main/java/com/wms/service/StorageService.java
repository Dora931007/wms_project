package com.wms.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wms.entity.Storage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;


public interface StorageService extends IService<Storage> {


    IPage pageCC(IPage<Storage> page, Wrapper wrapper);

    //通过仓库ID查找对应的名称。
    Map<Integer, String> getStorageMap();

    //通过仓库名称反向查找ID
    Map<String, Integer> getStorageNameToIdMap();

}
