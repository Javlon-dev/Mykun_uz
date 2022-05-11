package com.company.service;

import com.company.dto.CategoryDTO;
import com.company.entity.CategoryEntity;
import com.company.enums.LangEnum;
import com.company.exception.AppBadRequestException;
import com.company.exception.ItemAlreadyExistsException;
import com.company.exception.ItemNotFoundException;
import com.company.repository.CategoryRepository;
import com.company.validation.CategoryValidation;
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
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    @Lazy
    private ProfileService profileService;

    public CategoryDTO create(CategoryDTO dto, Integer pId) {
//        CategoryValidation.isValid(dto);

        Optional<CategoryEntity> optional = categoryRepository.findByKey(dto.getKey());
        if (optional.isPresent()) {
            log.warn("Unique {}", dto);
            throw new ItemAlreadyExistsException("This Category already used!");
        }

        CategoryEntity entity = new CategoryEntity();
        entity.setKey(dto.getKey());
        entity.setNameEn(dto.getNameEn());
        entity.setNameRu(dto.getNameRu());
        entity.setNameUz(dto.getNameUz());
        entity.setProfileId(pId);

        try {
            categoryRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique Items!");
        }
        return toDTO(entity);
    }

    public List<CategoryDTO> list() {
        List<CategoryDTO> list = new ArrayList<>();
        categoryRepository.findAllByVisible(true).forEach(entity -> {
            list.add(toDTO(entity));
        });
        return list;
    }

    public List<CategoryDTO> listByLang(LangEnum lang) {
        List<CategoryDTO> dtoList = new ArrayList<>();

        categoryRepository.findAllByVisible(true).forEach(entity -> {
            dtoList.add(toDTO(entity, lang));
        });

        return dtoList;
    }

    public CategoryDTO update(Integer id, CategoryDTO dto, Integer pId) {
//        CategoryValidation.isValid(dto);

        CategoryEntity entity = get(id);

        entity.setKey(dto.getKey());
        entity.setNameEn(dto.getNameEn());
        entity.setNameRu(dto.getNameRu());
        entity.setNameUz(dto.getNameUz());
        entity.setProfileId(pId);
        entity.setUpdatedDate(LocalDateTime.now());

        try {
            categoryRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique Items!");
        }
        return toDTO(entity);

    }

    public Boolean delete(Integer id) {
        CategoryEntity entity = get(id);

        int n = categoryRepository.updateVisible(false, id);
        return n > 0;
    }

    public CategoryEntity get(Integer id) {
        CategoryEntity entity = categoryRepository.findById(id)
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

    public CategoryDTO getById(Integer id, LangEnum lang) {
        CategoryEntity entity = get(id);
        return toDTO(entity, lang);
    }

    private CategoryDTO toDTO(CategoryEntity entity, LangEnum lang) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(entity.getId());
        dto.setKey(entity.getKey());
        switch (lang) {
            case en -> dto.setName(entity.getNameEn());
            case ru -> dto.setName(entity.getNameRu());
            case uz -> dto.setName(entity.getNameUz());
        }
        return dto;
    }

    private CategoryDTO toDTO(CategoryEntity entity) {
        CategoryDTO dto = new CategoryDTO();
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
