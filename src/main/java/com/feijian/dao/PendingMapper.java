package com.feijian.dao;

import com.feijian.model.PendingInfo;

import java.util.List;

/**
 * @author: ApoloSeven
 * @version: 1.0
 * @date: 2020/9/6 12:44
 */
public interface PendingMapper {

    PendingInfo findById(String id);

    List<PendingInfo> findByCardNumber(String cardNumber);

    void deleteByUuid(String id);

    void updateByIdSelective(PendingInfo pendingInfo);

    void insert(PendingInfo pendingInfo);


}
