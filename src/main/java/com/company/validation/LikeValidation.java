package com.company.validation;

import com.company.dto.LikeDTO;
import com.company.exception.AppBadRequestException;

public class LikeValidation {
    public static void isValid(LikeDTO dto) {
        if (dto.getStatus() == null) {
            throw new AppBadRequestException("Status Not Valid");
        }
        if (dto.getArticleId() == null) {
            throw new AppBadRequestException("Article Id Not Valid");
        }
    }
}
