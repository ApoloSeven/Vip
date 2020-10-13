package com.feijian.dto;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @Author ApoloSeven
 * @Date 2020/8/30 19:17
 */
@Data
public class UserCardDto {

    private Integer userId;

    private String userName;

    private String phone;

    private String cardNumber;

    private String cardType;

    private String cardStatus;

    private float mainCount;

    private String includeSeatUuid;

    private String seatNumber;

    private Timestamp startTime;

    private Timestamp endTime;

    private int daysLeft;
}
