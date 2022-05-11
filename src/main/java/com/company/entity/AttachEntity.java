package com.company.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = AttachEntity.TABLE_NAME)
@Getter
@Setter
public class AttachEntity {
    static final String TABLE_NAME = "attach";

    @Id
    private String id;

    @Column
    private String path;

    @Column
    private String extension;

    @Column(name = "original_name")
    private String originalName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();
}
