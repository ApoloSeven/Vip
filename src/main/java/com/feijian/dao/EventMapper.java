package com.feijian.dao;

import com.feijian.model.MyEvent;

import java.util.List;

/**
 * @author: ApoloSeven
 * @version: 1.0
 * @date: 2020/9/6 16:17
 */
public interface EventMapper {

    void insert(MyEvent event);

    MyEvent findByUuid(String uuid);

    List<MyEvent> findByType();

    void deleteByUuid(String uuid);


}
