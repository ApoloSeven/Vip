package com.feijian.service.impl;

import com.feijian.dao.UserMapper;
import com.feijian.dto.CardStatusEnum;
import com.feijian.dto.CardTypeEnum;
import com.feijian.dto.UserCardDto;
import com.feijian.dto.UserCardListSearchDto;
import com.feijian.model.PendingInfo;
import com.feijian.model.User;
import com.feijian.service.PendingService;
import com.feijian.service.UserService;
import com.feijian.utils.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Autowired
    private PendingService pendingService;

    @Override
    public void insert(User record) {
        User user = retriveUsersByPhone(record.getPhone());
        if (record.getUserId() == null) {
            if (user == null) {
                userMapper.insert(record);
            } else {
                throw new IllegalArgumentException("手机号已被注册");
            }
        } else if (record.getUserId() != null) {
            if (user != null && !record.getUserId().equals(user.getUserId())) {
                throw new IllegalArgumentException("手机号已被注册");
            } else {
                userMapper.updateByUserIdSelective(record);
            }
        }
    }

    @Override
    public User retriveUsersByPhone(String phone) {
        return userMapper.selectByPhone(phone);
    }

    @Override
    public User retriveUsersByUserId(Integer userId) {
        return userMapper.selectByUserId(userId);
    }


    @Override
    public List<UserCardDto> findUserCardList(String phone, String cardNumber) {
        if (StringUtils.isEmpty(phone) && StringUtils.isEmpty(cardNumber)) {
            return null;
        }
        List<UserCardDto> list = userMapper.selectUserCards(new UserCardListSearchDto(phone, cardNumber));
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        for (UserCardDto dto : list) {
            if (StringUtils.isEmpty(dto.getCardNumber())) {
                dto.setCardNumber("-");
                continue;
            }
            dto.setCardType(CardTypeEnum.resolveCardType(dto.getCardType()).getDesc());
            dto.setCardStatus(CardStatusEnum.resolveCardStatus(dto.getCardStatus()).getStatusDesc());
            if (!dto.getCardType().equals(CardTypeEnum.MONEY.getDesc())) {
                if (dto.getEndTime() != null) {
                    dto.setDaysLeft(DateUtils.dayDiff(new Date(), dto.getEndTime()));
                } else {
                    dto.setDaysLeft(0);
                }
            }
            //判断是否请假
//            if (!CardStatusEnum.PENDING.name().equals(dto.getCardStatus())) {
//                List<PendingInfo> pendingInfoList = pendingService.findByCardNumber(dto.getCardNumber());
//                long now = System.currentTimeMillis();
//                for (PendingInfo pendingInfo : pendingInfoList) {
//                    if (pendingInfo.getStartTime().getTime() < now && pendingInfo.getEndTime().getTime() > now) {
//                        dto.setCardStatus(CardStatusEnum.PENDING.name());
//                        break;
//                    }
//                }
//            }
        }
        return list;
    }


}