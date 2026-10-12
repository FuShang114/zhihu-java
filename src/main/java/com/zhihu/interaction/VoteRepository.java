package com.zhihu.interaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByUserIdAndTargetTypeAndTargetId(Long userId, String targetType, Long targetId);
}
