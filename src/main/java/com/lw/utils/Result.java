package com.lw.utils;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;

/**
 * @author lw
 * @data 2023/9/27
 * @周三
 */
@Data
@Component
public class Result<T> {
    private T data;
    private Integer code;
    private String msg;

    public Result() {
    }

    public Result(T data, Integer code, String msg) {
        this.data = data;
        this.code = code;
        this.msg = msg;
    }
    public static <T> Result<T> success() {
        return new Result<>(null, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase());
    }

    public static <T> Result<T> success(T data) {
        return new Result<T>(data, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase());
    }

    public static <T> Result<T> success(String msg) {
        return new Result<T>(null, HttpStatus.OK.value(), msg);
    }

    public static <T> Result<T> success(T data, String msg) {
        return new Result<T>(data, HttpStatus.OK.value(), msg);
    }

    public static <T> Result<T> success(Integer code, String msg) {
        return new Result<T>(null, code, msg);
    }

    public static <T> Result<T> success(T data, Integer code, String msg) {
        return new Result<T>(data, code, msg);
    }

    public static <T> Result<T> error() {
        return new Result<>(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    public static <T> Result<T> error(T data) {
        return new Result<T>(data, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    public static <T> Result<T> error(String msg) {
        return new Result<T>(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }

    public static <T> Result<T> error(T data, String msg) {
        return new Result<T>(data, HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<T>(null, code, msg);
    }

    public static <T> Result<T> error(T data, Integer code, String msg) {
        return new Result<T>(data, code, msg);
    }
}
