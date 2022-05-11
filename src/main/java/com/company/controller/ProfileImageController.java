package com.company.controller;

import com.company.service.ProfileImageService;
import com.company.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/profile_image")
public class ProfileImageController {

    @Autowired
    private ProfileImageService profileImageService;

    /**
     * PUBLIC
     */

    @PostMapping("")
    public ResponseEntity<?> create(@RequestParam MultipartFile file, HttpServletRequest request) {
        Integer pId = JwtUtil.getIdFromHeader(request);
        return ResponseEntity.ok(profileImageService.create(file, pId));
    }

    @PutMapping("/{key}")
    public ResponseEntity<?> update(@RequestParam MultipartFile file,
                                    @PathVariable("key") String key,
                                    HttpServletRequest request) {
        Integer pId = JwtUtil.getIdFromHeader(request);
        return ResponseEntity.ok(profileImageService.update(file, key, pId));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<?> update(@PathVariable("key") String key,
                                    HttpServletRequest request) {
        Integer pId = JwtUtil.getIdFromHeader(request);
        return ResponseEntity.ok(profileImageService.delete(key, pId));
    }
}
