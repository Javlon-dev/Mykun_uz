package com.company.service;

import com.company.dto.AttachDTO;
import com.company.entity.AttachEntity;
import com.company.exception.AppBadRequestException;
import com.company.exception.ItemNotFoundException;
import com.company.repository.AttachRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AttachService {

    @Autowired
    private AttachRepository attachRepository;
    @Value("${attach.upload.folder}")
    private String attachFolder;

    @Value("${server.domain.name}")
    private String domainName;

    public AttachDTO upload(MultipartFile file) {
        String pathFolder = getDateFolder();

        File folder = new File(attachFolder + "/" + pathFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String key = UUID.randomUUID().toString();
        String extension;


        try {
            extension = getExtension(file.getOriginalFilename());

            byte[] bytes = file.getBytes();
            Path url = Paths.get(folder.getAbsolutePath() + "/" + key + "." + extension);
            Files.write(url, bytes);

            return toDTO(saveAttach(key, pathFolder, extension, file));
        } catch (IOException | RuntimeException e) {
            log.warn("Cannot Upload");
            throw new AppBadRequestException(e.getMessage());
        }
    }

    public byte[] open(String key) {
        byte[] data;

        AttachEntity entity = getByKey(key);
        String pathFolder = entity.getPath() + "/" + key + "." + entity.getExtension();

        try {
            Path path = Paths.get(attachFolder + "/" + pathFolder);
            data = Files.readAllBytes(path);
            return data;
        } catch (IOException e) {
            log.warn("Cannot Open");
            return new byte[0];
        }
    }

    public ResponseEntity<?> download(String key) {
        try {
            AttachEntity entity = getByKey(key);
            String path = entity.getPath() + "/" + key + "." + entity.getExtension();

            Path file = Paths.get(attachFolder + "/" + path);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + entity.getOriginalName() + "\"")
                        .body(resource);
            } else {
                log.warn("Cannot Read");
                throw new AppBadRequestException("Could not read the file!");
            }

        } catch (MalformedURLException e) {
            log.warn("Cannot Download");
            throw new AppBadRequestException("Error" + e.getMessage());
        }
    }

    public String getDateFolder() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DATE);

        return year + "/" + month + "/" + day;
    }

    public String getExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        return fileName.substring(lastIndex + 1);
    }

    public AttachEntity getByKey(String key) {
        return attachRepository.findById(key).orElseThrow(() -> {
            log.warn("Not found {}", key);
            return new ItemNotFoundException("Attach not found!");
        });
    }

    public AttachEntity saveAttach(String key, String pathFolder, String extension, MultipartFile file) {
        AttachEntity entity = new AttachEntity();
        entity.setId(key);
        entity.setPath(pathFolder);
        entity.setOriginalName(file.getOriginalFilename());
        entity.setExtension(extension);
        entity.setFileSize(file.getSize());
        attachRepository.save(entity);
        return entity;
    }

    public String toOpenUrl(String key) {
        return domainName + "/attach/open/" + key;

    }

    public AttachDTO toOpenURLDTO(String key) {
        return new AttachDTO(domainName + "/attach/open/" + key);

    }

    public AttachDTO toDTO(AttachEntity entity) {
        AttachDTO dto = new AttachDTO();
        dto.setId(entity.getId());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setOriginalName(entity.getOriginalName());
        dto.setPath(entity.getPath());
        dto.setUrl(domainName + "/attach/download/" + entity.getId());
        return dto;
    }

    public Boolean delete(String key) {
        AttachEntity entity = getByKey(key);

        File file = new File(attachFolder + "/" + entity.getPath() +
                "/" + entity.getId() + "." + entity.getExtension());

        if (file.delete()) {
            attachRepository.deleteById(key);
            return true;
        } else {
            log.warn("Cannot Read");
            throw new AppBadRequestException("Could not read the file!");
        }

    }

    public PageImpl<AttachDTO> paginationList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        List<AttachDTO> dtoList = new ArrayList<>();

        Page<AttachEntity> entityPage = attachRepository.findAll(pageable);
        entityPage.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });

        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }


    /**
     * simple upload
     */
    /*public String simple_upload(MultipartFile file) {
        try {
            if (!attachFolder           attachFolder    }

            byte[] bytes = file.getBytes();
            Path path = Paths.get(attachFolder + "/" + file.getOriginalFilename());
            Files.write(path, bytes);
            return file.getOriginalFilename();
        } catch (IOException e) {
            throw new AppBadRequestException(e.getMessage());
        }
    }*/


    /**
     * open general
     */
    /*public byte[] open_general_simple(String fileName) {
        byte[] data;

        try {
            Path path = Paths.get(attachFolder + "/" + fileName);
            data = Files.readAllBytes(path);
            return data;
        } catch (IOException e) {
            return new byte[0];
        }
    }*/


    /**
     * open image
     */
    /* public byte[] open(String fileName) {
        byte[] imageInByte;
        BufferedImage originalImage;

        try {
            originalImage = ImageIO.read(new File(attachFolder + "/" + fileName));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "png", baos);

            baos.flush();
            imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException e) {
            return new byte[0];
        }
    }*/
}
