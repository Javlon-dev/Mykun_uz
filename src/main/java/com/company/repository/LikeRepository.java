package com.company.repository;

import com.company.entity.CommentEntity;
import com.company.entity.LikeEntity;
import com.company.mapper.LikeCountSimpleMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikeEntity, Integer> {

    Page<LikeEntity> findByVisible(Boolean visible, Pageable pageable);

    Page<LikeEntity> findByVisibleAndArticleId(Boolean visible, Pageable pageable, Integer aId);

    Page<LikeEntity> findByVisibleAndProfileId(Boolean visible, Pageable pageable, Integer pId);


    Optional<LikeEntity> findByArticleIdAndProfileId(Integer aId, Integer pId);

    @Transactional
    @Modifying
    @Query("update LikeEntity set visible = :visible where id = :id")
    int updateVisible(@Param("visible") Boolean visible, @Param("id") Integer id);

    @Query(value = "select sum( case when status = 'LIKE' THEN 1 else 0 END ) like_count," +
            "sum( case when status = 'LIKE' THEN 0 else 1 END ) dislike_count " +
            "from likes where article_id = ?1 ", nativeQuery = true)
    LikeCountSimpleMapper getLikeCountByArticleId(Integer aId);
}
