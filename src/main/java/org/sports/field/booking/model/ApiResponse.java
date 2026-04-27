package org.sports.field.booking.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private String code;
    private T data;
    private Meta meta;

    public ApiResponse(String code, T data) {
        this.code = code;
        this.data = data;
    }

    public ApiResponse(String code, T data, Meta meta) {
        this.code = code;
        this.data = data;
        this.meta = meta;
    }

    public String getCode() {
        return code;
    }

    public T getData() {
        return data;
    }

    public Meta getMeta() {
        return meta;
    }
}