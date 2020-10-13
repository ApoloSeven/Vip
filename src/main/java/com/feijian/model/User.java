package com.feijian.model;


import lombok.*;

import java.io.Serializable;

@ToString
@Data
public class User implements Serializable {

    private Integer userId;

    private String userName;

    private String phone;
}