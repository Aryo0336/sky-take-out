package com.sky.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 参数顺序：saltLength(bytes), hashLength(bytes), parallelism, memory(KB), iterations
        // 16 字节盐，32 字节哈希，并行度 1，内存 16 MB(1<<14 KB)，迭代 2 次
        return new Argon2PasswordEncoder(16, 32, 1, 1 << 14, 2);
    }
}