package com.feijian.service;

import com.feijian.dto.CountPayDto;
import com.feijian.dto.InverstDto;
import com.feijian.dto.PendingInfoDto;
import com.feijian.dto.SeatDto;
import com.feijian.model.Card;

import java.util.List;

public interface CardService {

    /**
     * 开卡
     * @param record
     */
    void insert(Card record);

    void updateCard(Card record);

    void sellCard(String cardNumber, String userId);

    List<Card> findByUserId(Integer userId);

    Card findByCardNumber(String cardNumber);

    /**
     * 销卡
     * @param cardNumber
     */
    void deleteByCardNumber(String cardNumber);

    /**
     * 打卡消费
     * @param cardNumber
     */
    void consume(String cardNumber);

    /**
     * 结算前计算消费信息
     * @param cardNumber
     */
    CountPayDto countPay(String cardNumber);

    /**
     * 结算
     * @param cardNumber
     */
    void pay(String cardNumber);

    /**
     * 充值
     */
    void invest(InverstDto dto);

    /**
     * 请假
     * @param cardNumber
     */
    void pending(String cardNumber, String startTime, String endTime);

    /**
     * 请假信息展示
     * @param cardNumber
     * @return
     */
    PendingInfoDto showPending(String cardNumber);

    /**
     * 销假
     * @param pendingId
     * @param day
     */
    void removePending(String pendingId, int day);

    /**
     * 包座
     * @param cardNumber
     */
    void includeSeat(String cardNumber, String startTime, String endTime, String seatNumber, String seatNumberOld);

    /**
     * 包座信息展示
     * @param cardNumber
     * @return
     */
    SeatDto showIncludeSeat(String cardNumber);

}