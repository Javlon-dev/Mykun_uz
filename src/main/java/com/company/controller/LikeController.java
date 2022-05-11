package com.company.controller;

import com.company.dto.LikeDTO;
import com.company.dto.ProfileJwtDTO;
import com.company.enums.ProfileRole;
import com.company.service.LikeService;
import com.company.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.function.Predicate;

@Slf4j
@RestController
@RequestMapping("/like")
public class LikeController {

    @Autowired
    private LikeService likeService;

    /**
     * PUBLIC
     */

    @PostMapping("/public")
    public ResponseEntity<?> create(@RequestBody @Valid LikeDTO dto, HttpServletRequest request) {
        log.info("CREATE {}", dto);
        Integer pId = JwtUtil.getIdFromHeader(request);
        return ResponseEntity.ok(likeService.create(dto, pId));
    }

    @GetMapping("/public/profile/{articleId}")
    public ResponseEntity<?> findByProfile(@PathVariable(value = "articleId") Integer aId, HttpServletRequest request) {
        log.info("/public/profile/{articleId} {}", aId);
        Integer pId = JwtUtil.getIdFromHeader(request);
        return ResponseEntity.ok(likeService.findByArticleId(aId, pId));
    }

    @GetMapping("/public/article/{articleId}")
    public ResponseEntity<?> getListByArticleId(@RequestParam(value = "page", defaultValue = "0") int page,
                                                @RequestParam(value = "size", defaultValue = "5") int size,
                                                @PathVariable(value = "articleId") Integer aId) {
        log.info("/public/article/{articleId} {}", aId);
        return ResponseEntity.ok(likeService.paginationListByArticleId(page, size, aId));
    }

    @DeleteMapping("/public/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id,
                                    HttpServletRequest request) {
        log.info("DELETE BY ID {}", id);
        ProfileJwtDTO dto = JwtUtil.getProfileFromHeader(request);
        return ResponseEntity.ok(likeService.delete(id, dto.getId(), dto.getRole()));
    }

    /**
     * ADMIN
     */

    @GetMapping("/adminjon")
    public ResponseEntity<?> getList(@RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "size", defaultValue = "5") int size,
                                     HttpServletRequest request) {
        log.info("LIST page={} size={}", page, size);
        ProfileJwtDTO dto = JwtUtil.getProfileFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(likeService.paginationList(page, size));
    }

    @GetMapping("/adminjon/profile/{profileId}")
    public ResponseEntity<?> getListByProfileId(@RequestParam(value = "page", defaultValue = "0") int page,
                                                @RequestParam(value = "size", defaultValue = "5") int size,
                                                @PathVariable(value = "profileId") Integer pId,
                                                HttpServletRequest request) {
        log.info("LIST BY PROFILE ID {}", pId);
        ProfileJwtDTO dto = JwtUtil.getProfileFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(likeService.paginationListByProfileId(pId, page, size));
    }
}
