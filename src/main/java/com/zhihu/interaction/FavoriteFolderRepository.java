package com.zhihu.interaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteFolderRepository extends JpaRepository<FavoriteFolder, Long> {

    List<FavoriteFolder> findByUserIdOrderByIdDesc(Long userId);
}
