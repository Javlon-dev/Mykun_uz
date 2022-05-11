package com.company.controller;

import com.company.dto.TagDTO;
import com.company.enums.LangEnum;
import com.company.enums.ProfileRole;
import com.company.service.TagService;
import com.company.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * PUBLIC
     */

    @GetMapping("/public")
    public ResponseEntity<?> listByLang(@RequestHeader(name = "Accept-Language", defaultValue = "uz") LangEnum lang,
                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "size", defaultValue = "5") int size) {
        log.info("/public page={} size={}", page, size);
        return ResponseEntity.ok(tagService.listByLang(lang, page, size));
    }

    /**
     * ADMIN
     */

    @PostMapping("/adminjon")
    public ResponseEntity<?> create(@RequestBody @Valid TagDTO dto, HttpServletRequest request) {
        log.info("CREATE {}", dto);
        return ResponseEntity.ok(tagService.create(dto, JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN)));
    }

    @PutMapping("/adminjon/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Integer id,
                                    @RequestBody @Valid TagDTO dto,
                                    HttpServletRequest request) {
        log.info("UPDATE {}", dto);
        return ResponseEntity.ok(tagService.update(id, dto, JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN)));
    }

    @DeleteMapping("/adminjon/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id, HttpServletRequest request) {
        log.info("DELETE BY ID {}", id);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(tagService.delete(id));
    }
}
