package com.feijian.controller;

import com.feijian.dto.*;
import com.feijian.model.Card;
import com.feijian.response.ResponseCode;
import com.feijian.response.ResponseResult;
import com.feijian.service.CardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller()
public class CardController {

    @Autowired
    private CardService cardService;

    @RequestMapping("/cardView")
    public String toCardView() {
        return "cardPage";
    }

    /**
     * 根据用户id或者卡号查询卡片信息
     *
     * @param cardNumber
     * @return
     */
    @GetMapping("/card")
    @ResponseBody
    public ResponseResult queryByUserId(String cardNumber) {
        Card card = cardService.findByCardNumber(cardNumber);
        return new ResponseResult(ResponseCode.SUCCESS, card);
    }


    /**
     * 会员卡的添加
     *
     * @param card
     */
    @PostMapping("/card")
    @ResponseBody
    public ResponseResult insert(Card card) {
        try {
            card.setStatus(CardStatusEnum.INITIAL.name());
            card.setMainCount(0f);
            card.setPoints(0f);
            cardService.insert(card);
            return new ResponseResult();
        } catch (IllegalArgumentException e) {
            return new ResponseResult(ResponseCode.ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("开卡失败：", e);
            return new ResponseResult(ResponseCode.ERROR, null);
        }
    }

    /**
     * 会员卡信息的修改
     *
     * @param card
     */
    @PutMapping("/card/{cardNumber}")
    @ResponseBody
    public ResponseResult updateCard(Card card) {
        try {
            cardService.updateCard(card);
            return new ResponseResult();
        } catch (IllegalArgumentException e) {
            return new ResponseResult(ResponseCode.ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("修改卡片信息失败：", e);
            return new ResponseResult(ResponseCode.ERROR, null);
        }
    }

    /**
     * 会员卡信息的修改
     */
    @PostMapping("/card/{cardNumber}/sell")
    @ResponseBody
    public ResponseResult updateCard(@PathVariable("cardNumber") String cardNumber, @RequestParam String userId) {
        try {
            cardService.sellCard(cardNumber, userId);
            return new ResponseResult();
        } catch (IllegalArgumentException e) {
            return new ResponseResult(ResponseCode.ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("转让卡片失败：", e);
            return new ResponseResult(ResponseCode.ERROR, null);
        }
    }

    /**
     * 删除会员卡
     *
     * @param cardNumber
     */
    @DeleteMapping("/card/{cardNumber}")
    @ResponseBody
    public ResponseResult deleteCard(@PathVariable("cardNumber") String cardNumber) {
        try {
            cardService.deleteByCardNumber(cardNumber);
            return new ResponseResult();
        } catch (IllegalArgumentException e) {
            return new ResponseResult(ResponseCode.ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("删除卡片失败：", e);
            return new ResponseResult(ResponseCode.ERROR, null);
        }
    }

    /**
     * 请假前信息展示
     *
     * @param cardNumber
     * @return
     */
    @GetMapping("/showpending/card/{cardNumber}")
    @ResponseBody
    public ResponseResult showPending(@PathVariable("cardNumber") String cardNumber) {
        try {
            PendingInfoDto dto = cardService.showPending(cardNumber);
            return new ResponseResult(ResponseCode.SUCCESS, dto);
        } catch (IllegalArgumentException e) {
            return new ResponseResult(ResponseCode.ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("请假失败：", e);
            return new ResponseResult(ResponseCode.ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * 请假
     *
     * @param cardNumber
     * @param startTime
     * @param endTime
     * @return
     */
    @PostMapping("/pending/card/{cardNumber}")
    @ResponseBody
    public ResponseResult pendingCard(@PathVariable("cardNumber") String cardNumber, @RequestParam String startTime, @RequestParam String endTime) {
        try {
            cardService.pending(cardNumber, startTime, endTime);
            return new ResponseResult();
        } catch (IllegalArgumentException e) {
            return new ResponseResult(ResponseCode.ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("请假失败：", e);
            return new ResponseResult(ResponseCode.ERROR, null);
        }
    }

    /**
     * 打卡
     *
     * @param cardNumber
     * @return
     */
    @PostMapping("/consume/card/{cardNumber}")
    @ResponseBody
    public ResponseResult consume(@PathVariable("cardNumber") String cardNumber) {
        try {
            cardService.consume(cardNumber);
            return new ResponseResult();
        } catch (IllegalArgumentException e) {
            return new ResponseResult(ResponseCode.ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("打卡失败：", e);
            return new ResponseResult(ResponseCode.ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * 结算前查询消费信息
     *
     * @param cardNumber
     * @return
     */
    @PostMapping("/countPay/card/{cardNumber}")
    @ResponseBody
    public ResponseResult countPay(@PathVariable("cardNumber") String cardNumber) {
        try {
            CountPayDto dto = cardService.countPay(cardNumber);
            return new ResponseResult(ResponseCode.SUCCESS, dto);
        } catch (IllegalArgumentException e) {
            return new ResponseResult(ResponseCode.ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("计算消费金额失败：", e);
            return new ResponseResult(ResponseCode.ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * 结算
     *
     * @param cardNumber
     * @return
     */
    @PostMapping("/pay/card/{cardNumber}")
    @ResponseBody
    public ResponseResult pay(@PathVariable("cardNumber") String cardNumber) {
        try {
            cardService.pay(cardNumber);
            return new ResponseResult();
        } catch (IllegalArgumentException e) {
            return new ResponseResult(ResponseCode.ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("结算失败:", e);
            return new ResponseResult(ResponseCode.ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * 充值
     *
     * @param cardNumber
     * @param dto
     * @return
     */
    @PostMapping("/invest/card/{cardNumber}")
    @ResponseBody
    public ResponseResult invest(@PathVariable("cardNumber") String cardNumber, InverstDto dto) {
        try {
            cardService.invest(dto);
            return new ResponseResult();
        } catch (IllegalArgumentException e) {
            return new ResponseResult(ResponseCode.ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("充值失败：", e);
            return new ResponseResult(ResponseCode.ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * 包座前信息展示
     *
     * @param cardNumber
     * @return
     */
    @GetMapping("/showseat/card/{cardNumber}")
    @ResponseBody
    public ResponseResult showIncludeSeat(@PathVariable("cardNumber") String cardNumber) {
        try {
            SeatDto dto = cardService.showIncludeSeat(cardNumber);
            return new ResponseResult(ResponseCode.SUCCESS, dto);
        } catch (IllegalArgumentException e) {
            return new ResponseResult(ResponseCode.ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("充值失败：", e);
            return new ResponseResult(ResponseCode.ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * 包座
     *
     * @param cardNumber
     * @return
     */
    @PostMapping("/seat/card/{cardNumber}")
    @ResponseBody
    public ResponseResult includeSeat(@PathVariable("cardNumber") String cardNumber,@RequestParam String startTime,
                                      @RequestParam String endTime,@RequestParam String seatNumber,@RequestParam String seatNumberOld) {
        try {
            cardService.includeSeat(cardNumber, startTime, endTime, seatNumber, seatNumberOld);
            return new ResponseResult();
        } catch (IllegalArgumentException e) {
            return new ResponseResult(ResponseCode.ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("充值失败：", e);
            return new ResponseResult(ResponseCode.ERROR.getCode(), e.getMessage());
        }
    }


}