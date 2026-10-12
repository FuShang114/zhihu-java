package com.zhihu.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtAuthInterceptor jwtAuthInterceptor;
    private final CurrentUserArgumentResolver currentUserResolver;

    public WebMvcConfig(JwtAuthInterceptor jwtAuthInterceptor, CurrentUserArgumentResolver currentUserResolver) {
        this.jwtAuthInterceptor = jwtAuthInterceptor;
        this.currentUserResolver = currentUserResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 细粒度的"哪些方法/路径要登录"由拦截器内部规则判断（MVC 拦截器不认 HTTP 方法）
        registry.addInterceptor(jwtAuthInterceptor).addPathPatterns("/api/v1/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserResolver);
    }
}
