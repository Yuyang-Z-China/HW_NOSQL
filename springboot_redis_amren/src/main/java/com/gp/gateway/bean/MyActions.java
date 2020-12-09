package com.gp.gateway.bean;
import lombok.Data;

import java.util.List;

@Data
public class MyActions {

    private String name;
    private String describe;
    private List<Retrieve> retrieve;
    private List<Save> save;

}