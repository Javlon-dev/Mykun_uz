package com.company.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttachDTO {

    public AttachDTO() {
    }

    public AttachDTO(String url) {
        this.url = url;
    }

    private String id;

    private String path;

    private String url;

    private String originalName;

    private LocalDateTime createdDate;

}
