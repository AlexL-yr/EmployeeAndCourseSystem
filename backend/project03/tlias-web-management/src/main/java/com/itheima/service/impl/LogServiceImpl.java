package com.itheima.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.mapper.LogMapper;
import com.itheima.pojo.OperateLog;
import com.itheima.pojo.PageResult;
import com.itheima.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogMapper logMapper;

    @Override
    public PageResult<OperateLog> page(Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<OperateLog> list = logMapper.list();
        PageInfo<OperateLog> pageInfo = new PageInfo<>(list);

        PageResult<OperateLog> pageBean = new PageResult<>();
        pageBean.setRows(pageInfo.getList());
        pageBean.setTotal(pageInfo.getTotal());
        return pageBean;
    }
}