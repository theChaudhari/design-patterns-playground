package com.designpattern.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderResponse {

    private String message;
    private String platform;
    private String item;
    private String address;
    private String deliveryPartner;
    private int estimatedTimeMinutes;
    private double deliveryCharge;

}