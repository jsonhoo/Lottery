package com.wyzk.lottery.model;

/**
 * Created by sushuai on 2016/3/25.
 */
public class ResultReturn<T> {

    private int code;
    private T data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ResultReturn{" +
                "code=" + code +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                '}';
    }

    public enum ResultCode {
        RESULT_OK(0);

        private int value;

        ResultCode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
