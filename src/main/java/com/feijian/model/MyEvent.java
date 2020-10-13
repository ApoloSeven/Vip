package com.feijian.model;

import lombok.Data;

/**
 * @Author ApoloSeven
 * @Date 2020/9/6 16:18
 */
@Data
public class MyEvent {

    private String uuid;

    private String name;

    private Integer mainCount;

    private Integer points;

    private Long percent;

    /**
     * 活动类型，1充值送积分，2消费打折
     */
    private Integer type;
}
