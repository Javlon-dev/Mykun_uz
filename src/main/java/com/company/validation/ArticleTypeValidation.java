package com.company.validation;

import com.company.dto.ArticleTypeDTO;
import com.company.exception.AppBadRequestException;

public class ArticleTypeValidation {
    public static void isValid(ArticleTypeDTO dto) {
        if (dto.getNameUz().trim().length() < 3 || dto.getNameUz() == null){
            throw new AppBadRequestException("Name Uz Not Valid");
        }
        if (dto.getNameEn().trim().length() < 3 || dto.getNameEn() == null){
            throw new AppBadRequestException("Name En Not Valid");
        }
        if (dto.getNameRu().trim().length() < 3 || dto.getNameRu() == null){
            throw new AppBadRequestException("Name Ru Not Valid");
        }
        if (dto.getKey().trim().length() < 3 || dto.getKey() == null){
            throw new AppBadRequestException("Key Not Valid");
        }
    }
}
