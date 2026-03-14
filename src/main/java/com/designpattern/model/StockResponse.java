package com.designpattern.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StockResponse {

    private String message;
    private StockEvent event;
    private List<String> notifications;   // all observer responses

}
