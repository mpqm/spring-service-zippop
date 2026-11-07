package com.fiiiiive.zippop.global.web;

import com.fiiiiive.zippop.global.base.InputValidationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.time.LocalDate;

public final class RequestValidation {
    private RequestValidation() {}

    public static PageRequest page(int page, int size, Sort sort) {
        if (page < 0 || size < 1 || size > 100) {
            throw new InputValidationException("page는 0 이상, size는 1~100이어야 합니다.");
        }
        return PageRequest.of(page, size, sort);
    }

    public static <E extends Enum<E>> E enumValue(Class<E> type, String value) {
        try { return Enum.valueOf(type, value); }
        catch (IllegalArgumentException | NullPointerException ex) {
            throw new InputValidationException("올바른 상태 값을 입력해주세요.");
        }
    }
}
