package com.designpattern.model;

import lombok.Data;

@Data
public class StockUpdateRequest {

    private String symbol;
    private double price;

}