package com.gp.gateway.bean;

import lombok.Data;

@Data
public class MyCounters {

    private String counterName;
    private String counterIndex;
    private String type;
    private String keyFields;
    private String fields;
    private String valueFields;
    private int maxSize;
    private int expireTime;

}