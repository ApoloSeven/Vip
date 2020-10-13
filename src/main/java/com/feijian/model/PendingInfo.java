package com.feijian.model;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @Author ApoloSeven
 * @Date 2020/9/6 12:45
 */
@Data
public class PendingInfo {

    private String uuid;

    private String cardNumber;

    private Timestamp startTime;

    private Timestamp endTime;

    private Integer countDay;

    private Timestamp operateTime;
}
