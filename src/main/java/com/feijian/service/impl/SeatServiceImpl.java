package com.feijian.service.impl;

import com.feijian.dao.IncludeSeatMapper;
import com.feijian.dto.SeatIncludeDto;
import com.feijian.response.PageDataResult;
import com.feijian.service.SeatService;
import com.feijian.utils.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author ApoloSeven
 * @Date 2020/10/8 18:37
 */
@Service
public class SeatServiceImpl implements SeatService {

    @Resource
    private IncludeSeatMapper seatMapper;

    @Override
    public PageDataResult querySeatList(Integer pageNum, Integer pageSize, int included) {
        PageDataResult pageDataResult = new PageDataResult();
        Integer total = seatMapper.countAllSeats();
        if (total == null || total == 0) {
            return pageDataResult;
        }
        int startIndex = (pageNum - 1) * pageSize;
        List<SeatIncludeDto> totalSeats = seatMapper.findAllSeats();
        if (CollectionUtils.isEmpty(totalSeats)) {
            return pageDataResult;
        }
        long curr = System.currentTimeMillis();
        Iterator<SeatIncludeDto> iterator = totalSeats.listIterator();
        while (iterator.hasNext()) {
            SeatIncludeDto seat = iterator.next();
            if (seat.getEndTime() != null && seat.getEndTime().getTime() < curr) {
                seat.setUuid(null);
            }
            if (included == 1 && StringUtils.isEmpty(seat.getUuid())) {
                iterator.remove();
                continue;
            }
            if (included == 2 && StringUtils.isNotEmpty(seat.getUuid())) {
                iterator.remove();
                continue;
            }

            if (StringUtils.isNotEmpty(seat.getUuid())) {
                seat.setStartTimeStr(DateUtils.dateToString(seat.getStartTime(), DateUtils.FORMAT_ONE));
                seat.setEndTimeStr(DateUtils.dateToString(seat.getEndTime(), DateUtils.FORMAT_ONE));
            }
        }

        List<SeatIncludeDto> resultList = new ArrayList<>(pageSize);
        for (int i = startIndex; i < totalSeats.size(); i++) {
            if (i - startIndex == pageSize) break;
            resultList.add(totalSeats.get(i));
        }
        pageDataResult.setList(resultList);
        pageDataResult.setTotals(totalSeats.size());
        return pageDataResult;
    }
}
