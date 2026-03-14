package com.designpattern.observer;

import com.designpattern.model.StockEvent;

import java.util.List;

public interface StockSubject {

    void registerObserver(StockObserver observer);

    void removeObserver(StockObserver observer);

    List<String> notifyObservers(StockEvent event);

}