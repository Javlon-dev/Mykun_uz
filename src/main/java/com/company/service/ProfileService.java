package com.company.service;

import com.company.dto.ProfileDTO;
import com.company.entity.AttachEntity;
import com.company.entity.ProfileEntity;
import com.company.enums.ProfileStatus;
import com.company.exception.ItemAlreadyExistsException;
import com.company.exception.ItemNotFoundException;
import com.company.repository.ProfileRepository;
import com.company.validation.ProfileValidation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private AttachService attachService;


    public ProfileDTO create(ProfileDTO dto) {
//        ProfileValidation.isValid(dto); // validation

        Optional<ProfileEntity> optional = profileRepository.findByEmail(dto.getEmail());
        if (optional.isPresent()) {
            log.warn("Unique {}", dto);
            throw new ItemAlreadyExistsException("This Email already used!");
        }

        ProfileEntity entity = new ProfileEntity();
        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        entity.setEmail(dto.getEmail());
        entity.setRole(dto.getRole());
        entity.setStatus(ProfileStatus.ACTIVE);

        String password = DigestUtils.md5Hex(dto.getPassword());
        entity.setPassword(password);

        profileRepository.save(entity);
        return toDTO(entity);
    }

    public PageImpl<ProfileDTO> paginationList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        List<ProfileDTO> dtoList = new ArrayList<>();

        Page<ProfileEntity> entityPage = profileRepository.findByVisible(true, pageable);
        entityPage.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });

        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public ProfileDTO getById(Integer id) {
        ProfileEntity entity = get(id);

        ProfileDTO dto = new ProfileDTO();
        dto.setName(entity.getName());
        dto.setSurname(entity.getSurname());
        dto.setEmail(entity.getEmail());
        dto.setCreatedDate(entity.getCreatedDate());

        return dto;
    }

    public ProfileEntity get(Integer id) {
        ProfileEntity entity = profileRepository.findById(id).orElseThrow(() -> {
            log.warn("Not found {}", id);
            return new ItemNotFoundException("Not Found!");
        });
        if (!entity.getVisible()) {
            log.warn("Visible false {}", id);
            throw new ItemNotFoundException("Not Found!");
        }
        return entity;
    }

    public ProfileDTO update(Integer id, ProfileDTO dto) {
//        ProfileValidation.isValid(dto); // validation

        ProfileEntity entity = get(id);

        if (!entity.getEmail().equals(dto.getEmail())) {
            throw new ItemAlreadyExistsException("This Email already used!");
        }

        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        entity.setEmail(dto.getEmail());
        entity.setUpdatedDate(LocalDateTime.now());

        String password = DigestUtils.md5Hex(dto.getPassword());
        entity.setPassword(password);

        profileRepository.save(entity);
        return toDTO(entity);
    }

    public Boolean delete(Integer id) {
        ProfileEntity entity = get(id);
        int n = profileRepository.updateVisible(false, id);
        return n > 0;
    }

    public Boolean profileImage(String attachId, Integer pId) {
        AttachEntity attachEntity = attachService.getByKey(attachId);

        ProfileEntity entity = get(pId);

        if (entity.getAttach() != null) {
            if (entity.getAttach().getId().equals(attachId)) {
                return true;
            }
            attachService.delete(entity.getAttach().getId());
        }

        try {
            profileRepository.updateAttach(attachId, pId);
        } catch (DataIntegrityViolationException e) {
            log.warn("Not found {}", attachId);
            throw new ItemNotFoundException("Not found!");
        }

        return true;
    }

    private ProfileDTO toDTO(ProfileEntity entity) {
        ProfileDTO dto = new ProfileDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSurname(entity.getSurname());
        dto.setEmail(entity.getEmail());
        dto.setUpdatedDate(entity.getUpdatedDate());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }

    // TODO: CRUD - Validation
    // List with pagination
    // Category - CRUD,List
    // Region - CRUD,List

}
