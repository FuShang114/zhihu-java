package com.zhihu.security;

import com.zhihu.common.BusinessException;
import com.zhihu.common.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录态拦截器——Go 版 AuthJWT 中间件的 Spring MVC 平移。
 * 区别：Gin 的分组+Use 按"路径组"挂载；MVC 拦截器只认路径不认 HTTP 方法，
 * 所以"公开只读（GET 问答）/登录写（发帖回帖）/必须登录（me、收藏）"三条规则在 preHandle 里判。
 */
@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;

    public JwtAuthInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        boolean alwaysAuth = path.equals("/api/v1/users/me") || path.startsWith("/api/v1/favorite-folders");
        boolean writeOp = !"GET".equalsIgnoreCase(method)
                && (path.startsWith("/api/v1/questions") || path.startsWith("/api/v1/answers"));
        if (!alwaysAuth && !writeOp) {
            return true; // 公开只读路径放行
        }

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        Long userId = jwtService.verify(auth.substring(7));
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        request.setAttribute("userId", userId); // ≈ c.Set("uid", ...)，传给后面的 handler
        return true;
    }
}
