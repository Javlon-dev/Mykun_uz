package com.company.controller;

import com.company.dto.CommentDTO;
import com.company.dto.ProfileJwtDTO;
import com.company.enums.ProfileRole;
import com.company.service.CommentService;
import com.company.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * PUBLIC
     */

    @PostMapping("/public")
    public ResponseEntity<?> create(@RequestBody @Valid CommentDTO dto, HttpServletRequest request) {
        log.info("CREATE {}", dto);
        Integer pId = JwtUtil.getIdFromHeader(request);
        return ResponseEntity.ok(commentService.create(dto, pId));
    }

    @GetMapping("/article/{articleId}")
    public ResponseEntity<?> getListByArticleId(@RequestParam(value = "page", defaultValue = "0") int page,
                                                @RequestParam(value = "size", defaultValue = "5") int size,
                                                @PathVariable(value = "articleId") Integer aId) {
        log.info("/article/{articleId} {}", aId);
        return ResponseEntity.ok(commentService.paginationListByArticleId(aId, page, size));
    }

    @PutMapping("/public/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Integer id,
                                    @RequestBody @Valid CommentDTO dto,
                                    HttpServletRequest request) {
        log.info("UPDATE {}", dto);
        Integer pId = JwtUtil.getIdFromHeader(request);
        return ResponseEntity.ok(commentService.update(id, dto, pId));
    }

    @DeleteMapping("/public/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id, HttpServletRequest request) {
        log.info("DELETE BY ID {}", id);
        ProfileJwtDTO dto = JwtUtil.getProfileFromHeader(request);
        Integer pId = dto.getId();
        ProfileRole role = dto.getRole();
        return ResponseEntity.ok(commentService.delete(id, pId, role));
    }

    /**
     * ADMIN
     */

    @GetMapping("/adminjon/profile/{profileId}")
    public ResponseEntity<?> getListByProfileId(@RequestParam(value = "page", defaultValue = "0") int page,
                                                @RequestParam(value = "size", defaultValue = "5") int size,
                                                @PathVariable(value = "profileId") Integer pId,
                                                HttpServletRequest request) {
        log.info("LIST BY PROFILE ID {}", pId);
        Integer adminId = JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(commentService.paginationListByProfileId(pId, page, size));
    }

    @GetMapping("/adminjon")
    public ResponseEntity<?> getList(@RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "size", defaultValue = "5") int size,
                                     HttpServletRequest request) {
        log.info("LIST page={} size={}", page, size);
        Integer pId = JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(commentService.paginationList(page, size));
    }
}
