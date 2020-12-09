package com.gp.gateway.vo;

import lombok.Data;

/**
 * @Author : amrengp
 * @Describe : 统一返回体
 */
@Data
public class ResultBody {
    /**
     * 响应代码
     */
    private String code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 总条数
     */
    private Object data = 0;


    public ResultBody() {
    }

    public ResultBody(String msg, String code) {
        this.code = code;
        this.msg = msg;
    }


    /**
     * 成功-当页面查询成功了,ajax返回,设置返回内容
     *
     * @return
     */
    public static ResultBody success(String msg) {
        ResultBody rb = new ResultBody();
        rb.setCode("200");
        rb.setMsg(msg);
        return rb;
    }


    /**
     * 失败
     */
    public static ResultBody error(String msg) {
        ResultBody rb = new ResultBody();
        rb.setCode("500");
        rb.setMsg(msg);
        return rb;
    }

}
