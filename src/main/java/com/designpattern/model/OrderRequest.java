package com.designpattern.model;

import lombok.Data;

@Data
public class OrderRequest {

    private String platform;
    private String item;
    private String address;

}