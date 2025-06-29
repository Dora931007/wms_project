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
        //检查传入参数是否为空
        if (queryPageParam == null || queryPageParam.getParam() == null) {
            return Result.fail();
        }

        // 从参数对象中获取前端传递的查询条件Map
        Map<String, Object> param = queryPageParam.getParam();
        //从Map中获取"name"参数
        String name = (String) param.get("name");
        // 构建分页参数
        Page<Storage> page = new Page<>(queryPageParam.getPageNum(), queryPageParam.getPageSize());
        // 构建查询条件
        LambdaQueryWrapper<Storage> queryWrapper = new LambdaQueryWrapper<>();
        // 如果name参数有效（非空且不是"null"字符串），添加模糊查询条件
        if (StringUtils.isNotBlank(name) && !"null".equals(name)) {
            queryWrapper.like(Storage::getName, name);
        }
        queryWrapper.orderByDesc(Storage::getId);
        //调用Service层方法，执行分页查询（自动组装分页SQL）
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
        //设置响应头
        String fileName = URLEncoder.encode("仓库信息", "UTF-8") + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        //获取所有数据
        List<Storage> list = storageService.list();

        //创建一个ArrayList来存储最终要导出的数据，每个元素是一个Map代表一行数据
        List<Map<String, Object>> exportList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Storage item = list.get(i);
            // 使用LinkedHashMap保持字段顺序(确保Excel列顺序一致)
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("serialNumber", i + 1); // 添加前端序号 (i+1)
            map.put("name", item.getName());
            map.put("remark", item.getRemark());
            exportList.add(map);
        }

        // 使用try-with-resources确保资源关闭（自动管理资源，避免内存泄漏）
        try (ExcelWriter writer = ExcelUtil.getWriter(true);
             ServletOutputStream out = response.getOutputStream()) {

            //设置表头别名（将实体类字段名映射为Excel表头显示名称）
            writer.addHeaderAlias("serialNumber", "序号");
            writer.addHeaderAlias("name", "仓库名称");
            writer.addHeaderAlias("remark", "备注");

            //写入数据并刷新  true-写入表头
            writer.write(exportList, true);
            //将内存中的Excel数据刷写到输出流  输出Excel文件到客户端  out-目标输出流 true-关闭输出流
            writer.flush(out, true);
        }
    }

    @PostMapping("/import")
    public Result importGoods(@RequestBody MultipartFile file) throws Exception {
        //使用Hutool的ExcelUtil工具类，基于文件输入流创建ExcelReader对象
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
        //读取整个Excel文件，自动将每一行数据转换成Storage类的对象并存储。
        List<Storage> storageList = reader.readAll(Storage.class);
        //批量写入数据到数据库
        storageService.saveBatch(storageList);
        return Result.suc();
    }

}
