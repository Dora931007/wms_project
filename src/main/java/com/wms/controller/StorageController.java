package com.wms.controller;


import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.entity.Goods;
import com.wms.entity.Menu;
import com.wms.entity.Storage;
import com.wms.entity.User;
import com.wms.service.StorageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/storage")
public class StorageController {
    @Resource
    private StorageService storageService;

    @PostMapping("/save")
    public Result add(@RequestBody Storage storage) {
        return storageService.save(storage) ? Result.suc() : Result.fail();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Storage storage) {
        return storageService.updateById(storage) ? Result.suc() : Result.fail();
    }

    @GetMapping("/del")
    public Result del(@RequestParam String id) {
        return storageService.removeById(id) ? Result.suc() : Result.fail();
    }

    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam queryPageParam) {
        HashMap param = queryPageParam.getParam();
        String name = (String) param.get("name");

        Page<Storage> page = new Page();
        page.setCurrent(queryPageParam.getPageNum());
        page.setSize(queryPageParam.getPageSize());

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name) && !"null".equals(name)) {
            lambdaQueryWrapper.like(User::getName, name);
        }

        //IPage result = userService.pageC(page);
        IPage result = storageService.pageCC(page, lambdaQueryWrapper);
        storageService.pageCC(page, lambdaQueryWrapper);

        List<Storage> storageList = storageService.lambdaQuery()
                .orderByDesc(Storage::getId)
                .list();
        return Result.suc(result.getRecords(),result.getTotal());


    }

    @GetMapping("/list")
    public Result list() {
        List list = storageService.list();
        return Result.suc(list);

    }


    @GetMapping("/export")
    public void exportStorage(HttpServletResponse response) throws Exception {
        ExcelWriter writer = ExcelUtil.getWriter(true);
        List<Storage> list = storageService.list();

        // 创建用于导出的Map列表
        List<Map<String, Object>> exportList = new ArrayList<>();
        for (Storage item : list) {
            Map<String, Object> storage = new LinkedHashMap<>();
            storage.put("id", item.getId());
            storage.put("name", item.getName());
            storage.put("remark", item.getRemark());
            exportList.add(storage);
        }

        // 设置只导出需要的列（排除原始ID字段）
        writer.addHeaderAlias("id", "序号");
        writer.addHeaderAlias("name", "仓库名称");
        writer.addHeaderAlias("remark", "备注");

        writer.write(exportList, true);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("content-Disposition", "attachment;filename=" + URLEncoder.encode("物品信息", "UTF-8") + ".xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        writer.flush(outputStream, true);
        writer.close();
        outputStream.flush();
        outputStream.close();
    }

    @PostMapping("/import")
    public Result importGoods(@RequestBody MultipartFile file) throws Exception {
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
        List<Storage> storageList = reader.readAll(Storage.class);
        //写入数据到数据库
        storageService.saveBatch(storageList);
        return Result.suc();
    }

}
