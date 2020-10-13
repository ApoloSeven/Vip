package com.feijian.controller;

import com.feijian.dto.CardStatusEnum;
import com.feijian.dto.UserCardDto;
import com.feijian.model.Card;
import com.feijian.model.User;
import com.feijian.response.ResponseCode;
import com.feijian.response.ResponseResult;
import com.feijian.service.CardService;
import com.feijian.service.UserService;
import com.feijian.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller()
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CardService cardService;

    @GetMapping("/userView")
    public String toUserPage() {
        return "vipPage";
    }

    @GetMapping("/user")
    @ResponseBody
    public ResponseResult retrieveUser(@RequestParam Integer userId) {
        try {
            User user = userService.retriveUsersByUserId(userId);
            return new ResponseResult(ResponseCode.SUCCESS, user);
        } catch (Exception e) {
            log.error("查询用户信息失败", e);
            return new ResponseResult(ResponseCode.ERROR.getCode(), e.getMessage());
        }

    }

    @GetMapping("/userCardList")
    @ResponseBody
    public ResponseResult retrieveUser(@RequestParam(required = false) String phone, @RequestParam(required = false) String cardNumber) {
        try {
            if(StringUtils.isEmpty(phone) && StringUtils.isEmpty(cardNumber)){
                return new ResponseResult(ResponseCode.SUCCESS, null);
            }
            List<UserCardDto> list = userService.findUserCardList(phone, cardNumber);
            return new ResponseResult(ResponseCode.SUCCESS, list);
        } catch (Exception e) {
            log.error("查询用户卡片列表信息失败", e);
            return new ResponseResult(ResponseCode.ERROR.getCode(), e.getMessage());
        }

    }

    @PostMapping("/user")
    @ResponseBody
    public ResponseResult addUser(@RequestParam Integer userId, String userName, String phone) {
        try {
            User user = new User();
            user.setUserId(userId);
            user.setUserName(userName);
            user.setPhone(phone);
            userService.insert(user);
            return new ResponseResult();
        } catch (Exception e) {
            log.error("新增或更新用户失败：", e);
            return new ResponseResult(ResponseCode.ERROR.getCode(), e.getMessage());
        }
    }

}
