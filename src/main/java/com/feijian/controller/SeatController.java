package com.feijian.controller;

import com.feijian.response.PageDataResult;
import com.feijian.service.SeatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author ApoloSeven
 * @Date 2020/10/8 18:33
 */
@Slf4j
@Controller
public class SeatController {

    @Autowired
    private SeatService seatService;

    @GetMapping(value = "/seats")
    @ResponseBody
    public PageDataResult findLog(@RequestParam("page") Integer pageNum,
                                  @RequestParam("limit") Integer pageSize,
                                  @RequestParam(required = false) Integer includeSeat) {

        try {
            if(includeSeat == null) includeSeat = 0;
            return seatService.querySeatList(pageNum, pageSize, includeSeat);
        } catch (Exception e) {
            log.error("查询座位列表异常！", e);
            return null;
        }
    }
}
