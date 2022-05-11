package com.company.service;

import com.company.dto.EmailDTO;
import com.company.entity.EmailEntity;
import com.company.enums.EmailType;
import com.company.exception.AppBadRequestException;
import com.company.exception.ItemNotFoundException;
import com.company.repository.EmailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EmailRepository emailRepository;

    public void send(String toEmail, String title, String content) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            message.setSubject(title);
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setText(content, true);
            javaMailSender.send(message);

            EmailEntity entity = new EmailEntity();
            entity.setToEmail(toEmail);
            entity.setType(EmailType.VERIFICATION);
            emailRepository.save(entity);

        } catch (MessagingException | MailException e) {
            log.error("Mail not send {}", e.getMessage());
            throw new AppBadRequestException(e.getMessage());
        }

        /**
         * without html tags
         */
        /*SimpleMailMessage simple = new SimpleMailMessage();
        simple.setTo(toEmail);
        simple.setSubject(title);
        simple.setText(content);
        javaMailSender.send(simple);*/
    }

    public PageImpl<EmailDTO> paginationList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        List<EmailDTO> dtoList = new ArrayList<>();
        Page<EmailEntity> entityPage = emailRepository.findAll(pageable);
        entityPage.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });

        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public Boolean delete(Integer id) {
        emailRepository.findById(id).orElseThrow(() -> {
            log.warn("Email not delete {}", id);
            return new ItemNotFoundException("Not found!");
        });

        emailRepository.deleteById(id);
        return true;
    }

    private EmailDTO toDTO(EmailEntity entity) {
        EmailDTO dto = new EmailDTO();
        dto.setId(entity.getId());
        dto.setToEmail(entity.getToEmail());
        dto.setType(entity.getType());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }
}
