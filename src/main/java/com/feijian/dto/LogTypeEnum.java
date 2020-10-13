package com.feijian.dto;

/**
 * @author: ApoloSeven
 * @version: 1.0
 * @date: 2020/9/6 22:29
 */
public enum LogTypeEnum {

    CONSUME_NO_MONEY(1),
    CONSUME_WITH_MONEY(2),
    INVEST(3),
    BACK(4),
    PENDING(5);

    private int code;

    public int getCode() {
        return code;
    }

    LogTypeEnum(int code){
        this.code = code;
    }
}
