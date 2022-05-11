package com.company.service;

import com.company.dto.ArticleDTO;
import com.company.entity.ArticleEntity;
import com.company.entity.ProfileEntity;
import com.company.enums.ArticleStatus;
import com.company.enums.LangEnum;
import com.company.exception.AppBadRequestException;
import com.company.exception.ItemAlreadyExistsException;
import com.company.exception.ItemNotFoundException;
import com.company.mapper.ArticleSimpleMapper;
import com.company.repository.ArticleRepository;
import com.company.validation.ArticleValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ArticleService {
    @Value("${server.domain.name}")
    private String domainName;

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private AttachService attachService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RegionService regionService;
    @Autowired
    private ArticleTypeService articleTypeService;
    @Autowired
    private TagService tagService;


    public ArticleDTO create(ArticleDTO dto, Integer pId) {
//        ArticleValidation.isValid(dto); // validation

        Optional<ArticleEntity> optional = articleRepository.findByTitle(dto.getTitle());
        if (optional.isPresent()) {
            log.warn("Unique {}", dto);
            throw new ItemAlreadyExistsException("This Article already used!");
        }

        ArticleEntity entity = new ArticleEntity();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setContent(dto.getContent());
        entity.setProfileId(pId);

        entity.setStatus(ArticleStatus.CREATED);

        entity.setAttachId(dto.getAttachId());
        entity.setCategoryId(dto.getCategoryId());
        entity.setRegionId(dto.getRegionId());
        entity.setTypeId(dto.getTypeId());
        entity.setTagList(dto.getTagList());

        articleRepository.save(entity);
        return toDTO(entity);
    }

    public List<ArticleDTO> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        List<ArticleDTO> dtoList = new ArrayList<>();

        articleRepository.findByVisibleAndStatus(true, pageable, ArticleStatus.PUBLISHED).forEach(entity -> {
            dtoList.add(toDTO(entity));
        });

        return dtoList;
    }

    public ArticleDTO update(Integer id, ArticleDTO dto, Integer pId) {
//        ArticleValidation.isValid(dto); // validation

        ArticleEntity entity = get(id);

        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setContent(dto.getContent());
        entity.setProfileId(pId);
        entity.setUpdatedDate(LocalDateTime.now());

        entity.setAttachId(dto.getAttachId());
        entity.setCategoryId(dto.getCategoryId());
        entity.setRegionId(dto.getRegionId());
        entity.setTypeId(dto.getTypeId());
        entity.setTagList(dto.getTagList());

        articleRepository.save(entity);
        return toDTO(entity);
    }

    public Boolean delete(Integer id) {
        ArticleEntity entity = articleRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Not found {}", id);
                    return new ItemNotFoundException("Not Found!");
                });

        if (!entity.getVisible()) {
            log.warn("Visible false {}", id);
            throw new ItemNotFoundException("Not Found!");
        }

        int n = articleRepository.updateVisible(false, id);
        return n > 0;
    }

    public List<ArticleDTO> getTop5ByTypeId(Integer typeId) {
        List<ArticleSimpleMapper> entityList = articleRepository.getTypeId(typeId, ArticleStatus.PUBLISHED.name());

        List<ArticleDTO> dtoList = new LinkedList<>();
        entityList.forEach(entity -> {
            ArticleDTO dto = new ArticleDTO();
            dto.setId(entity.getId());
            dto.setTitle(entity.getTitle());
            dto.setDescription(entity.getDescription());
            dto.setPublishedDate(entity.getPublished_date());

            dto.setImage(attachService.toOpenURLDTO(entity.getAttach_id()));
            dtoList.add(dto);
        });
        return dtoList;
    }

    public ArticleDTO getByIdPublished(Integer articleId, LangEnum lang) {
        Optional<ArticleEntity> optional = articleRepository.findByIdAndStatus(articleId, ArticleStatus.PUBLISHED);
        if (optional.isEmpty()) {
            log.warn("Not found {}", articleId);
            throw new ItemNotFoundException("Item not found");
        }
        return toDetailDTO(optional.get(), lang);
    }

    public ArticleDTO getByIdAdAdmin(Integer articleId, LangEnum lang) {
        Optional<ArticleEntity> optional = articleRepository.findById(articleId);
        if (optional.isEmpty()) {
            log.warn("Not found {}", articleId);
            throw new ItemNotFoundException("Item not found");
        }

        return toDetailDTO(optional.get(), lang);
    }

    public PageImpl<ArticleDTO> publishedListByRegion(Integer regionId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        List<ArticleDTO> dtoList = new ArrayList<>();

        Page<ArticleEntity> entityPage = articleRepository.findByRegionIdAndStatus(regionId, pageable, ArticleStatus.PUBLISHED);
        entityPage.stream().forEach(entity -> {
            dtoList.add(toSimpleDTO(entity));
        });

        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());

    }

    public PageImpl<ArticleDTO> publishedListByCategoryId(Integer cId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        List<ArticleDTO> dtoList = new ArrayList<>();

        Page<ArticleEntity> entityPage = articleRepository.findByCategoryIdAndStatus(cId, pageable, ArticleStatus.PUBLISHED);
        entityPage.stream().forEach(entity -> {
            dtoList.add(toSimpleDTO(entity));
        });

        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public PageImpl<ArticleDTO> publishedListByTypeId(Integer tId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        List<ArticleDTO> dtoList = new ArrayList<>();

        Page<ArticleEntity> entityPage = articleRepository.findByTypeIdAndStatus(tId, pageable, ArticleStatus.PUBLISHED);
        entityPage.stream().forEach(entity -> {
            dtoList.add(toSimpleDTO(entity));
        });

        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public List<ArticleDTO> last4() {
        List<ArticleDTO> dtoList = new ArrayList<>();

        List<ArticleSimpleMapper> entityPage = articleRepository.getLast4(ArticleStatus.PUBLISHED.name());
        entityPage.forEach(entity -> {
            dtoList.add(toSimpleDTO(entity));
        });

        return dtoList;
    }

    public List<ArticleDTO> top4ByRegionId(Integer rId) {
//        Pageable pageable = PageRequest.of(0, 4, Sort.by(Sort.Direction.DESC, "createdDate"));

        List<ArticleDTO> dtoList = new ArrayList<>();

        List<ArticleSimpleMapper> entityPage = articleRepository.getByRegionIdLast4(rId, ArticleStatus.PUBLISHED.name());
        entityPage.forEach(entity -> {
            dtoList.add(toSimpleDTO(entity));
        });

        return dtoList;
    }

    public List<ArticleDTO> top4ByCategoryId(Integer cId) {
        List<ArticleDTO> dtoList = new ArrayList<>();

        List<ArticleSimpleMapper> entityPage = articleRepository.getByCategoryIdLast4(cId, ArticleStatus.PUBLISHED.name());
        entityPage.forEach(entity -> {
            dtoList.add(toSimpleDTO(entity));
        });

        return dtoList;
    }

    public Boolean changeStatus(Integer aId, ArticleStatus status) {
        ArticleEntity entity = get(aId);
        try {
            if (entity.getStatus().equals(status)) {
                return false;
            }
            entity.setStatus(status);
            return articleRepository.updateStatus(status, aId) > 0;

        } catch (RuntimeException e) {
            log.warn("Status incorrect {}", status);
            throw new AppBadRequestException("Status not valid!");
        }
    }

    public String getShared(LangEnum lang, Integer id) {
        ArticleEntity article = getByIdAndStatus(id, ArticleStatus.PUBLISHED);
        articleRepository.updateSharedCount(id);
        return domainName + "/article/public/" + id + "/" + lang;
    }

    public void updateViewCount(Integer articleId) {
        ArticleEntity entity = getByIdAndStatus(articleId, ArticleStatus.PUBLISHED);
        articleRepository.updateViewCount(articleId);
    }

    public ArticleDTO toDetailDTO(ArticleEntity entity, LangEnum lang) {
        ArticleDTO dto = toDTO(entity);

        dto.setViewCount(entity.getViewCount());
        dto.setSharedCount(entity.getSharedCount());

        dto.setLike(likeService.getLikeCountByArticleId(entity.getId()));  // like
        dto.setCategory(categoryService.getById(entity.getCategoryId(), lang)); // category
        dto.setRegion(regionService.getById(entity.getRegionId(), lang)); // region
        dto.setType(articleTypeService.getById(entity.getTypeId(), lang)); // type
        dto.setTags(tagService.getListById(entity.getTagList(), lang)); // tag

        return dto;
    }

    private ArticleDTO toSimpleDTO(ArticleSimpleMapper entity) {
        ArticleDTO dto = new ArticleDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setPublishedDate(entity.getPublished_date());

        dto.setImage(attachService.toOpenURLDTO(entity.getAttach_id()));

        return dto;
    }

    private ArticleDTO toSimpleDTO(ArticleEntity entity) {
        ArticleDTO dto = new ArticleDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setPublishedDate(entity.getPublishedDate());

        dto.setImage(attachService.toOpenURLDTO(entity.getAttachId()));

        return dto;
    }

    private ArticleDTO toDTO(ArticleEntity entity) {
        ArticleDTO dto = new ArticleDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setContent(entity.getContent());

        dto.setProfileId(entity.getProfileId());

        dto.setUpdatedDate(entity.getUpdatedDate());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setPublishedDate(entity.getPublishedDate());

        dto.setImage(attachService.toOpenURLDTO(entity.getAttachId()));

        return dto;
    }

    public ArticleEntity get(Integer id) {
        ArticleEntity entity = articleRepository.findById(id)
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

    public ArticleEntity getByIdAndStatus(Integer id, ArticleStatus status) {
        ArticleEntity entity = articleRepository.findByIdAndStatus(id, status)
                .orElseThrow(() -> {
                    log.warn("Not found {}", id);
                    return new ItemNotFoundException("Article Not Found!");
                });

        if (!entity.getVisible()) {
            log.warn("Visible false {}", id);
            throw new ItemNotFoundException("Article Not Found!");
        }

        return entity;
    }
}
