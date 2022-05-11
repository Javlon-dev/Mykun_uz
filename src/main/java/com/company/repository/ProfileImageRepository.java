package com.company.repository;

import com.company.entity.ProfileImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileImageRepository extends JpaRepository<ProfileImageEntity, Integer> {

    Optional<ProfileImageEntity> findByAttachId(String key);
}
