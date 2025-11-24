package com.itheima.controller;

import com.itheima.anno.Log;
import com.itheima.pojo.Clazz;
import com.itheima.pojo.ClazzQueryParam;
import com.itheima.pojo.PageResult;
import com.itheima.pojo.Result;
import com.itheima.service.ClazzService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/clazzs")
@RestController
public class ClazzController {
    @Autowired
    private ClazzService clazzService;

    @GetMapping
    public Result list(ClazzQueryParam clazzQueryParam){
        log.info("分页查询班级：{}");
        PageResult<Clazz> pageResult =clazzService.page(clazzQueryParam);
        return Result.success(pageResult);
    }
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable List<Integer> id){
        log.info("批量删除班级：{}",id);
        clazzService.deleteById(id);
        return Result.success();
    }
    @Log
    @PostMapping
    public Result save(@RequestBody Clazz clazz){
        log.info("新增班级：{}",clazz);
        clazzService.add(clazz);
        return Result.success();
    }
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id){
        log.info("根据id查询班级：{}",id);
        Clazz clazz = clazzService.getById(id);
        return Result.success(clazz);
    }
    @Log
    @PutMapping
    public Result update(@RequestBody Clazz clazz){
        log.info("修改班级：{}",clazz);
        clazzService.update(clazz);
        return Result.success();
    }
    @GetMapping("/list")
    public Result list(){
        log.info("查询全部班级");
        List<Clazz> clazzList = clazzService.findAll();
        return Result.success(clazzList);
    }
}
