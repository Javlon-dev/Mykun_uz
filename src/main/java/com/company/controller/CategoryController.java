package com.company.controller;

import com.company.dto.CategoryDTO;
import com.company.enums.LangEnum;
import com.company.enums.ProfileRole;
import com.company.service.CategoryService;
import com.company.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * PUBLIC
     */

    @GetMapping("/public")
    public ResponseEntity<?> listByLang(@RequestHeader(name = "Accept-Language", defaultValue = "uz") LangEnum lang) {
        log.info("/public");
        return ResponseEntity.ok(categoryService.listByLang(lang));
    }

    /**
     * ADMIN
     */

    @PostMapping("/adminjon")
    public ResponseEntity<?> create(@RequestBody @Valid CategoryDTO dto, HttpServletRequest request) {
        log.info("CREATE {}", dto);
        Integer pId = JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(categoryService.create(dto, pId));
    }

    @GetMapping("/adminjon")
    public ResponseEntity<?> list(HttpServletRequest request) {
        log.info("LIST");
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(categoryService.list());
    }

    @PutMapping("/adminjon/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Integer id,
                                    @RequestBody @Valid CategoryDTO dto,
                                    HttpServletRequest request) {
        log.info("UPDATE {}", dto);
        Integer pId = JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(categoryService.update(id, dto, pId));
    }

    @DeleteMapping("/adminjon/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id, HttpServletRequest request) {
        log.info("DELETE BY ID {}", id);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(categoryService.delete(id));
    }
}
