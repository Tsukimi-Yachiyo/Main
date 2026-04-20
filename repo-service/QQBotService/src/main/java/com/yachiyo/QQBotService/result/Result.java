package com.yachiyo.QQBotService.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class Result<T>{
    private String code;
    private String message;
    private T data;
    private String detail = null;

    /**
     * 成功返回结果
     * @param data 数据
     * @param message 消息
     * @param detail 详情
     * @return Result<T>
     */
    public static <T> Result<T> success(T data, String message, String detail) {
        Result<T> result = new Result<>();
        result.setCode("200");
        result.setMessage(message);
        result.setData(data);
        result.setDetail(detail);

        log(true, result);
        return result;
    }

    /**
     * 成功返回结果
     * @return Result<T>
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode("200");
        result.setMessage("success");
        result.setData(null);
        result.setDetail(null);
        log(true, result);
        return result;
    }

    /**
     * 成功返回结果
     * @param data 数据
     * @return Result<T>
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode("200");
        result.setMessage("success");
        result.setData(data);
        result.setDetail(null);
        log(true, result);
        return result;
    }

    /**
     * 失败返回结果
     * @param code 状态码
     * @param message 消息
     * @param detail 详情
     * @return Result<T>
     */
    public static <T> Result<T> error(String code, String message, String detail) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        result.setDetail(detail);
        log(false, result);
        return result;
    }

    /**
     * 失败返回结果
     * @param message 消息
     * @param detail 详情
     * @return Result<T>
     */
    public static <T> Result<T> error(String message, String detail) {
        Result<T> result = new Result<>();
        result.setCode("500");
        result.setMessage(message);
        result.setData(null);
        result.setDetail(detail);
        log(false, result);
        return result;
    }

    /**
     * 失败返回结果
     * @param code 状态码
     * @return Result<T>
     */
    public static <T> Result<T> error(String code)  {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage("error");
        result.setData(null);
        result.setDetail(null);
        log(false, result);
        return result;
    }

    /**
     * 失败返回结果
     * @return Result<T>
     */
    public static <T> Result<T> error() {
        Result<T> result = new Result<>();
        result.setCode("500");
        result.setMessage("error");
        result.setData(null);
        result.setDetail(null);
        log(false, result);
        return result;
    }

    private static <T> void log(Boolean isSuccess,Result<T> result) {
        log.info("{}: code={}, message={}, data={}, detail={}", isSuccess ? "success" : "error", result.getCode(), result.getMessage(), result.getData(), result.getDetail());
    }
}
