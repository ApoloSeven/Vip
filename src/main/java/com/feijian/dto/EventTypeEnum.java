package com.feijian.dto;

/**
 * @author: ApoloSeven
 * @version: 1.0
 * @date: 2020/9/6 16:50
 */
public enum EventTypeEnum {

    INVEST(1),

    CONSUME(2);

    private int typeValue;

    EventTypeEnum(int typeValue) {
        this.typeValue = typeValue;
    }

    public int getTypeValue() {
        return typeValue;
    }
}
