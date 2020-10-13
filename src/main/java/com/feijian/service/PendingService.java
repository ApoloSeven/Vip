package com.feijian.service;

import com.feijian.model.PendingInfo;

import java.util.List;

/**
 * @author: ApoloSeven
 * @version: 1.0
 * @date: 2020/9/6 12:54
 */
public interface PendingService {

    PendingInfo findById(String id);

    List<PendingInfo> findByCardNumber(String cardNumber);

    void deleteById(String id);

    void update(PendingInfo pendingInfo);

    void save(PendingInfo pendingInfo);


}
