package com.company.validation;

import com.company.dto.RegionDTO;
import com.company.exception.AppBadRequestException;

public class RegionValidation {
    public static void isValid(RegionDTO dto) {
        if (dto.getNameUz() == null || dto.getNameUz().trim().length() < 2) {
            throw new AppBadRequestException("Name UZ not valid");
        }
        if (dto.getNameRu() == null || dto.getNameRu().trim().length() < 2) {
            throw new AppBadRequestException("Name RU not valid");
        }
        if (dto.getNameEn() == null || dto.getNameEn().trim().length() < 2) {
            throw new AppBadRequestException("Name EN not valid");
        }
        if (dto.getKey() == null || dto.getKey().trim().length() < 2) {
            throw new AppBadRequestException("Key not valid");
        }
    }
}
