package com.itheima.aop;


import com.itheima.mapper.OperateLogMapper;
import com.itheima.pojo.OperateLog;
import com.itheima.utils.CurrentHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private OperateLogMapper operateLogMapper;

    /**
     * 切入点：拦截 com.itheima.controller 包下的所有类增删改操作方法
     * 假设增删改方法名包含关键字：add, insert, update, delete, remove
     */
    @Around("@annotation(com.itheima.anno.Log)")
    public Object logOperate(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();

        // 1. 执行目标方法
        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long costTime = endTime - startTime;

        try {
            // 2. 构建日志对象
            OperateLog olog = new OperateLog();

            // 操作人ID（这里示例用固定值或从SecurityContext获取）
            olog.setOperateEmpId(getCurrentUserId());

            // 操作时间
            olog.setOperateTime(LocalDateTime.now());

            // 类名
            String className = joinPoint.getTarget().getClass().getName();
            olog.setClassName(className);

            // 方法名
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String methodName = signature.getName();
            olog.setMethodName(methodName);

            // 方法参数
            Object[] args = joinPoint.getArgs();
            String params = Arrays.deepToString(args);
            if (params.length() > 2000) {
                params = params.substring(0, 2000);
            }
            olog.setMethodParams(params);

            // 返回值
            String returnValue = String.valueOf(result);
            if (returnValue.length() > 2000) {
                returnValue = returnValue.substring(0, 2000);
            }
            olog.setReturnValue(returnValue);

            // 执行耗时
            olog.setCostTime(costTime);

            // 3. 保存日志到数据库
            operateLogMapper.insert(olog);
            log.info("记录操作日志: {}", olog);

        } catch (Exception e) {
            log.error("记录操作日志失败: ", e);
        }

        return result;
    }

    private Integer getCurrentUserId() {
        return CurrentHolder.getCurrentId();
    }
}
