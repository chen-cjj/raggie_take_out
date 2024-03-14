package com.jun.reggie.controller;
/*
 * @author cjj
 * */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jun.reggie.common.R;
import com.jun.reggie.dto.DishDto;
import com.jun.reggie.entity.Category;
import com.jun.reggie.entity.Dish;
import com.jun.reggie.entity.DishFlavor;
import com.jun.reggie.entity.Setmeal;
import com.jun.reggie.service.CategoryService;
import com.jun.reggie.service.DishFlavorService;
import com.jun.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 菜品信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        // dish中只有categoryId，而前端要的是categoryName
        Page<Dish> dishPage = new Page(page, pageSize);
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(name != null, "name", name);
        queryWrapper.orderByDesc("update_time");
        dishService.page(dishPage, queryWrapper);

        // DishDto类有categoryName属性
        Page<DishDto> dishDtoPage = new Page(page, pageSize);
        // 对象属性copy,copy除了records的其他属性
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");

        List<Dish> records = dishPage.getRecords();
        List<DishDto> dishDtos = new ArrayList<>();
        for (Dish dish : records) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);
            Category category = categoryService.getById(dish.getCategoryId());
            dishDto.setCategoryName(category.getName());
            dishDtos.add(dishDto);
        }

        dishDtoPage.setRecords(dishDtos);
        return R.success(dishDtoPage);
    }

    /**
     * 菜品信息添加
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> add(@RequestBody DishDto dishDto) {
        // 清理缓存中对应的数据
        redisTemplate.delete(dishDto.getCategoryId()+"_"+dishDto.getStatus());
        log.info(dishDto.toString());
        // 1 保存dish的信息
        dishService.save(dishDto);
        // 2 保存dishFlavor信息
        Long id = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(id);
        }
        dishFlavorService.saveBatch(flavors);
        return R.success("添加菜品成功");
    }

    /**
     * 根据id查询菜品信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable long id) {
        log.info("修改菜品的id：{}", id);
        // 1 查dish
        Dish dish = dishService.getById(id);
        // 2 查dishFlavors
        QueryWrapper<DishFlavor> queryWrapper = new QueryWrapper();
        queryWrapper.eq("dish_id", id);
        List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper);
        // 3 组装到DishDto中
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        dishDto.setFlavors(dishFlavors);
        return R.success(dishDto);
    }

    /**
     * 更新菜单信息
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    @Transactional
    public R<String> update(@RequestBody DishDto dishDto) {

        // 清理缓存中对应的数据
        redisTemplate.delete(dishDto.getCategoryId()+"_"+dishDto.getStatus());

        // 写回数据库
        // 写dish
        dishService.updateById(dishDto);
        // 写DishFlavor,先通过dish_id删除口味表，再添加
        QueryWrapper<DishFlavor> queryWrapper = new QueryWrapper<>();
        Long dishId = dishDto.getId();
        queryWrapper.eq("dish_id", dishId);
        dishFlavorService.remove(queryWrapper);
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }
        dishFlavorService.saveBatch(flavors);
        return R.success("修改成功");
    }

    /**
     * 通过categoryId查询菜品
     *
     * @return
     */
    // http://localhost:8080/dish/list?categoryId=1397844263642378242
    @GetMapping("/list")
    public R<List<DishDto>> getList(Dish dish) {
        // 缓存中有数据，直接返回
        String key = dish.getCategoryId() + "_" +dish.getStatus();
        List<DishDto> dishDtoList = (List<DishDto>)redisTemplate.opsForValue().get(key);
        if(dishDtoList!=null) return R.success(dishDtoList);

        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus, 1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        // 如果缓存中没有，从数据库中查到放入缓存
        redisTemplate.opsForValue().set(key, dishDtoList,20, TimeUnit.MINUTES);

        return R.success(dishDtoList);

    }

    /**
     * 停售
     *
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> statusStop(@RequestParam List<Long> ids) {
        List<Dish> dishes = dishService.listByIds(ids);
        for (Dish dish : dishes) {
            dish.setStatus(0);
        }
        dishService.updateBatchById(dishes);
        log.info(ids.toString());
        return R.success("停售成功");
    }

    /**
     * 起售
     *
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public R<String> statusOpen(@RequestParam List<Long> ids) {
        List<Dish> dishes = dishService.listByIds(ids);
        for (Dish dish : dishes) {
            dish.setStatus(1);
        }
        dishService.updateBatchById(dishes);
        log.info(ids.toString());
        return R.success("起售成功");
    }
}
