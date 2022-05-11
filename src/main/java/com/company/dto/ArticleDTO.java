package com.company.dto;

import com.company.entity.*;
import com.company.enums.ArticleStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticleDTO extends BaseDTO {

    @NotBlank(message = "Title required")
    private String title;

    @NotBlank(message = "Description required")
    private String description;

    @NotBlank(message = "Content required")
    private String content;

    private Boolean visible;

    private Integer profileId;

    @NotBlank(message = "AttachId required")
    private String attachId;

    @Positive
    @NotBlank(message = "RegionId required")
    private Integer regionId;

    @Positive
    @NotBlank(message = "ArticleTypeId required")
    private Integer typeId;

    @Positive
    @NotBlank(message = "CategoryId required")
    private Integer categoryId;

    private Integer viewCount;

    private Integer sharedCount;

    private LocalDateTime publishedDate;

    @NotEmpty
    @NotBlank(message = "TagList required")
    private List<Integer> tagList;

    private AttachDTO image;

    private String sharedLink;

    private String tagName;

    private LikeDTO like;
    private CategoryDTO category;
    private RegionDTO region;
    private ArticleTypeDTO type;
    private List<TagDTO> tags;
}
