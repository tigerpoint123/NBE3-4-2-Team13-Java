package com.app.backend.global.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@JsonInclude(Include.NON_NULL)
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    @NonNull
    private final Boolean isSuccess;
    @NonNull
    private final String  code;
    @NonNull
    private final String  message;
    private final T       data;

    public static <T> ApiResponse<T> of(@NonNull final Boolean isSuccess,
                                        @NonNull final String code,
                                        @NonNull final String message) {
        return new ApiResponse<>(isSuccess, code, message, null);
    }

    public static <T> ApiResponse<T> of(@NonNull final Boolean isSuccess,
                                        @NonNull final String code,
                                        @NonNull final String message,
                                        @NonNull final T data) {
        return new ApiResponse<>(isSuccess, code, message, data);
    }

}
