package com.feijian.service;

import com.feijian.dto.LogSearchDTO;
import com.feijian.model.UserLog;
import com.feijian.response.PageDataResult;

public interface LogService {
    /**
     * 查询某个用户的卡片活动日志
     * @param logSearchDTO
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageDataResult findAllLogs(Integer pageNum, Integer pageSize, LogSearchDTO logSearchDTO);

    void insert(UserLog userLog);
}
