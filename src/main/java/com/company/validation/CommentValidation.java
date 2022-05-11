package com.company.validation;

import com.company.dto.CommentDTO;
import com.company.exception.AppBadRequestException;

public class CommentValidation {
    public static void isValid(CommentDTO dto) {
        if (dto.getContent().trim().length() < 1 || dto.getContent() == null){
            throw new AppBadRequestException("Content Not Valid");
        }
    }
}
