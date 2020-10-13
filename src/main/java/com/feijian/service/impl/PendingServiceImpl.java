package com.feijian.service.impl;

import com.feijian.dao.PendingMapper;
import com.feijian.model.PendingInfo;
import com.feijian.service.PendingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author ApoloSeven
 * @Date 2020/9/6 12:55
 */
@Service
public class PendingServiceImpl implements PendingService {

    @Resource
    private PendingMapper pendingMapper;


    @Override
    public PendingInfo findById(String id) {
        return pendingMapper.findById(id);
    }

    @Override
    public List<PendingInfo> findByCardNumber(String cardNumber) {
        return pendingMapper.findByCardNumber(cardNumber);
    }

    @Override
    public void deleteById(String id) {
        pendingMapper.deleteByUuid(id);
    }

    @Override
    public void update(PendingInfo pendingInfo) {
        pendingMapper.updateByIdSelective(pendingInfo);
    }

    @Override
    public void save(PendingInfo pendingInfo) {
        pendingMapper.insert(pendingInfo);
    }
}
