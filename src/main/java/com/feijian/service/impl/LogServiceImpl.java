package com.feijian.service.impl;

import com.feijian.dao.LogMapper;
import com.feijian.dto.LogSearchDTO;
import com.feijian.dto.UserLogDto;
import com.feijian.model.UserLog;
import com.feijian.response.PageDataResult;
import com.feijian.service.LogService;
import com.feijian.utils.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class LogServiceImpl implements LogService {

    @Resource
    private LogMapper logMapper;

    @Override
    public PageDataResult findAllLogs(Integer pageNum, Integer pageSize, LogSearchDTO logSearchDTO) {
        PageDataResult pageDataResult = new PageDataResult();
        Integer total = logMapper.countAllLogs(logSearchDTO);
        if (total == null || total == 0) {
            return pageDataResult;
        }
        List<UserLog> onePageLogs = logMapper.findAllLogs(logSearchDTO);
        if (CollectionUtils.isNotEmpty(onePageLogs)) {
            List<UserLogDto> dtoList = new ArrayList<>(onePageLogs.size());
            for (UserLog userLog : onePageLogs) {
                UserLogDto dto = new UserLogDto();
                BeanUtils.copyProperties(userLog, dto);
                dto.setOperateTimeStr(DateUtils.dateToString(userLog.getOperateTime(), DateUtils.FORMAT_ONE));
                dtoList.add(dto);
            }
            pageDataResult.setList(dtoList);
            pageDataResult.setTotals(total);
        }
        return pageDataResult;
    }

    @Override
    public void insert(UserLog userLog) {
        logMapper.insert(userLog);
    }
}
