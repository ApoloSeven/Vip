package com.feijian.dto;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @Author ApoloSeven
 * @Date 2020/10/8 19:10
 */
@Data
public class SeatIncludeDto {

    private Integer id;

    private String seatNumber;

    private String uuid;

    private String cardNumber;

    private Timestamp startTime;

    private String startTimeStr;

    private Timestamp endTime;

    private String endTimeStr;
}
