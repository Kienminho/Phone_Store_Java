package com.tdtu.phone_store_java.Common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Response<T> {

    private int statusCode;
    private String message;
    private int totalRecord;
    private T data;

    public static <T> Response<T> createSuccessResponseModel(int totalRecord, T data) {
        return new Response<>(200, "Successful", totalRecord, data);
    }

    public static <T> Response<T> createErrorResponseModel(String message, T data) {
        return new Response<>(400, message, 0, null);
    }

    public static <T> Response<T> createResponseModel(int statusCode, String message, int totalRecord, T data) {
        return new Response<>(statusCode, message, totalRecord, data);
    }

}
