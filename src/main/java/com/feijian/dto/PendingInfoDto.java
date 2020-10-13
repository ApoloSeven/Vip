package com.feijian.dto;

import lombok.Data;

/**
* @Author ApoloSeven
* @Date 2020/10/9 11:17
*/
@Data
public class PendingInfoDto {

    private String cardNumber;

    private int pendingLeft;

    private String pendingMessage;
}
