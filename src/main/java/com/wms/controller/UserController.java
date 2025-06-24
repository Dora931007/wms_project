package com.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.entity.Goods;
import com.wms.entity.Menu;
import com.wms.entity.User;
import com.wms.service.MenuService;
import com.wms.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private MenuService menuService;

    @GetMapping("/list")
    public List<User> list() {
        return userService.list();
    }

    @GetMapping("/findByNo")
    public Result findByNo(@RequestParam String no) {
        List list = userService.lambdaQuery().eq(User::getNo, no).list();
        return list.size() > 0 ? Result.suc(list) : Result.fail();
    }

    //新增
    @PostMapping("/save")
    public Result add(@RequestBody User user) {
        return userService.save(user) ? Result.suc() : Result.fail();
    }

    @PostMapping("/update")
    public Result update(@RequestBody User user) {
        return userService.updateById(user) ? Result.suc() : Result.fail();
    }

    @GetMapping("/del")
    public Result del(@RequestParam String id) {
        return userService.removeById(id) ? Result.suc() : Result.fail();
    }

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        List<User> list = userService.lambdaQuery()
                .eq(User::getNo, user.getNo())
                .eq(User::getPassword, user.getPassword())
                .list();

        if (list.size() > 0) {
            User user1 = list.get(0);
            List<Menu> menuList = menuService.lambdaQuery().like(Menu::getMenuright, user1.getRoleId()).list();
            HashMap<String, Object> res = new HashMap<>();
            res.put("user", user1);
            res.put("menu", menuList); // 返回的是 List<Menu>
            return Result.suc(res);

        }
        return Result.fail();
    }


    //修改
//    @PostMapping("/update")
//    public boolean update(@RequestBody User user){
//        return userService.updateById(user);
//    }

    //新增或修改
    @PostMapping("/saveOrUpdate")
    public boolean saveOrUpdate(@RequestBody User user) {
        return userService.saveOrUpdate(user);
    }

    //删除
    @GetMapping("/delete")
    public boolean delete(Integer id) {
        return userService.removeById(id);
    }

    //查询（模糊、匹配）
    @PostMapping("/listP")
    public Result listP(@RequestBody User user) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper();
        if (StringUtils.isNotBlank(user.getName())) {
            lambdaQueryWrapper.like(User::getName, user.getName());
        }
        return Result.suc(userService.list(lambdaQueryWrapper));
    }


    @PostMapping("/listPageC1")
    public Result listPageC1(@RequestBody @Valid QueryPageParam queryPageParam) {
        // 参数校验
        if (queryPageParam == null || queryPageParam.getParam() == null) {
            return Result.fail();
        }

        // 获取查询参数
        Map<String, String> param = queryPageParam.getParam();
        String name = param.get("name");
        String sex = param.get("sex");
        String roleId = param.get("roleId");
        // 构建分页对象
        Page<User> page = new Page<>(queryPageParam.getPageNum(), queryPageParam.getPageSize());
        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = buildQueryWrapper(name, sex, roleId);
        // 执行查询
        IPage<User> result = userService.queryUserPageByWrapper(page, queryWrapper);
        log.debug("查询用户列表，总数: {}", result.getTotal());
        return Result.suc(result.getRecords(), result.getTotal());
    }

    // 构建查询条件的方法
    private LambdaQueryWrapper<User> buildQueryWrapper(String name, String sex, String roleId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(name)) {
            wrapper.like(User::getName, name);
        }
        if (StringUtils.isNotBlank(sex)) {
            wrapper.eq(User::getSex, sex);
        }
        if (StringUtils.isNotBlank(roleId)) {
            wrapper.eq(User::getRoleId, roleId);
        }
        // 按ID降序排序
        wrapper.orderByDesc(User::getId);
        return wrapper;
    }


    //测试分页2
    @PostMapping("/listPage")
    public List<User> listPage(@RequestBody QueryPageParam queryPageParam) {
        HashMap param = queryPageParam.getParam();
        String name = (String) param.get("name");
        System.out.println("num==" + queryPageParam.getPageNum());
        System.out.println("size==" + queryPageParam.getPageSize());

        Page<User> page = new Page();
        page.setCurrent(queryPageParam.getPageNum());
        page.setSize(queryPageParam.getPageSize());
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(User::getName, name);

        IPage result = userService.page(page, lambdaQueryWrapper);
        System.out.println("Total===" + result.getTotal());

        return result.getRecords();
    }


    @PostMapping("/listPageC")
    public List<User> listPageC(@RequestBody QueryPageParam queryPageParam) {
        HashMap param = queryPageParam.getParam();
        String name = (String) param.get("name");
        System.out.println("num==" + queryPageParam.getPageNum());
        System.out.println("size==" + queryPageParam.getPageSize());


        Page<User> page = new Page();
        page.setCurrent(queryPageParam.getPageNum());
        page.setSize(queryPageParam.getPageSize());
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(User::getName, name);

        //IPage result = userService.pageC(page);
        IPage result = userService.queryUserPageByWrapper(page, lambdaQueryWrapper);
        System.out.println("Total===" + result.getTotal());

        return result.getRecords();
    }


}
