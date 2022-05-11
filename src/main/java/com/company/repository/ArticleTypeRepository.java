package com.company.repository;

import com.company.entity.ArticleTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;

public interface ArticleTypeRepository extends JpaRepository<ArticleTypeEntity, Integer> {

    Page<ArticleTypeEntity> findByVisible(Boolean visible, Pageable pageable);

    Optional<ArticleTypeEntity> findByKey(String key);

    @Transactional
    @Modifying
    @Query("update ArticleTypeEntity set visible = :visible where id = :id")
    int updateVisible(@Param("visible") Boolean visible, @Param("id") Integer id);
}
