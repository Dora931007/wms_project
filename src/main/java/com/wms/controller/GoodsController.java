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
import com.wms.entity.Goodstype;
import com.wms.entity.Storage;
import com.wms.entity.User;
import com.wms.service.GoodsService;
import com.wms.service.GoodstypeService;
import com.wms.service.StorageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    @Resource
    private GoodstypeService goodstypeService;

    @Resource
    private StorageService storageService;

    //新增
    @PostMapping("/save")
    public Result add(@RequestBody Goods goods) {
        System.out.println("保存商品数据为: " + goods);
        return goodsService.save(goods) ? Result.suc() : Result.fail();
    }

    //修改
    @PostMapping("/update")
    public Result update(@RequestBody Goods goods) {
        return goodsService.updateById(goods) ? Result.suc() : Result.fail();
    }

    //删除
    @GetMapping("/del")
    public Result del(@RequestParam String id) {
        return goodsService.removeById(id) ? Result.suc() : Result.fail();
    }

    //查询
    @GetMapping("/list")
    public Result list() {
        List list = goodsService.goodsList();
        return Result.suc(list);
    }

    //分页查询
    @PostMapping("/listPage")
    public Result listPage(@RequestBody @Valid QueryPageParam queryPageParam) { //使用@Valid注解自动验证参数合法性
        //参数校验,检查查询参数是否为空，避免空指针异常
        if (queryPageParam == null || queryPageParam.getParam() == null) {
            return Result.fail();
        }
        //从参数对象中获取查询条件Map
        Map<String, String> param = queryPageParam.getParam();
        String name = param.get("name");
        String goodsType = param.get("goodstype");
        String storage = param.get("storage");

        //构建分页对象, 使用MyBatis-Plus的Page类，传入当前页码和每页大小
        Page<Goods> page = new Page<>(queryPageParam.getPageNum(), queryPageParam.getPageSize());
        //构建查询条件 使用LambdaQueryWrapper构建类型安全的查询条件
        LambdaQueryWrapper<Goods> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.like(Goods::getName, name); //根据名称模糊查询
        }
        if (StringUtils.isNotBlank(goodsType)) {
            lambdaQueryWrapper.eq(Goods::getGoodstype, goodsType);// 根据物品分类精确查询
        }
        if (StringUtils.isNotBlank(storage)) {
            lambdaQueryWrapper.eq(Goods::getStorage, storage);// 根据仓库精确查询
        }
        //按ID降序，确保新添加的数据显示在前面
        lambdaQueryWrapper.orderByDesc(Goods::getId);
        //执行分页查询，调用Service层方法，传入分页参数和查询条件
        IPage<Goods> result = goodsService.listGoodsPage(page, lambdaQueryWrapper);
        //获取当前页的数据列表和总条数帮助前端显示分页控件
        return Result.suc(result.getRecords(), result.getTotal());
    }


    /**
     * 导出商品数据到Excel
     * @param response HTTP响应对象
     */
    @GetMapping("/export")
    public void exportGoods(HttpServletResponse response) throws Exception {
        ExcelWriter writer = ExcelUtil.getWriter(true);
        List<Goods> list = goodsService.goodsList();

        // 获取仓库和分类的映射关系
        Map<Integer, String> storageMap = storageService.getStorageMap();
        Map<Integer, String> goodsTypeMap = goodstypeService.getGoodsTypeMap();

        List<Map<String, Object>> exportList = new ArrayList<>();
        // 遍历商品列表(list)，i是当前索引
        for (int i = 0; i < list.size(); i++) {
            // 获取当前商品对象
            Goods item = list.get(i);
            // 使用LinkedHashMap保持字段顺序(确保Excel列顺序一致)
            Map<String, Object> goods = new LinkedHashMap<>();
            goods.put("serialNumber", i + 1); // 使用连续序号替代ID 序号从1开始
            goods.put("name", item.getName());
            goods.put("storage", storageMap.get(item.getStorage()));
            goods.put("goodstype", goodsTypeMap.get(item.getGoodstype()));
            goods.put("count", item.getCount());
            goods.put("remark", item.getRemark());
            // 将转换后的Map添加到导出列表
            exportList.add(goods);
        }

        // 设置表头别名
        writer.addHeaderAlias("serialNumber", "序号");
        writer.addHeaderAlias("name", "物品名称");
        writer.addHeaderAlias("storage", "所属仓库");
        writer.addHeaderAlias("goodstype", "所属分类");
        writer.addHeaderAlias("count", "物品数量");
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


    //导入
    @PostMapping("/import")
    public Result importGoods(@RequestBody MultipartFile file) throws Exception {
        //创建Excel读取器并解析文件内容
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
        //将Excel数据读取为Goods对象列表,readAll方法会自动将每一行数据映射为Goods对象
        List<Goods> goodsList = reader.readAll(Goods.class);

        //数据转换处理：将名称转换为ID
        for (Goods goods : goodsList) {
            // 根据仓库名称查询仓库ID
            Storage storage = storageService.lambdaQuery()
                    .eq(Storage::getName, goods.getStorageName())
                    .one();
            // 如果查询到仓库，设置商品对应的仓库ID
            if (storage != null) {
                goods.setStorage(storage.getId());
            }

            // 根据分类名称查询分类ID
            Goodstype goodsType = goodstypeService.lambdaQuery()
                    .eq(Goodstype::getName, goods.getGoodsTypeName())
                    .one();
            if (goodsType != null) {
                goods.setGoodstype(goodsType.getId());
            }
        }

        //使用MyBatis-Plus的saveBatch方法进行批量插入到数据库
        goodsService.saveBatch(goodsList);
        return Result.suc();
    }

    @GetMapping("/statistics")
    public Result getGoodsStatistics() {
        // 1. 按仓库统计商品数量
        List<Map<String, Object>> storageStats = goodsService.getGoodsCountByStorage();
        // 2. 按分类统计商品数量
        List<Map<String, Object>> typeStats = goodsService.getGoodsCountByType();
        // 3. 按物品统计商品数量
        List<Map<String, Object>> goodsStats = goodsService.getGoodsCountByGoods();
        // 4. 库存预警统计(数量<50的)
        int lowStockCount = goodsService.getLowStockCount(50);
        // 5. 商品总数
        int totalGoods = goodsService.count();
        // 6. 库存总量
        int totalStock = goodsService.getTotalStock();

        //创建结果Map，用于聚合所有统计信息，将各类统计结果放入结果Map中
        Map<String, Object> result = new HashMap<>();
        result.put("storageStats", storageStats);
        result.put("typeStats", typeStats);
        result.put("goodsStats", goodsStats);
        result.put("lowStockCount", lowStockCount);
        result.put("totalGoods", totalGoods);
        result.put("totalStock", totalStock);
        return Result.suc(result);


    }
}
