package com.mercury.discovery.common;

public class SimpleResponseModel {
    private int affected;
    private String message;
    private Object object;

    public SimpleResponseModel(int affected) {
        this.affected = affected;
        if (affected > 0) {
            this.message = "처리되었습니다.";
        } else {
            this.message = "처리되었습니다...";
        }
    }

    public SimpleResponseModel(int affected, String message) {
        this.affected = affected;
        this.message = message;
    }

    public SimpleResponseModel(int affected, Object object, String message) {
        this.affected = affected;
        this.object = object;
        this.message = message;
    }

    public int getAffected() {
        return affected;
    }

    public String getMessage() {
        return message;
    }

    public Object getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "SimpleResponseModel{" +
                "affected=" + affected +
                ", message='" + message + '\'' +
                '}';
    }
}
