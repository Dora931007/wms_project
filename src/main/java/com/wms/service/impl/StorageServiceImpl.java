package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wms.entity.Storage;
import com.wms.entity.User;
import com.wms.mapper.StorageMapper;
import com.wms.service.StorageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class StorageServiceImpl extends ServiceImpl<StorageMapper, Storage> implements StorageService {
    @Resource
    private StorageMapper storageMapper;

    @Override
        public IPage pageCC(IPage< Storage > page, Wrapper wrapper) {
            return storageMapper.pageCC(page,wrapper);
        }

    //获取仓库类型ID与名称的映射关系
    @Override
    public Map<Integer, String> getStorageMap() {
        List<Storage> storages = storageMapper.selectList(null);
        return storages.stream()
                .collect(Collectors.toMap(Storage::getId, Storage::getName));
    }

    @Override
    public Map<String, Integer> getStorageNameToIdMap() {
        List<Storage> list = this.list();
        return list.stream().collect(Collectors.toMap(Storage::getName, Storage::getId));
    }


}
