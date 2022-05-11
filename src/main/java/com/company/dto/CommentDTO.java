package com.company.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDTO extends BaseDTO{

    @NotBlank(message = "Content required")
    private String content;

    private Integer profileId;

    @NotBlank(message = "ArticleId required")
    @Positive
    private Integer articleId;

}
