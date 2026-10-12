package com.zhihu.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 用户实体。表设计约定与 Go 版一致：DATETIME(3) 精度、username 唯一索引充当并发注册的裁判。
 * 实体可变 → 不用 record，用 Lombok @Getter/@Setter 消灭样板。
 */
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "uk_username", columnNames = "username"))
@Getter
@Setter
public class User {

    public static final short STATUS_NORMAL = 0;
    public static final short STATUS_BANNED = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;   // bcrypt 指纹；任何响应都不许携带（由 UserView 保证）

    @Column(length = 64)
    private String nickname;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(length = 255)
    private String bio;

    @Column(nullable = false)
    private Short status = STATUS_NORMAL;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
