package com.company.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @Column(name = "updated_date")
    protected LocalDateTime updatedDate;
    @Column(name = "created_date")
    protected LocalDateTime createdDate = LocalDateTime.now();
}
