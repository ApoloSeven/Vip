package com.feijian.dto;

/**
 * @author: ApoloSeven
 * @version: 1.0
 * @date: 2020/8/30 20:17
 */
public enum CardStatusEnum {

    INITIAL("新卡"),
    START("已打卡"),
    END("已结算"),
    PENDING("请假"),
    FORBIDDEN("禁用"),
    UNKNOWN("无效卡片");

    private String statusDesc;

    public String getStatusDesc(){
        return statusDesc;
    }

    CardStatusEnum(String statusDesc){
        this.statusDesc = statusDesc;
    }

    public static CardStatusEnum resolveCardStatus(String name){
        for (CardStatusEnum cardStatus : CardStatusEnum.values()) {
            if (cardStatus.name().equals(name)) {
                return cardStatus;
            }
        }
        return UNKNOWN;
    }

}
