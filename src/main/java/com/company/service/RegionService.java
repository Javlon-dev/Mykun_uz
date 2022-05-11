package com.company.service;

import com.company.dto.RegionDTO;
import com.company.entity.RegionEntity;
import com.company.enums.LangEnum;
import com.company.exception.AppBadRequestException;
import com.company.exception.ItemAlreadyExistsException;
import com.company.exception.ItemNotFoundException;
import com.company.repository.RegionRepository;
import com.company.validation.RegionValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RegionService {
    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    @Lazy
    private ProfileService profileService;

    public RegionDTO create(RegionDTO dto, Integer pId) {
//        RegionValidation.isValid(dto);

        Optional<RegionEntity> optional = regionRepository.findByKey(dto.getKey());
        if (optional.isPresent()) {
            log.warn("Unique {}", dto);
            throw new ItemAlreadyExistsException("This Region already used!");
        }

        RegionEntity entity = new RegionEntity();
        entity.setKey(dto.getKey());
        entity.setNameEn(dto.getNameEn());
        entity.setNameRu(dto.getNameRu());
        entity.setNameUz(dto.getNameUz());
        entity.setProfileId(pId);

        try {
            regionRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique Items!");
        }
        return toDTO(entity);
    }

    public List<RegionDTO> list() {
        List<RegionDTO> list = new ArrayList<>();

        regionRepository.findAllByVisible(true).forEach(entity -> {
            list.add(toDTO(entity));
        });

        return list;
    }

    public List<RegionDTO> listByLang(LangEnum lang) {
        List<RegionDTO> dtoList = new ArrayList<>();

        regionRepository.findAllByVisible(true).forEach(entity -> {
            dtoList.add(toDTO(entity, lang));
        });

        return dtoList;
    }

    public RegionDTO update(Integer id, RegionDTO dto, Integer pId) {
//        RegionValidation.isValid(dto);

        RegionEntity entity = get(id);

        entity.setKey(dto.getKey());
        entity.setNameEn(dto.getNameEn());
        entity.setNameRu(dto.getNameRu());
        entity.setNameUz(dto.getNameUz());
        entity.setProfileId(pId);
        entity.setUpdatedDate(LocalDateTime.now());

        try {
            regionRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique Items!");
        }
        return toDTO(entity);
    }

    public Boolean delete(Integer id) {
        RegionEntity entity = get(id);

        int n = regionRepository.updateVisible(false, id);
        return n > 0;
    }

    public RegionEntity get(Integer id) {
        RegionEntity entity = regionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Not found {}", id);
                    return new ItemNotFoundException("Not Found!");
                });

        if (!entity.getVisible()) {
            log.warn("Visible false {}", id);
            throw new ItemNotFoundException("Not Found!");
        }

        return entity;
    }

    public RegionDTO getById(Integer id, LangEnum lang) {
        RegionEntity entity = get(id);
        return toDTO(entity, lang);
    }

    private RegionDTO toDTO(RegionEntity entity, LangEnum lang) {
        RegionDTO dto = new RegionDTO();
        dto.setId(entity.getId());
        dto.setKey(entity.getKey());
        switch (lang) {
            case en -> dto.setName(entity.getNameEn());
            case ru -> dto.setName(entity.getNameRu());
            case uz -> dto.setName(entity.getNameUz());
        }
        return dto;
    }

    private RegionDTO toDTO(RegionEntity entity) {
        RegionDTO dto = new RegionDTO();
        dto.setId(entity.getId());
        dto.setKey(entity.getKey());
        dto.setNameEn(entity.getNameEn());
        dto.setNameUz(entity.getNameUz());
        dto.setNameRu(entity.getNameRu());
        dto.setProfileId(entity.getProfileId());
        dto.setUpdatedDate(entity.getUpdatedDate());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }

}
