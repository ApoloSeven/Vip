package com.feijian.dto;

import lombok.Data;

@Data
public class LogSearchDTO {

    private String phone;

    private String startTime;

    private String endTime;

    private String operateType;

    private Integer startIndex;

    private Integer pageSize;
}
