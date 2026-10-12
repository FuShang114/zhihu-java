package com.zhihu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码哈希器：内部就是你在 Go 里调过的 bcrypt（GenerateFromPassword/CompareHashAndPassword 的 Java 版）。
 * 只引 spring-security-crypto 单件，不启用 Spring Security 全家桶过滤链。
 */
@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
