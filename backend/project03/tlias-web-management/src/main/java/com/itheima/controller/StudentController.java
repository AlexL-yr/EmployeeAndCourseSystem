package com.itheima.controller;

import com.itheima.pojo.*;
import com.itheima.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @GetMapping
    public Result page(StudentQueryParam studentQueryParam){
        log.info("分页查询: {}", studentQueryParam);
        PageResult<Student> pageResult =studentService.page(studentQueryParam);
        return Result.success(pageResult);
    }
    @DeleteMapping("/{ids}")
    public Result delete(@PathVariable List<Integer> ids){
        log.info("批量删除：{}", ids);
        studentService.deleteByIds(ids);
        return Result.success();
    }
    @PostMapping
    public Result save(@RequestBody Student student){
        log.info("新增员工：{}", student);
        studentService.add(student);
        return Result.success();
    }
    @GetMapping("/{id}")
    public Result get(@PathVariable Integer id){
        log.info("查询员工：{}", id);
        Student student = studentService.getById(id);
        return Result.success(student);
    }
    @PutMapping
    public Result update(@RequestBody Student student){
        log.info("更新员工：{}", student);
        studentService.update(student);
        return Result.success();
    }
    @PutMapping("/violation/{id}/{score}")
    public Result updateViolation(@PathVariable Integer id, @PathVariable Short score){
        log.info("学生违纪处理：{},{}", id,score);
        studentService.updateViolation(id, score);
        return Result.success();
    }
}
