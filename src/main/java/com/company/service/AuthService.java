package com.company.service;

import com.company.dto.AttachDTO;
import com.company.dto.AuthDTO;
import com.company.dto.ProfileDTO;
import com.company.dto.RegistrationDTO;
import com.company.entity.AttachEntity;
import com.company.entity.ProfileEntity;
import com.company.enums.ProfileRole;
import com.company.enums.ProfileStatus;
import com.company.exception.*;
import com.company.repository.ProfileRepository;
import com.company.util.JwtUtil;
import com.company.validation.RegistrationValidation;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AttachService attachService;

    public ProfileDTO login(AuthDTO dto) {
        ProfileEntity entity = authorization(dto);

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setName(entity.getName());
        profileDTO.setSurname(entity.getSurname());
        profileDTO.setEmail(entity.getEmail());
        profileDTO.setJwt(JwtUtil.encode(entity.getId(), entity.getRole()));

        Optional<AttachEntity> optional = Optional.ofNullable(entity.getAttach());
        if (optional.isPresent()){
            AttachDTO imageDTO = new AttachDTO();
            imageDTO.setUrl(attachService.toOpenUrl(optional.get().getId()));
            profileDTO.setImage(imageDTO);
        }

        return profileDTO;
    }

    public ProfileEntity authorization(AuthDTO dto) {
        ProfileEntity entity = profileRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new ItemNotFoundException("Not found!"));
        if (!entity.getVisible()) {
            throw new ItemNotFoundException("Not Found!");
        }
        if (entity.getStatus().equals(ProfileStatus.BLOCK)){
            throw new AppForbiddenException("You're blocked!\nPlease contact with Admin!");
        }
        if (!entity.getStatus().equals(ProfileStatus.ACTIVE)) {
            throw new AppForbiddenException("No Access bratiwka!");
        }
        String password = DigestUtils.md5Hex(dto.getPassword());
        if (!entity.getPassword().equals(password)) {
            throw new AppBadRequestException("Invalid Password!");
        }
        return entity;
    }

    public String registration(RegistrationDTO dto) {
        RegistrationValidation.isValid(dto);

        Optional<ProfileEntity> optional = profileRepository.findByEmail(dto.getEmail());
        ProfileEntity entity;
        if (optional.isPresent()) {
            entity = optional.get();

            if (!entity.getStatus().equals(ProfileStatus.NOT_ACTIVE)) {
                throw new ItemAlreadyExistsException("This Email already used!");
            }

        } else {
            String password = DigestUtils.md5Hex(dto.getPassword());

            entity = new ProfileEntity();
            entity.setName(dto.getName());
            entity.setSurname(dto.getSurname());
            entity.setEmail(dto.getEmail());
            entity.setPassword(password);
            entity.setStatus(ProfileStatus.NOT_ACTIVE);
            entity.setRole(ProfileRole.USER);

            profileRepository.save(entity);
        }

        Thread thread = new Thread(() -> sendVerificationEmail(entity));
        thread.start();

        return "Confirm your email address.\nCheck your email!";
    }

    public String verification(Integer id) {
        if (profileRepository.updateStatus(ProfileStatus.ACTIVE, id) > 0) {
            return "Successfully verified";
        }
        throw new AppNotAcceptableException("Unsuccessfully verified!");
    }

    private void sendVerificationEmail(ProfileEntity entity) {
        StringBuilder builder = new StringBuilder();
        builder.append("<h2>Hellomaleykum ").append(entity.getName()).append(" ").append(entity.getSurname()).append("!</h2>");
        builder.append("<br><p><b>To verify your registration click to next link -> ");
        builder.append("<a href=\"http://localhost:8080/auth/verification/")
                .append(JwtUtil.encode(entity.getId()))
                .append("\">This Link</a></b></p></br>");
        builder.append("<br>Mazgi !</br>");
        emailService.send(entity.getEmail(), "Active Your Registration", builder.toString());
    }
}
