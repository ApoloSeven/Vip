package com.feijian.service.impl;

import com.feijian.dao.*;
import com.feijian.dto.*;
import com.feijian.model.*;
import com.feijian.service.CardService;
import com.feijian.service.PendingService;
import com.feijian.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class CardServiceImpl implements CardService {

    @Resource
    private CardMapper cardMapper;

    @Resource
    private LogMapper logMapper;

    @Resource
    private EventMapper eventMapper;

    @Resource
    private IncludeSeatMapper seatMapper;

    @Resource
    private UserMapper userMapper;

    @Autowired
    private Environment env;

    @Autowired
    private PendingService pendingService;

    private final String card_not_active = "卡片未激活!";


    /**
     * 会员卡的添加
     *
     * @param record
     */
    @Override
    @Transactional
    public void insert(Card record) {
        Card card = findByCardNumber(record.getCardNumber());
        if (card != null) {
            throw new IllegalArgumentException("卡号重复，请换一张卡");
        }
        cardMapper.insert(record);
    }

    /**
     * 会员卡的修改
     *
     * @param record
     */
    @Override
    @Transactional
    public void updateCard(Card record) {
        Card card = findByCardNumber(record.getCardNumber());
        if (card != null && card.getUserId().intValue() != record.getUserId().intValue()) {
            throw new IllegalArgumentException("卡号重复，请换一张卡");
        }
        cardMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public void sellCard(String cardNumber, String userId) {
        Card card = findByCardNumber(cardNumber);
        if (card == null) {
            List<Card> list = findByUserId(Integer.valueOf(userId));
            if (CollectionUtils.isNotEmpty(list)) {
                card = list.get(0);
            }
        }
        validateCard(card);
        card.setUserId(Integer.valueOf(userId));
        cardMapper.updateByPrimaryKeySelective(card);
    }

    @Override
    public List<Card> findByUserId(Integer userId) {
        return cardMapper.selectByUserId(userId);
    }

    @Override
    public Card findByCardNumber(String cardNumber) {
        return cardMapper.selectByCardNumber(cardNumber);
    }

    @Override
    public void deleteByCardNumber(String cardNumber) {
        Card card = findByCardNumber(cardNumber);
        validateCard(card);
        cardMapper.deleteByCardNumber(cardNumber);
    }

    @Override
    public void consume(String cardNumber) {
        Card card = findByCardNumber(cardNumber);
        validateCard(card);
        if (card.getStatus().equals(CardStatusEnum.START.name())) {
            UserLog userLog = logMapper.selectLatestConsume(cardNumber);
            if (!DateUtils.dateIsToday(userLog.getOperateTime())) {
                throw new IllegalArgumentException("此卡尚未结算，打卡日期：" + DateUtils.dateToString(userLog.getOperateTime(), DateUtils.FORMAT_ONE));
            } else {
                throw new IllegalArgumentException("此卡今日已打卡！");
            }
        }

        CardTypeEnum cardType = CardTypeEnum.resolveCardType(card.getCardType().toString());
        int addDay = cardType.getDay();
        if (addDay > 0) {
            //时段卡消费
            consumeMonthCard(card);
        } else if (cardType == CardTypeEnum.MONEY) {
            //储值卡消费
            consumeMoneyCard(card);
        } else {
            throw new IllegalArgumentException("未知类型卡片：" + cardType.getDesc());
        }

    }


    @Override
    public CountPayDto countPay(String cardNumber) {
        Card card = findByCardNumber(cardNumber);
        validateCard(card);
        if (card.getStatus().equals(CardStatusEnum.END.name())) {
            throw new IllegalArgumentException("尚未打卡,无需结算");
        }
        CountPayDto countPayDto = new CountPayDto();
        countPayDto.setCardNumber(cardNumber);
        if (!card.getCardType().equals(CardTypeEnum.MONEY.getId())) {
            countPayDto.setTotalHours(0);
            countPayDto.setTotalPrice(0.0f);
            countPayDto.setMainCount(0f);
            countPayDto.setSinceTime(StringUtils.EMPTY);
        } else {
            UserLog userLog = logMapper.selectLatestConsume(cardNumber);
            if (userLog == null) {
                throw new IllegalArgumentException("异常：未查询到未结算打卡记录");
            }

            float price;
            if (!DateUtils.dateIsToday(userLog.getOperateTime())) {
                String thatDay = DateUtils.dateToString(userLog.getOperateTime(), DateUtils.FORMAT_TWO);
                String endTime = thatDay + " " + env.getProperty("yingye.end");
                Date until = DateUtils.stringToDate(endTime, DateUtils.FORMAT_ONE);
                price = countPrice(userLog.getOperateTime(), until);
                countPayDto.setTotalHours(DateUtils.hourDiff(userLog.getOperateTime(), until));
            } else {
                price = countPrice(userLog.getOperateTime(), new Date());
                countPayDto.setTotalHours(DateUtils.hourDiff(userLog.getOperateTime(), new Date()));
            }
            countPayDto.setTotalPrice(price);
            countPayDto.setSinceTime(DateUtils.dateToString(userLog.getOperateTime(), DateUtils.FORMAT_ONE));
            countPayDto.setMainCount(card.getMainCount());
        }
        return countPayDto;
    }


    private void consumeMonthCard(Card card) {
        if (card.getEndTime() == null) {
            throw new IllegalArgumentException("请先进行充值操作!");
        }
        if (card.getEndTime().getTime() < System.currentTimeMillis()) {
            throw new IllegalArgumentException("卡片已到期!");
        }

        long now = System.currentTimeMillis();
        List<PendingInfo> pendingInfoList = pendingService.findByCardNumber(card.getCardNumber());
        if (CollectionUtils.isNotEmpty(pendingInfoList)) {
            for (PendingInfo pendingInfo : pendingInfoList) {
                if (pendingInfo.getStartTime().getTime() < now && pendingInfo.getEndTime().getTime() > now) {
                    throw new IllegalArgumentException("此卡今天已请假!");
                }
            }
        }
        card.setStatus(CardStatusEnum.START.name());
        cardMapper.updateByPrimaryKeySelective(card);
        //todo 消费金额
        saveLog(card.getCardNumber(), card.getUserId(), OperateEnum.CONSUME.getType(), 0, null);
        log.info("时段卡消费，cardNumber:{}", card.getCardNumber());
    }

    private void consumeMoneyCard(Card card) {
        if (card.getMainCount() < 1) {
            throw new IllegalArgumentException("余额不足,请充值");
        }
        card.setStatus(CardStatusEnum.START.name());
        cardMapper.updateByPrimaryKeySelective(card);
        saveLog(card.getCardNumber(), card.getUserId(), OperateEnum.CONSUME.getType(), 0, null);
    }

    private void consumeOnceCard(Card card) {
        if (card.getTimesLeft() == null || card.getTimesLeft() < 1) {
            throw new IllegalArgumentException("次数已用完!");
        }
        card.setTimesLeft(card.getTimesLeft() - 1);
        cardMapper.updateByPrimaryKeySelective(card);
        int value = 1;
        saveLog(card.getCardNumber(), card.getUserId(), OperateEnum.CONSUME.getType(), value, null);
    }


    private void saveLog(String cardNumber, int userId, int operateType, float operateValue, String phone) {
        UserLog userLog = new UserLog();
        userLog.setOperateTime(Timestamp.from(Instant.now()));
        userLog.setCardNumber(cardNumber);
        if (StringUtils.isNotEmpty(phone)) {
            userLog.setPhone(phone);
        } else {
            User user = userMapper.selectByUserId(userId);
            userLog.setPhone(user.getPhone());
        }
        userLog.setOperateType(operateType);
        userLog.setOperateValue(operateValue);
        logMapper.insert(userLog);
    }

    @Override
    public void pay(String cardNumber) {
        Card card = findByCardNumber(cardNumber);
        validateCard(card);
        float price;
        if (card.getStatus().equals(CardStatusEnum.END.name())) {
            throw new IllegalArgumentException("尚未打卡,无需结算");
        }
        UserLog userLog = logMapper.selectLatestConsume(cardNumber);
        if (userLog == null) {
            throw new IllegalArgumentException("异常：未查询到打卡记录");
        }
        if (!card.getCardType().equals(CardTypeEnum.MONEY.getId())) {
            price = 0f;
            //do nothing
        } else {
            float mainCount = card.getMainCount();
            float points = card.getPoints();


            if (!DateUtils.dateIsToday(userLog.getOperateTime())) {
                String thatDay = DateUtils.dateToString(userLog.getOperateTime(), DateUtils.FORMAT_TWO);
                String endTime = thatDay + " " + env.getProperty("yingye.end");
                Date until = DateUtils.stringToDate(endTime, DateUtils.FORMAT_ONE);
                price = countPrice(userLog.getOperateTime(), until);
            } else {
                price = countPrice(new Date(userLog.getOperateTime().getTime()), new Date());
            }
            if (mainCount + points < price) {
                price = price - mainCount - points;
                card.setMainCount(0f);
                card.setPoints(0f);
            } else if (mainCount < price) {
                card.setMainCount(0f);
                card.setPoints(points + mainCount - price);
            } else {
                card.setMainCount(mainCount - price);
            }


        }
        card.setStatus(CardStatusEnum.END.name());
        cardMapper.updateByPrimaryKeySelective(card);
        //更新打卡记录为结算
        userLog.setParam(DateUtils.dateToString(userLog.getOperateTime(), DateUtils.FORMAT_ONE));
        userLog.setOperateTime(Timestamp.from(Instant.now()));
        userLog.setOperateValue(price);
        userLog.setOperateType(OperateEnum.PAY.getType());
        logMapper.updateLog(userLog);
    }

    /**
     * 同一天的两个时间，花费
     *
     * @param since
     * @param until
     * @return
     */
    private float countPrice(Date since, Date until) {
        String nightLine = DateUtils.dateToString(since, DateUtils.FORMAT_TWO) + " " + env.getProperty("night-line");
        Date night = DateUtils.stringToDate(nightLine, DateUtils.FORMAT_ONE);
        float moneyDay = Float.valueOf(env.getProperty("price.money-day"));
        float moneyNight = Float.valueOf(env.getProperty("price.money-night"));
        if (night.getTime() < since.getTime()) {
            return moneyNight * DateUtils.hourDiff(since, until);
        } else if (night.getTime() > until.getTime()) {
            return moneyDay * DateUtils.hourDiff(since, until);
        } else {
            return moneyDay * DateUtils.hourDiff(since, night) + moneyNight * DateUtils.hourDiff(night, until);
        }
    }

    @Override
    public void invest(InverstDto dto) {
        Card card = findByCardNumber(dto.getCardNumber());
        validateCard(card);
        CardTypeEnum cardType = CardTypeEnum.resolveCardType(card.getCardType().toString());
        if (cardType.getDay() > 0) {
            //时段卡充值
            investMonthCard(card, dto);
        } else if (cardType == CardTypeEnum.MONEY) {
            //储值卡充值
            investMoneyCard(card, dto);
        } else {
            throw new IllegalArgumentException("未知类型卡片：" + cardType.getDesc());
        }
        saveLog(card.getCardNumber(), card.getUserId(), OperateEnum.INVEST.getType(), dto.getMoney(), null);
    }

    private void investMonthCard(Card card, InverstDto dto) {
        dto.setDays(30);
        if (dto.getDays() < 1) {
            throw new IllegalArgumentException("充值天数必须大于1");
        }
        Date since;
        Date until;
        if (card.getEndTime() == null || card.getEndTime().getTime() < System.currentTimeMillis()) {
            since = DateUtils.ignoreTime(new Date());
            card.setStartTime(Timestamp.from(Instant.ofEpochMilli(since.getTime())));
            until = DateUtils.addDay(since, dto.getDays());
            card.setEndTime(Timestamp.from(Instant.ofEpochMilli(until.getTime())));
        } else {
            until = DateUtils.addDay(card.getEndTime(), dto.getDays());
            card.setEndTime(Timestamp.from(Instant.ofEpochMilli(until.getTime())));
        }
        cardMapper.updateByPrimaryKeySelective(card);
    }

    private void investMoneyCard(Card card, InverstDto dto) {
        if (dto.getMoney() < 1) {
            throw new IllegalArgumentException("充值金额必须大于1");
        }
        float mainCount = card.getMainCount() == null ? 0f : card.getMainCount();
        float points = card.getPoints() == null ? 0f : card.getPoints();
        card.setMainCount(mainCount + dto.getMoney());
        //TODO 充值加积分
        card.setPoints(points);
        cardMapper.updateByPrimaryKeySelective(card);
    }

    private void investOnceCard(Card card, InverstDto dto) {
        if (dto.getTimes() < 1) {
            throw new IllegalArgumentException("充值次数至少为1");
        }
        cardMapper.updateByPrimaryKeySelective(card);
    }

    @Override
    public void pending(String cardNumber, String startTime, String endTime) {
        Card card = cardMapper.selectByCardNumber(cardNumber);
        validateCard(card);
        if (CardTypeEnum.resolveCardType(card.getCardType().toString()).getDay() == 0) {
            throw new IllegalArgumentException("只有时段卡能请假！");
        }
        Date since = DateUtils.ignoreTime(startTime);
        Date until = DateUtils.ignoreTime(endTime);
        if (until.getTime() == since.getTime()) {
            until = DateUtils.addDay(until, 1);
        }
        validatePendingTime(since, until);
        int countDay = (int) ((until.getTime() - since.getTime()) / (24 * 3600000));
        int max = Integer.valueOf(env.getProperty("pending-max"));
        if (countDay > max) {
            throw new IllegalArgumentException("每月最多请假" + max + "天");
        }

        Date firstDayOfThisMonth = DateUtils.getFirstDayOfCurrentMonth();
        List<PendingInfo> pendingInfoList = pendingService.findByCardNumber(cardNumber);
        if (CollectionUtils.isNotEmpty(pendingInfoList)) {
            int total = 0;
            for (PendingInfo pendingInfo : pendingInfoList) {
                if (pendingInfo.getEndTime().getTime() <= firstDayOfThisMonth.getTime()) {
                    continue;
                }
                if (!(since.getTime() >= pendingInfo.getEndTime().getTime() || until.getTime() <= pendingInfo.getStartTime().getTime())) {
                    throw new IllegalArgumentException("请假时间重复，请检查！");
                }
                total = total + pendingInfo.getCountDay();
            }
            if (total > max) {
                throw new IllegalArgumentException("本月请假天数已用完");
            }
            if (countDay + total > max) {
                throw new IllegalArgumentException("每月最多请假" + max + "天,已请假" + total + "天");
            }
        }
        PendingInfo pendingInfo = new PendingInfo();
        pendingInfo.setCardNumber(cardNumber);
        pendingInfo.setStartTime(Timestamp.from(Instant.ofEpochMilli(since.getTime())));
        pendingInfo.setEndTime(Timestamp.from(Instant.ofEpochMilli(until.getTime())));
        pendingInfo.setOperateTime(Timestamp.from(Instant.now()));
        pendingInfo.setUuid(UUID.randomUUID().toString());

        pendingInfo.setCountDay(countDay);
        pendingService.save(pendingInfo);
        Date endDate = DateUtils.addDay(card.getEndTime(), countDay);
        card.setEndTime(Timestamp.from(Instant.ofEpochMilli(endDate.getTime())));
        try {
            cardMapper.updateByPrimaryKeySelective(card);
        } catch (Exception e) {
            pendingService.deleteById(pendingInfo.getUuid());
            log.error("更新卡片请假信息失败", e);
            throw e;
        }
        saveLog(cardNumber, card.getUserId(), OperateEnum.PENDING.getType(), countDay, null);
    }

    @Override
    public PendingInfoDto showPending(String cardNumber) {
        List<PendingInfo> pendingInfoList = pendingService.findByCardNumber(cardNumber);
        PendingInfoDto dto = new PendingInfoDto();
        dto.setCardNumber(cardNumber);
        Date firstDayOfThisMonth = DateUtils.getFirstDayOfCurrentMonth();
        if (CollectionUtils.isNotEmpty(pendingInfoList)) {
            int total = 0;
            StringBuilder pendingMsg = new StringBuilder();
            for (PendingInfo pendingInfo : pendingInfoList) {
                if (pendingInfo.getEndTime().getTime() <= firstDayOfThisMonth.getTime()) {
                    continue;
                }
                total = total + pendingInfo.getCountDay();
                pendingMsg.append(DateUtils.dateToString(pendingInfo.getStartTime(), DateUtils.FORMAT_ONE));
                pendingMsg.append("至");
                pendingMsg.append(DateUtils.dateToString(pendingInfo.getEndTime(), DateUtils.FORMAT_ONE));
                pendingMsg.append(" 共" + pendingInfo.getCountDay() + "天\r\n");
            }
            dto.setPendingLeft(Integer.valueOf(env.getProperty("pending-max")) - total);
            dto.setPendingMessage(pendingMsg.toString());
        } else {
            dto.setPendingLeft(Integer.valueOf(env.getProperty("pending-max")));
            dto.setPendingMessage("本月尚未请假");
        }
        return dto;
    }

    @Override
    public void removePending(String pendingId, int day) {
        log.info("销假");
    }

    @Override
    public void includeSeat(String cardNumber, String startTime, String endTime, String seatNumber, String seatNumberOld) {
        Seat a = seatMapper.findBySeatNumber(seatNumber);
        if (a != null && !StringUtils.equals(cardNumber, a.getCardNumber()) && System.currentTimeMillis() < a.getEndTime().getTime()) {
            throw new IllegalArgumentException(seatNumber + "号座位已经被包，请更换");
        }
        Seat oldSeat = seatMapper.findByCardNumber(cardNumber);
        if (oldSeat != null) {
            if (StringUtils.isNotEmpty(seatNumberOld) && System.currentTimeMillis() < oldSeat.getEndTime().getTime()) {
                seatMapper.updateSeatNumber(seatNumber, oldSeat.getUuid());
                return;
            }
            if (System.currentTimeMillis() < oldSeat.getEndTime().getTime()) {
                throw new IllegalArgumentException("已存在包座记录:" + DateUtils.dateToString(oldSeat.getStartTime(),
                        DateUtils.FORMAT_ONE) + " 至 " + DateUtils.dateToString(oldSeat.getEndTime(), DateUtils.FORMAT_ONE));

            }
        }
        seatMapper.deleteByCardNumber(cardNumber);
        if (a != null) {
            seatMapper.deleteByUuid(a.getUuid());
        }
        Date since = DateUtils.ignoreTime(startTime);
        Date until = DateUtils.ignoreTime(endTime);
        Card card = cardMapper.selectByCardNumber(cardNumber);
        validateCard(card);
        Seat seat = new Seat();
        seat.setCardNumber(cardNumber);
        seat.setUuid(UUID.randomUUID().toString());
        seat.setSeatNumber(seatNumber);
        seat.setStartTime(Timestamp.from(Instant.ofEpochMilli(since.getTime())));
        seat.setEndTime(Timestamp.from(Instant.ofEpochMilli(until.getTime())));
        seatMapper.insert(seat);
    }

    @Override
    public SeatDto showIncludeSeat(String cardNumber) {
        SeatDto dto = new SeatDto();
        dto.setCardNumber(cardNumber);
        Seat seat = seatMapper.findByCardNumber(cardNumber);
        if (seat == null) {
            Card card = cardMapper.selectByCardNumber(cardNumber);
            validateCard(card);
            dto.setSeatNumberOld(StringUtils.EMPTY);
            dto.setStartTime(DateUtils.dateToString(new Date(), DateUtils.FORMAT_ONE));
            if (card.getCardType().intValue() == CardTypeEnum.MONEY.getId()) {
                dto.setEndTime(DateUtils.dateToString(DateUtils.addDay(new Date(), 30), DateUtils.FORMAT_ONE));
            } else {
                if (card.getEndTime() == null) {
                    dto.setEndTime(DateUtils.dateToString(DateUtils.addDay(new Date(), 30), DateUtils.FORMAT_ONE));
                } else {
                    dto.setEndTime(DateUtils.dateToString(card.getEndTime(), DateUtils.FORMAT_ONE));
                }
            }
        } else {
            dto.setSeatNumberOld(seat.getSeatNumber());
            dto.setStartTime(DateUtils.dateToString(seat.getStartTime(), DateUtils.FORMAT_ONE));
            dto.setEndTime(DateUtils.dateToString(seat.getEndTime(), DateUtils.FORMAT_ONE));
        }
        return dto;
    }

    private void validatePendingTime(Date since, Date until) {
        if (since.getTime() > until.getTime()) {
            throw new IllegalArgumentException("开始时间不得晚于结束时间");
        }
        Date now = DateUtils.ignoreTime(new Date());
        Date firstDayOfNextMonth = DateUtils.getFirstDayOfNextMonth();
        if (!(since.getTime() >= now.getTime() && until.getTime() <= firstDayOfNextMonth.getTime())) {
            throw new IllegalArgumentException("请假时间必须在明天到月底之间");
        }
    }

    private void validateCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException(card_not_active);
        }
        if (StringUtils.equals(card.getStatus(), CardStatusEnum.FORBIDDEN.name())) {
            throw new IllegalArgumentException("卡片已禁用");
        }
    }


}