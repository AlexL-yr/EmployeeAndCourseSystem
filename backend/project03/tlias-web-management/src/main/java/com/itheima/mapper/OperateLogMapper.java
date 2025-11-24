package com.itheima.mapper;

import com.itheima.pojo.OperateLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OperateLogMapper {

    // ✅ 1. 插入日志（AOP切面调用）
    @Insert("insert into log(operate_emp_id, operate_time, class_name, method_name, method_params, return_value, cost_time) " +
            "values(#{operateEmpId}, #{operateTime}, #{className}, #{methodName}, #{methodParams}, #{returnValue}, #{costTime})")
    void insert(OperateLog log);

    // ✅ 2. 分页查询日志（LogController调用）
    @Select("select * from log order by operate_time desc limit #{offset}, #{pageSize}")
    List<OperateLog> listPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    // ✅ 3. 统计总数
    @Select("select count(*) from log")
    Long count();
}
