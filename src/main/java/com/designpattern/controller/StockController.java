package com.designpattern.controller;

import com.designpattern.model.StockResponse;
import com.designpattern.model.StockUpdateRequest;
import com.designpattern.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @PostMapping("/update")
    public StockResponse updateStock(@RequestBody StockUpdateRequest request) {
        return stockService.updateStockPrice(request);
    }

    @PostMapping("/observer/register/{observerName}")
    public String registerObserver(@PathVariable String observerName) {
        return stockService.registerObserverByName(observerName);
    }

    @DeleteMapping("/observer/remove/{observerName}")
    public String removeObserver(@PathVariable String observerName) {
        return stockService.removeObserverByName(observerName);
    }

}