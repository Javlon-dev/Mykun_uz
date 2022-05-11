package com.company.repository;

import com.company.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface CommentRepository extends JpaRepository<CommentEntity,Integer> {

    Page<CommentEntity> findByVisible(Boolean visible, Pageable pageable);

    Page<CommentEntity> findByVisibleAndArticleId(Boolean visible, Pageable pageable, Integer aId);

    Page<CommentEntity> findByVisibleAndProfileId(Boolean visible, Pageable pageable, Integer pId);

    @Transactional
    @Modifying
    @Query("update CommentEntity set visible = :visible where id = :id")
    int updateVisible(@Param("visible") Boolean visible, @Param("id") Integer id);
}
