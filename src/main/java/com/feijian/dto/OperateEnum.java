package com.feijian.dto;

/**
 * @author: ApoloSeven
 * @version: 1.0
 * @date: 2020/9/10 21:21
 */
public enum OperateEnum {

    CONSUME(1),
    PAY(2),
    INVEST(3),
    BACK(4),
    PENDING(5);

    private int type;

    OperateEnum(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
