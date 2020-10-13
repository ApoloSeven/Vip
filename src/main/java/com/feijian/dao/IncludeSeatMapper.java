package com.feijian.dao;

import com.feijian.dto.SeatIncludeDto;
import com.feijian.model.Seat;

import java.util.List;

/**
 * @author: ApoloSeven
 * @version: 1.0
 * @date: 2020/9/6 16:17
 */
public interface IncludeSeatMapper {

    void insert(Seat seat);

    Seat findByUuid(String uuid);

    Seat findByCardNumber(String cardNumber);

    Seat findBySeatNumber(String seatNumber);

    void deleteByCardNumber(String cardNumber);

    void deleteByUuid(String uuid);

    void updateSeatNumber(String seatNumber, String uuid);

    Integer countAllSeats();

    List<SeatIncludeDto> findAllSeats();
}
