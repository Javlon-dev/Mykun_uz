package com.company.service;

import com.company.dto.AttachDTO;
import com.company.dto.ProfileDTO;
import com.company.dto.ProfileImageDTO;
import com.company.entity.AttachEntity;
import com.company.entity.ProfileEntity;
import com.company.entity.ProfileImageEntity;
import com.company.exception.AppBadRequestException;
import com.company.exception.ItemNotFoundException;
import com.company.repository.ProfileImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
public class ProfileImageService {

    @Autowired
    private ProfileImageRepository profileImageRepository;

    @Autowired
    private AttachService attachService;

    @Autowired
    private ProfileService profileService;

    @Value("${server.domain.name}")
    private String domainName;


    public ProfileImageDTO create(MultipartFile file, Integer pId) {

        AttachDTO dto = attachService.upload(file);

        ProfileImageEntity entity = new ProfileImageEntity();
        entity.setProfileId(pId);
        entity.setAttachId(dto.getId());
        entity.setUrl(domainName + "/attach/open/" + dto.getId());

        profileImageRepository.save(entity);
        return toDTO(entity);
    }

    public ProfileImageDTO update(MultipartFile file, String key, Integer pId) {

        ProfileEntity profileEntity = profileService.get(pId);

        ProfileImageEntity image = profileImageRepository.findByAttachId(key)
                .orElseThrow(() -> new ItemNotFoundException("Not found!"));

        AttachDTO dto = attachService.upload(file);

        ProfileImageEntity entity = new ProfileImageEntity();
        entity.setProfileId(pId);
        entity.setAttachId(dto.getId());
        entity.setUrl(domainName + "/attach/open/" + dto.getId());
        entity.setCreatedDate(image.getCreatedDate());
        entity.setUpdatedDate(LocalDateTime.now());

        profileImageRepository.save(entity);

        attachService.delete(key);

        return toDTO(entity);
    }

    public Boolean delete(String key, Integer pId) {
        ProfileEntity profileEntity = profileService.get(pId);

        attachService.delete(key);

        ProfileImageEntity entity = profileImageRepository.findByAttachId(key)
                .orElseThrow(() -> new ItemNotFoundException("Not found!"));

        profileImageRepository.deleteById(entity.getId());
        return true;
    }

    private ProfileImageDTO toDTO(ProfileImageEntity entity) {
        ProfileImageDTO dto = new ProfileImageDTO();
        dto.setId(entity.getId());
        dto.setProfileId(entity.getProfileId());
        dto.setAttachId(entity.getAttachId());
        dto.setUrl(entity.getUrl());
        dto.setUpdatedDate(entity.getUpdatedDate());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }

}
