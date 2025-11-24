package com.itheima.service.impl;

import com.itheima.mapper.OperateLogMapper;
import com.itheima.pojo.OperateLog;
import com.itheima.pojo.PageResult;
import com.itheima.service.OperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperateLogServiceImpl implements OperateLogService {

    @Autowired
    private OperateLogMapper operateLogMapper;

    @Override
    public PageResult<OperateLog> page(Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<OperateLog> rows = operateLogMapper.listPage(offset, pageSize);
        Long total = operateLogMapper.count();
        return new PageResult<>(total, rows);
    }
}
