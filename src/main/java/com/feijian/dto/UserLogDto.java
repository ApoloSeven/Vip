package com.feijian.dto;

import com.feijian.model.UserLog;
import lombok.Data;

/**
 * @Author ApoloSeven
 * @Date 2020/10/8 18:12
 */
@Data
public class UserLogDto extends UserLog {

    private String operateTimeStr;
}
