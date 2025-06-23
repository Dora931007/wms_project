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
import com.wms.entity.*;
import com.wms.service.StorageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
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
    public Result listPage(@RequestBody @Valid QueryPageParam queryPageParam) {
        // 参数校验
        if (queryPageParam == null || queryPageParam.getParam() == null) {
            return Result.fail();
        }
        Map<String, Object> param = queryPageParam.getParam();
        String name = (String) param.get("name");
        // 构建分页参数
        Page<Storage> page = new Page<>(queryPageParam.getPageNum(), queryPageParam.getPageSize());
        // 构建查询条件
        LambdaQueryWrapper<Storage> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name) && !"null".equals(name)) {
            queryWrapper.like(Storage::getName, name);
        }
        queryWrapper.orderByDesc(Storage::getId);
        // 执行分页查询
        IPage<Storage> result = storageService.page(page, queryWrapper);
        return Result.suc(result.getRecords(), result.getTotal());
    }

    @GetMapping("/list")
    public Result list() {
        List list = storageService.list();
        return Result.suc(list);

    }

    @GetMapping("/export")
    public void exportStorage(HttpServletResponse response) throws Exception {

        // 1. 设置响应头
        String fileName = URLEncoder.encode("仓库信息", "UTF-8") + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        // 2. 获取所有数据
        List<Storage> list = storageService.list();

        // 3. 转换为导出格式并添加前端序号
        List<Map<String, Object>> exportList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Storage item = list.get(i);
            Map<String, Object> map = new LinkedHashMap<>();
            // 添加前端序号 (i+1)
            map.put("serialNumber", i + 1);
            map.put("name", item.getName());
            map.put("remark", item.getRemark());
            exportList.add(map);
        }

        // 4. 使用try-with-resources确保资源关闭
        try (ExcelWriter writer = ExcelUtil.getWriter(true);
             ServletOutputStream out = response.getOutputStream()) {

            // 5. 设置表头别名
            writer.addHeaderAlias("serialNumber", "序号");
            writer.addHeaderAlias("name", "仓库名称");
            writer.addHeaderAlias("remark", "备注");

            // 6. 写入数据并刷新
            writer.write(exportList, true);
            writer.flush(out, true);
        }
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
