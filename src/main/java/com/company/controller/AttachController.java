package com.company.controller;

import com.company.enums.ProfileRole;
import com.company.service.AttachService;
import com.company.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/attach")
public class AttachController {
    @Autowired
    private AttachService attachService;

    /**
     * PUBLIC
     */

    @PostMapping("/upload")
    public ResponseEntity<?> create(@RequestParam MultipartFile file) {
        log.info("/upload");
        return ResponseEntity.ok(attachService.upload(file));
    }

    @GetMapping(value = "/open/{key}", produces = MediaType.ALL_VALUE)
    public byte[] open(@PathVariable("key") String key) {
        log.info("/open/{key} {}", key);
        return attachService.open(key);
    }

    @GetMapping("/download/{key}")
    public ResponseEntity<?> download(@PathVariable("key") String key) {
        log.info("/download/{key} {}", key);
        return attachService.download(key);
    }

    /**
     * ADMIN
     */

    @GetMapping("/adminjon")
    public ResponseEntity<?> list(@RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "3") int size,
                                  HttpServletRequest request) {
        log.info("LIST page={} size={}", page, size);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(attachService.paginationList(page, size));
    }

    @DeleteMapping("/adminjon/{key}")
    public ResponseEntity<?> delete(@PathVariable("key") String key, HttpServletRequest request) {
        log.info("DELETE BY KEY {}", key);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(attachService.delete(key));
    }


//    open image
    /*@GetMapping(value = "/open/{fileName}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] open(@PathVariable("fileName") String fileName) {
        return attachService.open(fileName);

    }*/

}
