package com.company.service;

import com.company.dto.TagDTO;
import com.company.entity.TagEntity;
import com.company.enums.LangEnum;
import com.company.exception.AppBadRequestException;
import com.company.exception.ItemAlreadyExistsException;
import com.company.exception.ItemNotFoundException;
import com.company.repository.TagRepository;
import com.company.validation.TagValidation;
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
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    @Lazy
    private ProfileService profileService;

    public TagDTO create(TagDTO dto, Integer pId) {
//        TagValidation.isValid(dto);

        Optional<TagEntity> optional = tagRepository.findByKey("#" + dto.getKey());
        if (optional.isPresent()) {
            log.warn("Unique {}", dto);
            throw new ItemAlreadyExistsException("This Tag already used!");
        }

        TagEntity entity = new TagEntity();
        entity.setKey("#" + dto.getKey());
        entity.setNameEn("#" + dto.getNameEn());
        entity.setNameRu("#" + dto.getNameRu());
        entity.setNameUz("#" + dto.getNameUz());
        entity.setProfileId(pId);
        try {
            tagRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique Items!");
        }
        return toDTO(entity);
    }

    public PageImpl<TagDTO> listByLang(LangEnum lang, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        List<TagDTO> dtoList = new ArrayList<>();

        Page<TagEntity> entityPage = tagRepository.findByVisible(true, pageable);

        entityPage.forEach(entity -> {
            dtoList.add(toDTO(entity, lang));
        });

        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public TagDTO update(Integer id, TagDTO dto, Integer pId) {
//        TagValidation.isValid(dto);

        TagEntity entity = get(id);

        entity.setKey("#" + dto.getKey());
        entity.setNameUz("#" + dto.getNameUz());
        entity.setNameEn("#" + dto.getNameEn());
        entity.setNameRu("#" + dto.getNameRu());
        entity.setProfileId(pId);
        entity.setUpdatedDate(LocalDateTime.now());

        try {
            tagRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique Items!");
        }
        return toDTO(entity);
    }

    public Boolean delete(Integer id) {
        TagEntity entity = get(id);

        int n = tagRepository.updateVisible(false, id);
        return n > 0;
    }

    public TagEntity get(Integer id) {
        TagEntity entity = tagRepository.findById(id)
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

    public List<TagDTO> getListById(List<Integer> id, LangEnum lang) {
        List<TagEntity> entityList = tagRepository.findAllById(id);

        List<TagDTO> dtoList = new ArrayList<>();
        entityList.forEach(entity -> {
            dtoList.add(toDTO(entity, lang));
        });

        return dtoList;
    }

    private TagDTO toDTO(TagEntity entity, LangEnum lang) {
        TagDTO dto = new TagDTO();
        dto.setId(entity.getId());
        dto.setKey(entity.getKey());
        switch (lang) {
            case en -> dto.setName(entity.getNameEn());
            case ru -> dto.setName(entity.getNameRu());
            case uz -> dto.setName(entity.getNameUz());
        }
        return dto;
    }

    private TagDTO toDTO(TagEntity entity) {
        TagDTO dto = new TagDTO();
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
