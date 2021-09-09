package com.zygame.mvp_project.data.entity;

/**
 * Created on 2021/7/29 15
 *
 * @author xjl
 */
public class BaseNetEntity {
    private Boolean is_success;
    private String error_code;
    private String message;
    private Object data;

    public Boolean isIs_success() {
        return is_success;
    }

    public void setIs_success(Boolean is_success) {
        this.is_success = is_success;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object pData) {
        data = pData;
    }

    @Override
    public String toString() {
        return "BaseNetEntity{" +
                "is_success=" + is_success +
                ", error_code='" + error_code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
