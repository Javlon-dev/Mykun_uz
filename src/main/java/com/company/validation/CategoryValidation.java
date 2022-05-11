package com.company.validation;

import com.company.dto.CategoryDTO;
import com.company.exception.AppBadRequestException;

public class CategoryValidation {
    public static void isValid(CategoryDTO dto) {
        if (dto.getNameEn() == null || dto.getNameEn().trim().length() < 2) {
            throw new AppBadRequestException("Name EN not valid");
        }
        if (dto.getNameRu() == null || dto.getNameRu().trim().length() < 2) {
            throw new AppBadRequestException("Name RU not valid");
        }
        if (dto.getNameUz() == null || dto.getNameUz().trim().length() < 2) {
            throw new AppBadRequestException("Name UZ not valid");
        }
    }
}
