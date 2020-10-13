package com.feijian.dao;

import com.feijian.dto.LogSearchDTO;
import com.feijian.model.UserLog;

import java.util.List;
import java.util.Map;

public interface LogMapper {

    UserLog selectByPrimaryKey(Integer logId);

    UserLog selectLatestConsume(String cardNumber);

    int insert(UserLog record);

    List<UserLog> findAllLogs(LogSearchDTO logSearchDTO);

    Integer countAllLogs(LogSearchDTO logSearchDTO);

    void updateLog(UserLog userLog);


}