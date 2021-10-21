package com.teamwork.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Lsutin
 * @Date: 2021/10/21 20:35
 * @describe:
 */
@Data
@Accessors(chain = true)
public class ResultInfo {
    private int code;
    private String message;
    private Object result;

    public static ResultInfo ok(String message){
        ResultInfo resultInfo = new ResultInfo().setCode(200).setMessage(message);
        return resultInfo;
    }

    public static ResultInfo ok(Object result){
        ResultInfo resultInfo = new ResultInfo().setCode(200).setResult(result);
        return resultInfo;
    }

    public static ResultInfo error(String message){
        ResultInfo resultInfo = new ResultInfo().setCode(400).setMessage(message);
        return resultInfo;
    }
}
