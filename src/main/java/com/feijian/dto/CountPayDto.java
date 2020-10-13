package com.feijian.dto;

import lombok.Data;

/**
 * @Author ApoloSeven
 * @Date 2020/10/3 16:34
 */
@Data
public class CountPayDto {

    private String cardNumber;

    private String sinceTime;

    private float totalPrice;

    private float totalHours;

    private float mainCount;
}
