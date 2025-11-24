package com.itheima.interceptor;

import com.itheima.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求路径
        String requestURI = request.getRequestURI();

        // ✅ 1️⃣ 登录接口必须放行，否则永远拿不到 token
        if (requestURI.contains("/login") || requestURI.contains("/register") || requestURI.contains("/common/")) {
            log.info("登录或公共操作，放行 -> {}", requestURI);
            return true;
        }

        // ✅ 2️⃣ 其他接口验证 token
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            log.info("token为空，401 -> {}", requestURI);
            response.setStatus(401);
            return false;
        }

        try {
            JwtUtils.parseToken(token);
        } catch (Exception e) {
            log.info("token非法，401 -> {}", requestURI);
            response.setStatus(401);
            return false;
        }

        // ✅ 3️⃣ 验证通过
        log.info("token合法，放行 -> {}", requestURI);
        return true;
    }
}
