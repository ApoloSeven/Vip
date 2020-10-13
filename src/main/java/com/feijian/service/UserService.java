package com.feijian.service;

import com.feijian.dto.UserCardDto;
import com.feijian.model.User;

import java.util.List;


public interface UserService {

    void insert(User record);

    User retriveUsersByPhone(String phone);

    User retriveUsersByUserId(Integer userId);

    List<UserCardDto> findUserCardList(String phone, String cardNumber);
}
