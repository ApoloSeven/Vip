package com.feijian.model;


import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

@ToString
@Data
public class Card implements Serializable {

    private Integer userId;

    private String cardNumber;

    private Integer cardType;

    private Float mainCount;

    private Float points;

    private Timestamp startTime;

    private Timestamp endTime;

    private String status;

    private Integer timesLeft;

}