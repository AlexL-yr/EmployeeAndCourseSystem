package com.itheima.filter;

import com.itheima.utils.CurrentHolder;
import com.itheima.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
@Slf4j
@WebFilter(urlPatterns ="/*")
public class TokenFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取请求路径
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/login")){
            log.info("登录操作，放行");
            chain.doFilter(request,response);

            return;
        }
        String token = request.getHeader("token");
        if(token ==null||token.isEmpty()){
            log.info("token为空，401");
            response.setStatus(401);
            return;
        }
        try {
            Claims claims = JwtUtils.parseToken(token);
            Integer empId = Integer.valueOf(claims.get("id").toString());
            CurrentHolder.setCurrentId(empId);
            log.info("当前用户id为：{}",empId);
        } catch (Exception e) {
            log.info("token非法，401");
            response.setStatus(401);
            return;
        }
        //放行
        log.info("token合法，放行");
        chain.doFilter(request,response);

        //delete Data in ThreadLocal
        CurrentHolder.remove();
    }
}
