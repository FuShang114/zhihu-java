package com.zhihu.interaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteItemRepository extends JpaRepository<FavoriteItem, Long> {

    List<FavoriteItem> findByFolderIdOrderByIdDesc(Long folderId);

    boolean existsByFolderIdAndTargetTypeAndTargetId(Long folderId, String targetType, Long targetId);
}
