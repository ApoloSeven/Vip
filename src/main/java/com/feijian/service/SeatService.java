package com.feijian.service;

import com.feijian.response.PageDataResult;

/**
 * @author: ApoloSeven
 * @version: 1.0
 * @date: 2020/10/8 18:34
 */
public interface SeatService {

    PageDataResult querySeatList(Integer pageNum, Integer pageSize, int included);
}
