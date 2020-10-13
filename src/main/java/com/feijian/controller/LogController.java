package com.feijian.controller;

import com.feijian.dto.LogSearchDTO;
import com.feijian.response.PageDataResult;
import com.feijian.response.ResponseCode;
import com.feijian.response.ResponseResult;
import com.feijian.service.LogService;
import com.feijian.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@Controller()
public class LogController {

    @Autowired
    private LogService logService;


    @GetMapping(value = "/logs")
    @ResponseBody
    public PageDataResult findLog(@RequestParam("page") Integer pageNum,
                                  @RequestParam("limit") Integer pageSize,
                                  @RequestParam(required = false) String startTime,
                                  @RequestParam(required = false) String endTime,
                                  @RequestParam(required = false) String operateType,
                                  @RequestParam(required = false) String phone) {
        try {
            if (null == pageNum || pageNum < 1) {
                pageNum = 1;
            }
            if (null == pageSize || pageSize < 1) {
                pageSize = 10;
            }
            if (StringUtils.isEmpty(endTime)) {
                endTime = DateUtils.dateToString(new Date(), DateUtils.FORMAT_ONE);
            }
            if (StringUtils.isEmpty(startTime)) {
                startTime = StringUtils.substringBefore(endTime, " ") + " 00:00:00";
            }
            LogSearchDTO logSearchDTO = new LogSearchDTO();
            logSearchDTO.setPhone(phone);
            logSearchDTO.setOperateType(operateType);
            logSearchDTO.setStartTime(startTime);
            logSearchDTO.setEndTime(endTime);
            logSearchDTO.setStartIndex((pageNum - 1) * pageSize);
            logSearchDTO.setPageSize(pageSize);
            PageDataResult pageDataResult = logService.findAllLogs(pageNum, pageSize, logSearchDTO);
            return pageDataResult;
        } catch (Exception e) {
            log.error("日志列表查询异常！", e);
            return null;
        }
    }
}