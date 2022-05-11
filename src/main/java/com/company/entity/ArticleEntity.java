package com.company.entity;

import com.company.enums.ArticleStatus;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = ArticleEntity.TABLE_NAME)
@Setter
@Getter
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
public class ArticleEntity extends BaseEntity {

    static final String TABLE_NAME = "article";

    public ArticleEntity() {
    }

    public ArticleEntity(Integer id, String title, String description, String attachId, LocalDateTime publishedDate) {
        super.id = id;
        this.title = title;
        this.description = description;
        this.attachId = attachId;
        this.publishedDate = publishedDate;
    }

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column
    private Boolean visible = true;

    @Column(name = "profile_id", nullable = false)
    private Integer profileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private ProfileEntity profile;

    @Column(name = "attach_id", nullable = false)
    private String attachId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attach_id", insertable = false, updatable = false)
    private AttachEntity attach;

    @Column(name = "region_id", nullable = false)
    private Integer regionId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", insertable = false, updatable = false)
    private RegionEntity region;

    @Column(name = "type_id", nullable = false)
    private Integer typeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", insertable = false, updatable = false)
    private ArticleTypeEntity type;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private CategoryEntity category;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "shared_count")
    private Integer sharedCount = 0;

    @Column
    @Enumerated(EnumType.STRING)
    private ArticleStatus status;
    @Column(name = "published_date")
    private LocalDateTime publishedDate;

    @Type(type = "list-array")
    @Column(name = "tag_list", columnDefinition = "integer[]")
    private List<Integer> tagList;
}
