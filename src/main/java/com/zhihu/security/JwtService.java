package com.zhihu.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 签发与验签（HS256）——Go 版 signToken/verifyToken 的 jjwt 平移。
 * 差异只在"用库不用手搓"，内部机制完全一致：payload 带 uid+exp，签名=HMAC(密钥, 头.载荷)。
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long ttlHours;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.ttl-hours}") long ttlHours) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.ttlHours = ttlHours;
    }

    public String issue(long userId, String username) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))          // ≈ payload 里的 uid
                .claim("username", username)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ttlHours * 3600_000)) // ≈ exp
                .signWith(key)
                .compact();
    }

    /**
     * 验签 + 过期检查一体（jjwt 在 parse 时自动校验 exp）。
     * 非法/过期统一返回 null——错误细节对上层折叠，防探测（同 Go 版设计）。
     */
    public Long verify(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Long.valueOf(claims.getSubject());
        } catch (Exception e) {
            return null;
        }
    }
}
