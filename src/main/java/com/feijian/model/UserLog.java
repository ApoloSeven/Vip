package com.feijian.model;

import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;


@ToString
@Data
public class UserLog implements Serializable {

    private Integer id;

    private String phone;

    private String cardNumber;

    private Integer operateType;

    private Float operateValue;

    private Timestamp operateTime;

    private String param;

}