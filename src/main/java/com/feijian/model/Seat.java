package com.feijian.model;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @Author ApoloSeven
 * @Date 2020/9/6 16:23
 */
@Data
public class Seat {

    private String uuid;

    private String cardNumber;

    private Timestamp startTime;

    private Timestamp endTime;

    private String seatNumber;
}
