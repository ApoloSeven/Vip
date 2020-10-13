package com.feijian.dao;

import com.feijian.dto.UserCardDto;
import com.feijian.dto.UserCardListSearchDto;
import com.feijian.model.User;

import java.util.List;

public interface UserMapper {

    User selectByUserId(Integer userId);

    User selectByPhone(String phone);

    void deleteByUserId(Integer userId);

    int insert(User record);

    void updateByUserIdSelective(User record);

    List<UserCardDto> selectUserCards(UserCardListSearchDto dto);

}