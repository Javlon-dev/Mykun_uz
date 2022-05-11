package com.company.service;

import com.company.dto.CommentDTO;
import com.company.entity.ArticleEntity;
import com.company.entity.CommentEntity;
import com.company.entity.ProfileEntity;
import com.company.enums.ArticleStatus;
import com.company.enums.ProfileRole;
import com.company.exception.AppForbiddenException;
import com.company.exception.ItemNotFoundException;
import com.company.repository.CommentRepository;
import com.company.validation.CommentValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    @Lazy
    private ProfileService profileService;

    @Autowired
    private ArticleService articleService;


    public CommentDTO create(CommentDTO dto, Integer pId) {
//        CommentValidation.isValid(dto);

        ArticleEntity articleEntity = articleService.getByIdAndStatus(dto.getArticleId(), ArticleStatus.PUBLISHED);

        CommentEntity entity = new CommentEntity();
        entity.setContent(dto.getContent());
        entity.setArticleId(dto.getArticleId());
        entity.setProfileId(pId);

        commentRepository.save(entity);
        return toDTO(entity);
    }

    public PageImpl<CommentDTO> paginationList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        List<CommentDTO> dtoList = new ArrayList<>();

        Page<CommentEntity> pageList = commentRepository.findByVisible(true, pageable);

        pageList.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });

        return new PageImpl<>(dtoList, pageable, pageList.getTotalElements());
    }

    public PageImpl<CommentDTO> paginationListByArticleId(Integer aId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        List<CommentDTO> dtoList = new ArrayList<>();

        Page<CommentEntity> pageList = commentRepository.findByVisibleAndArticleId(true, pageable, aId);

        pageList.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });

        return new PageImpl<>(dtoList, pageable, pageList.getTotalElements());
    }

    public PageImpl<CommentDTO> paginationListByProfileId(Integer pId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        List<CommentDTO> dtoList = new ArrayList<>();

        Page<CommentEntity> pageList = commentRepository.findByVisibleAndProfileId(true, pageable, pId);

        pageList.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });

        return new PageImpl<>(dtoList, pageable, pageList.getTotalElements());
    }

    public CommentDTO update(Integer id, CommentDTO dto, Integer pId) {
        CommentValidation.isValid(dto);

        ArticleEntity articleEntity = articleService.getByIdAndStatus(dto.getArticleId(), ArticleStatus.PUBLISHED);

        CommentEntity entity = get(id);

        if (!entity.getProfileId().equals(pId)) {
            log.warn("Not access {}", pId);
            throw new AppForbiddenException("Not Access!");
        }

        /** Article Id */
        /*if (!entity.getArticleId().equals(dto.getArticleId())) {
         throw new AppBadRequestException("Not Access!");
         }*/

        entity.setContent(dto.getContent());
        entity.setUpdatedDate(LocalDateTime.now());

        commentRepository.save(entity);
        return toDTO(entity);
    }

    public Boolean delete(Integer id, Integer pId, ProfileRole role) {
        CommentEntity entity = get(id);

        if (entity.getProfileId().equals(pId) || role.equals(ProfileRole.ADMIN)) {
            int n = commentRepository.updateVisible(false, entity.getId());
            return n > 0;
        }
        log.warn("Not access {}", pId);
        throw new AppForbiddenException("Not access!");
    }

    public CommentEntity get(Integer id) {
        CommentEntity entity = commentRepository.findById(id).orElseThrow(() -> {
            log.warn("Not found {}", id);
            return new ItemNotFoundException("Not found!");
        });
        if (!entity.getVisible()) {
            log.warn("Visible false {}", id);
            throw new ItemNotFoundException("Not found!");
        }
        return entity;
    }

    private CommentDTO toDTO(CommentEntity entity) {
        CommentDTO dto = new CommentDTO();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setProfileId(entity.getProfileId());
        dto.setArticleId(entity.getArticleId());
        dto.setUpdatedDate(entity.getUpdatedDate());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }
}
