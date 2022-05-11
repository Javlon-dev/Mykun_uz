package com.company.dto;

import com.company.enums.LikeStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LikeDTO extends BaseDTO{

    @NotBlank(message = "Status required")
    private LikeStatus status;

    private Integer profileId;

    @NotBlank(message = "ArticleId required")
    private Integer articleId;

    private Integer likeCount;
    private Integer dislikeCount;

}
