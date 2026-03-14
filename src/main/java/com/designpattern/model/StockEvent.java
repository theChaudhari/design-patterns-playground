package com.designpattern.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockEvent {

    private String symbol;
    private double oldPrice;
    private double newPrice;
    private String trend;

}
