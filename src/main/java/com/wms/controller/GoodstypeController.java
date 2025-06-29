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
import com.wms.entity.Goodstype;
import com.wms.service.GoodstypeService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.util.*;


@RestController
@RequestMapping("/goodstype")
public class GoodstypeController {
    @Resource
    private GoodstypeService goodstypeService;

    @PostMapping("/save")
    public Result add(@RequestBody Goodstype goodstype) {
        return goodstypeService.save(goodstype) ? Result.suc() : Result.fail();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Goodstype goodstype) {
        return goodstypeService.updateById(goodstype) ? Result.suc() : Result.fail();
    }

    @GetMapping("/del")
    public Result del(@RequestParam String id) {
        return goodstypeService.removeById(id) ? Result.suc() : Result.fail();
    }

    /*
    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam queryPageParam){
        HashMap param = queryPageParam.getParam();
        String name = (String)param.get("name");

        Page<Goodstype> page = new Page();
        page.setCurrent(queryPageParam.getPageNum());
        page.setSize(queryPageParam.getPageSize());

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(name) && !"null".equals(name)){
            lambdaQueryWrapper.like(User::getName,name);
        }

        //IPage result = userService.pageC(page);
        IPage result = goodstypeService.pageCC(page,lambdaQueryWrapper);
        goodstypeService.pageCC(page,lambdaQueryWrapper);

        List<Goodstype> goodstypeList = goodstypeService.lambdaQuery()
                .orderByDesc(Goodstype::getId)
                .list();
        System.out.println(goodstypeList);
        return Result.suc(result.getRecords(),result.getTotal());
    } */

    @PostMapping("/listPage")
    public Result listPage(@RequestBody @Valid QueryPageParam queryPageParam) {
        // 1. 参数校验
        if (queryPageParam == null || queryPageParam.getParam() == null) {
            return Result.fail();
        }

        // 2. 提取查询参数
        Map<String, Object> param = queryPageParam.getParam();
        String name = (String) param.get("name");

        // 3. 构建分页参数
        Page<Goodstype> page = new Page<>(queryPageParam.getPageNum(), queryPageParam.getPageSize());

        // 4. 构建查询条件
        LambdaQueryWrapper<Goodstype> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name) && !"null".equals(name)) {
            queryWrapper.like(Goodstype::getName, name);
        }
        queryWrapper.orderByDesc(Goodstype::getId);

        // 5. 执行分页查询
        IPage<Goodstype> result = goodstypeService.page(page, queryWrapper);

        // 6. 返回结果
        return Result.suc(result.getRecords(), result.getTotal());
    }

    @GetMapping("/list")
    public Result list() {
        List list = goodstypeService.list();
        return Result.suc(list);
    }

 /*
    @GetMapping("/export")
    public void exportStorage(HttpServletResponse response) throws Exception {
        ExcelWriter writer = ExcelUtil.getWriter(true);
        List<Goodstype> list = goodstypeService.list();
        // 创建用于导出的Map列表
        List<Map<String, Object>> exportList = new ArrayList<>();
        for (Goodstype item : list) {
            Map<String, Object> goodsType = new LinkedHashMap<>();
            goodsType.put("id", item.getId());
            goodsType.put("name", item.getName());
            goodsType.put("remark", item.getRemark());
            exportList.add(goodsType);
        }

        // 设置只导出需要的列（排除原始ID字段）
        writer.addHeaderAlias("id", "序号");
        writer.addHeaderAlias("name", "物品分类名称");
        writer.addHeaderAlias("remark", "备注");

        writer.write(exportList, true);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("content-Disposition", "attachment;filename=" + URLEncoder.encode("物品信息", "UTF-8") + ".xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        writer.flush(outputStream, true);
        writer.close();
        outputStream.flush();
        outputStream.close();
    }  */

    @GetMapping("/export")
    public void exportGoodsType(HttpServletResponse response) throws Exception {
        //设置响应头
        String fileName = URLEncoder.encode("物品分类信息", "UTF-8") + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        //获取数据并转换为导出格式
        List<Goodstype> list = goodstypeService.list();
        List<Map<String, Object>> exportList = new ArrayList<>();

        //转换为导出格式并添加前端序号
        for (int i = 0; i < list.size(); i++) {
            Goodstype item = list.get(i);
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("serialNumber", i + 1);
            map.put("name", item.getName());
            map.put("remark", item.getRemark());
            exportList.add(map);
        }

        // 使用try-with-resources确保资源关闭（自动管理资源，避免内存泄漏）
        // 参数true表示生成xlsx格式（false为xls）
        try (ExcelWriter writer = ExcelUtil.getWriter(true);
             ServletOutputStream out = response.getOutputStream()) {

            //设置表头别名（将实体类字段名映射为Excel表头显示名称）
            writer.addHeaderAlias("serialNumber", "序号");
            writer.addHeaderAlias("name", "物品分类名称");
            writer.addHeaderAlias("remark", "备注");

            //写入数据并刷新  true-写入表头
            writer.write(exportList, true);
            //将内存中的Excel数据刷写到输出流 out-目标输出流 true-关闭输出流
            writer.flush(out, true);
        }
    }
    @PostMapping("/import")
    public Result importGoods(@RequestBody MultipartFile file) throws Exception {
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
        //读取整个Excel文件，自动将每一行数据转换成Goodstype类的对象并存储。
        List<Goodstype> goodstypeList = reader.readAll(Goodstype.class);
        //写入数据到数据库
        goodstypeService.saveBatch(goodstypeList);
        return Result.suc();
    }

}
