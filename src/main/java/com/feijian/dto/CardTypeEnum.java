package com.feijian.dto;

/**
 * @author: ApoloSeven
 * @version: 1.0
 * @date: 2020/8/30 20:01
 */
public enum CardTypeEnum {

    MONEY(1, "储值卡", 0),

    MONTH(2, "月卡(全天)", 30),

    MONTH_NIGHT(3, "月卡(晚间)", 30),

    UNKNOWN(999, "未知卡片", -1);

    private int id;

    private String desc;

    private int day;

    CardTypeEnum(int id, String name, int day) {
        this.id = id;
        this.desc = name;
        this.day = day;
    }

    public String getDesc() {
        return this.desc;
    }

    public int getId() {
        return this.id;
    }

    public int getDay() {
        return day;
    }

    public static CardTypeEnum resolveCardType(String id) {
        for (CardTypeEnum cardType : CardTypeEnum.values()) {
            if (cardType.getId() == Integer.valueOf(id)) {
                return cardType;
            }
        }
        return UNKNOWN;
    }


}
