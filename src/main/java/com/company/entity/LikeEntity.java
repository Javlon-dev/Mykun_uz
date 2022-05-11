package com.company.entity;

import com.company.enums.LikeStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = LikeEntity.TABLE_NAME)
@Getter
@Setter
public class LikeEntity extends BaseEntity{

    static final String TABLE_NAME = "likes";

    @Column
    @Enumerated(value = EnumType.STRING)
    private LikeStatus status;


    @Column(name = "profile_id", nullable = false)
    private Integer profileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private ProfileEntity profile;

    @Column(name = "article_id", nullable = false)
    private Integer articleId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", insertable = false, updatable = false)
    private ArticleEntity article;

    @Column
    private Boolean visible = true;

}
