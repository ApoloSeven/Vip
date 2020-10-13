package com.feijian.dao;

import com.feijian.model.Card;

import java.util.List;

public interface CardMapper {

    List<Card> selectByUserId(Integer userId);

    Card selectByCardNumber(String cardNumber);

    int insert(Card record);

    int updateByPrimaryKeySelective(Card record);

    void deleteByCardNumber(String cardNumber);
}