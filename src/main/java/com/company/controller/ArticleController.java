package com.company.controller;

import com.company.dto.ArticleDTO;
import com.company.enums.ArticleStatus;
import com.company.enums.LangEnum;
import com.company.enums.ProfileRole;
import com.company.service.ArticleService;
import com.company.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    /**
     * PUBLIC
     */

    @GetMapping("/public/list")
    public ResponseEntity<?> listByLang(@RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "size", defaultValue = "5") int size) {
        log.info("/public/list page={} size={}", page, size);
        return ResponseEntity.ok(articleService.list(page, size));
    }

    @GetMapping("/public/type/{typeId}/5")
    public ResponseEntity<?> getByType(@PathVariable("typeId") Integer typeId) {
        log.info("/public/type/{typeId}/5 {}", typeId);
        return ResponseEntity.ok(articleService.getTop5ByTypeId(typeId));
    }


    @GetMapping("/public/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Integer articleId,
                                     @RequestHeader(name = "Accept-Language", defaultValue = "uz") LangEnum lang) {
        log.info("/public/{id} {}", articleId);
        return ResponseEntity.ok(articleService.getByIdPublished(articleId, lang));
    }

    @GetMapping("/public/region/{regionId}")
    public ResponseEntity<?> publishedListByRegionId(@RequestParam(value = "page", defaultValue = "0") int page,
                                                     @RequestParam(value = "size", defaultValue = "5") int size,
                                                     @PathVariable("regionId") Integer rId) {
        log.info("/public/region/{regionId} {}", rId);
        return ResponseEntity.ok(articleService.publishedListByRegion(rId, page, size));
    }

    @GetMapping("/public/category/{categoryId}")
    public ResponseEntity<?> publishedListByCategoryId(@RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "size", defaultValue = "5") int size,
                                                       @PathVariable("categoryId") Integer cId) {
        log.info("/public/category/{categoryId} {}", cId);
        return ResponseEntity.ok(articleService.publishedListByCategoryId(cId, page, size));
    }

    @GetMapping("/public/type/{typeId}")
    public ResponseEntity<?> publishedListByTypeId(@RequestParam(value = "page", defaultValue = "0") int page,
                                                   @RequestParam(value = "size", defaultValue = "5") int size,
                                                   @PathVariable("typeId") Integer tId) {
        log.info("/public/type/{typeId} {}", tId);
        return ResponseEntity.ok(articleService.publishedListByTypeId(tId, page, size));
    }

    @GetMapping("/public/last")
    public ResponseEntity<?> getLastAdded4Article() {
        log.info("/public/last");
        return ResponseEntity.ok(articleService.last4());
    }

    @GetMapping("/public/region/{regionId}/4")
    public ResponseEntity<?> top4ByRegionId(@PathVariable("regionId") Integer rId) {
        log.info("/public/region/{regionId}/4 {}", rId);
        return ResponseEntity.ok(articleService.top4ByRegionId(rId));
    }

    @GetMapping("/public/category/{categoryId}/4")
    public ResponseEntity<?> top4ByCategoryId(@PathVariable("categoryId") Integer cId) {
        log.info("/public/category/{categoryId}/4 {}", cId);
        return ResponseEntity.ok(articleService.top4ByCategoryId(cId));
    }

    @GetMapping("/public/share/{id}")
    public ResponseEntity<?> generateShareLink(@RequestHeader(name = "Accept-Language", defaultValue = "uz") LangEnum lang,
                                               @PathVariable("id") Integer id) {
        log.info("/public/share/{id} {}", id);
        return ResponseEntity.ok(articleService.getShared(lang, id));
    }

    @GetMapping("/public/view/{id}")
    public ResponseEntity<?> increaseViewCount(@PathVariable("id") Integer id) {
        log.info("/public/view/{id} {}", id);
        articleService.updateViewCount(id);
        return ResponseEntity.ok().build();
    }

    /**
     * ADMIN
     */

    @PostMapping("/adminjon")
    public ResponseEntity<?> create(@RequestBody @Valid ArticleDTO dto, HttpServletRequest request) {
        log.info("CREATE {}", dto);
        Integer pId = JwtUtil.getIdFromHeader(request, ProfileRole.MODERATOR);
        return ResponseEntity.ok(articleService.create(dto, pId));
    }


    @PutMapping("/adminjon/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Integer id,
                                    @RequestBody @Valid ArticleDTO dto,
                                    HttpServletRequest request) {
        log.info("UPDATE {}", dto);
        Integer pId = JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(articleService.update(id, dto, pId));
    }

    @PutMapping("/adminjon/{aId}/status")
    public ResponseEntity<?> changeStatus(@PathVariable("aId") Integer aId,
                                          @RequestParam ArticleStatus status,
                                          HttpServletRequest request) {
        log.info("CHANGE STATUS {}", status);
        Integer pId = JwtUtil.getIdFromHeader(request, ProfileRole.PUBLISHER, ProfileRole.MODERATOR);
        return ResponseEntity.ok(articleService.changeStatus(aId, status));
    }

    @GetMapping("/adminjon/{id}")
    public ResponseEntity<?> getByIdAsAdmin(@PathVariable("id") Integer articleId,
                                            @RequestHeader(name = "Accept-Language", defaultValue = "uz") LangEnum lang) {
        log.info("GET BY ID {}", articleId);
        return ResponseEntity.ok(articleService.getByIdAdAdmin(articleId, lang));
    }

    @DeleteMapping("/adminjon/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id, HttpServletRequest request) {
        log.info("DELETE BY ID {}", id);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(articleService.delete(id));
    }
}
