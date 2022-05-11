package com.company.service;

import com.company.dto.LikeDTO;
import com.company.entity.ArticleEntity;
import com.company.entity.LikeEntity;
import com.company.entity.ProfileEntity;
import com.company.enums.ArticleStatus;
import com.company.enums.ProfileRole;
import com.company.exception.AppForbiddenException;
import com.company.exception.ItemNotFoundException;
import com.company.mapper.LikeCountSimpleMapper;
import com.company.repository.LikeRepository;
import com.company.validation.LikeValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private ProfileService profileService;

    @Autowired
    @Lazy
    private ArticleService articleService;


    public LikeDTO create(LikeDTO dto, Integer pId) {
//        LikeValidation.isValid(dto);

        ProfileEntity profileEntity = profileService.get(pId);

        ArticleEntity articleEntity = articleService.getByIdAndStatus(dto.getArticleId(), ArticleStatus.PUBLISHED);

        Optional<LikeEntity> oldLikeOptional = likeRepository.findByArticleIdAndProfileId(dto.getArticleId(), pId);

        if (oldLikeOptional.isPresent()) {
            oldLikeOptional.get().setStatus(dto.getStatus());
            likeRepository.save(oldLikeOptional.get());
            return toDTO(oldLikeOptional.get());
        }

        LikeEntity entity = new LikeEntity();
        entity.setStatus(dto.getStatus());
        entity.setArticleId(dto.getArticleId());
        entity.setProfileId(pId);

        likeRepository.save(entity);
        return toDTO(entity);
    }

    public LikeDTO findByArticleId(Integer aId, Integer pId) {
        LikeEntity oldLike = likeRepository.findByArticleIdAndProfileId(aId, pId).orElseThrow(() -> new ItemNotFoundException("Not found!"));
        if (!oldLike.getVisible()) {
            return new LikeDTO();
        }
        return toDTO(oldLike);
    }

    public PageImpl<LikeDTO> paginationList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        List<LikeDTO> dtoList = new ArrayList<>();

        Page<LikeEntity> pageList = likeRepository.findByVisible(true, pageable);

        pageList.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });

        return new PageImpl<>(dtoList, pageable, pageList.getTotalElements());
    }

    public PageImpl<LikeDTO> paginationListByArticleId(int page, int size, Integer aId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        List<LikeDTO> dtoList = new ArrayList<>();

        Page<LikeEntity> pageList = likeRepository.findByVisibleAndArticleId(true, pageable, aId);

        pageList.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });

        return new PageImpl<>(dtoList, pageable, pageList.getTotalElements());
    }

    public PageImpl<LikeDTO> paginationListByProfileId(Integer pId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        List<LikeDTO> dtoList = new ArrayList<>();

        Page<LikeEntity> pageList = likeRepository.findByVisibleAndProfileId(true, pageable, pId);

        pageList.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });

        return new PageImpl<>(dtoList, pageable, pageList.getTotalElements());
    }

    /**
     * Like Update
     */
    /*public LikeDTO update(Integer id, LikeDTO dto, Integer pId) {
        ProfileEntity profileEntity = profileService.get(pId);

        ArticleEntity articleEntity = articleService.get(dto.getArticleId());

        LikeEntity entity = get(id);

        if (!entity.getProfileId().equals(pId)) {
            throw new AppForbiddenException("Not access!");
        }

        entity.setStatus(dto.getStatus());

        likeRepository.save(entity);
        return toDTO(entity);
    }*/
    public Boolean delete(Integer id, Integer pId, ProfileRole role) {
        LikeEntity entity = get(id);

        if (entity.getProfileId().equals(pId) || role.equals(ProfileRole.ADMIN)) {
            int n = likeRepository.updateVisible(false, entity.getId());
            return n > 0;
        }
        log.warn("Not access {}", pId);
        throw new AppForbiddenException("Not access!");
    }

    public LikeEntity get(Integer id) {
        LikeEntity entity = likeRepository.findById(id).orElseThrow(() -> {
            log.warn("Not found {}", id);
            return new ItemNotFoundException("Not found!");
        });
        if (!entity.getVisible()) {
            log.warn("Visible false {}", id);
            throw new ItemNotFoundException("Not found!");
        }
        return entity;
    }

    public LikeDTO getLikeCountByArticleId(Integer aId) {
        LikeCountSimpleMapper mapper = likeRepository.getLikeCountByArticleId(aId);
        LikeDTO dto = new LikeDTO();
        dto.setLikeCount(mapper.getLike_count());
        dto.setDislikeCount(mapper.getDislike_count());
        return dto;
    }

    private LikeDTO toDTO(LikeEntity entity) {
        LikeDTO dto = new LikeDTO();
        dto.setId(entity.getId());
        dto.setStatus(entity.getStatus());
        dto.setProfileId(entity.getProfileId());
        dto.setArticleId(entity.getArticleId());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }
}
