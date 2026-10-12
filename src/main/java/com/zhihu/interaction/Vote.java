package com.zhihu.interaction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 投票表（多态设计：targetType+targetId 指向任意可投票对象，新增类型零迁移）。
 * 唯一索引 = 一个人对一个对象最多一条记录：改投是 UPDATE，取消是 DELETE。
 */
@Entity
@Table(name = "votes",
        uniqueConstraints = @UniqueConstraint(name = "uk_person_target",
                columnNames = {"user_id", "target_type", "target_id"}))
@Getter
@Setter
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "target_type", nullable = false, length = 20)
    private String targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(nullable = false)
    private Integer type;   // 1 赞同 / -1 反对

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
