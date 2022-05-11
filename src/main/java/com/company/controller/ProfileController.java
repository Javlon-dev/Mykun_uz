package com.company.controller;

import com.company.dto.AttachDTO;
import com.company.dto.ProfileDTO;
import com.company.enums.ProfileRole;
import com.company.service.ProfileService;
import com.company.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    /**
     * PUBLIC
     */

    @GetMapping("/public/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Integer id) {
        log.info("/public/{id} {}", id);
        return ResponseEntity.ok(profileService.getById(id));
    }

    @PutMapping("/image")
    public ResponseEntity<?> profileImage(@RequestBody @Valid AttachDTO dto, HttpServletRequest request) {
        log.info("/image {}", dto);
        Integer pId = JwtUtil.getIdFromHeader(request);
        return ResponseEntity.ok(profileService.profileImage(dto.getId(), pId));
    }

    /**
     * ADMIN
     */

    @PostMapping("/adminjon")
    public ResponseEntity<?> create(@RequestBody @Valid ProfileDTO dto, HttpServletRequest request) {
        log.info("CREATE {}", dto);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(profileService.create(dto));
    }

    @GetMapping("/adminjon")
    public ResponseEntity<?> getList(@RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "size", defaultValue = "5") int size,
                                     HttpServletRequest request) {
        log.info("LIST page={} size={}", page, size);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(profileService.paginationList(page, size));
    }

    @PutMapping("/adminjon/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Integer id, @RequestBody @Valid ProfileDTO dto,
                                    HttpServletRequest request) {
        log.info("UPDATE {}", dto);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(profileService.update(id, dto));
    }

    @DeleteMapping("/adminjon/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id, HttpServletRequest request) {
        log.info("DELETE BY ID {}", id);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(profileService.delete(id));
    }


}
