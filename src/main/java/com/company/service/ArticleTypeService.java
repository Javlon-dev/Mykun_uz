package com.company.service;

import com.company.dto.ArticleTypeDTO;
import com.company.entity.ArticleTypeEntity;
import com.company.enums.LangEnum;
import com.company.exception.AppBadRequestException;
import com.company.exception.ItemAlreadyExistsException;
import com.company.exception.ItemNotFoundException;
import com.company.repository.ArticleTypeRepository;
import com.company.validation.ArticleTypeValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ArticleTypeService {

    @Autowired
    private ArticleTypeRepository articleTypeRepository;

    @Autowired
    @Lazy
    private ProfileService profileService;

    public ArticleTypeDTO create(ArticleTypeDTO dto, Integer pId) {
//        ArticleTypeValidation.isValid(dto);

        Optional<ArticleTypeEntity> optional = articleTypeRepository.findByKey(dto.getKey());
        if (optional.isPresent()) {
            log.warn("Unique {}", dto);
            throw new ItemAlreadyExistsException("This Article Type already used!");
        }

        ArticleTypeEntity entity = new ArticleTypeEntity();
        entity.setKey(dto.getKey());
        entity.setNameEn(dto.getNameEn());
        entity.setNameRu(dto.getNameRu());
        entity.setNameUz(dto.getNameUz());
        entity.setProfileId(pId);

        try {
            articleTypeRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique Items!");
        }
        return toDTO(entity);
    }

    public PageImpl<ArticleTypeDTO> listByLang(LangEnum lang, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        List<ArticleTypeDTO> dtoList = new ArrayList<>();

        Page<ArticleTypeEntity> entityPage = articleTypeRepository.findByVisible(true, pageable);

        entityPage.forEach(entity -> {
            dtoList.add(toDTO(entity, lang));
        });

        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public ArticleTypeDTO update(Integer id, ArticleTypeDTO dto, Integer pId) {
//        ArticleTypeValidation.isValid(dto);

        ArticleTypeEntity entity = get(id);

        entity.setKey(dto.getKey());
        entity.setNameUz(dto.getNameUz());
        entity.setNameEn(dto.getNameEn());
        entity.setNameRu(dto.getNameRu());
        entity.setProfileId(pId);
        entity.setUpdatedDate(LocalDateTime.now());

        try {
            articleTypeRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique Items!");
        }
        return toDTO(entity);
    }

    public Boolean delete(Integer id) {
        ArticleTypeEntity entity = get(id);

        int n = articleTypeRepository.updateVisible(false, id);
        return n > 0;
    }

    public ArticleTypeEntity get(Integer id) {
        ArticleTypeEntity entity = articleTypeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Not Found {}", id);
                    return new ItemNotFoundException("Not Found!");
                });

        if (!entity.getVisible()) {
            log.warn("Visible false {}", id);
            throw new ItemNotFoundException("Not Found!");
        }

        return entity;
    }

    public ArticleTypeDTO getById(Integer id, LangEnum lang) {
        ArticleTypeEntity entity = get(id);
        return toDTO(entity, lang);
    }

    private ArticleTypeDTO toDTO(ArticleTypeEntity entity, LangEnum lang) {
        ArticleTypeDTO dto = new ArticleTypeDTO();
        dto.setId(entity.getId());
        dto.setKey(entity.getKey());
        switch (lang) {
            case en -> dto.setName(entity.getNameEn());
            case ru -> dto.setName(entity.getNameRu());
            case uz -> dto.setName(entity.getNameUz());
        }
        return dto;
    }

    private ArticleTypeDTO toDTO(ArticleTypeEntity entity) {
        ArticleTypeDTO dto = new ArticleTypeDTO();
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
