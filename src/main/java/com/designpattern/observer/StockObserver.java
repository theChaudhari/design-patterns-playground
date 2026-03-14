package com.designpattern.observer;

import com.designpattern.model.StockEvent;

public interface StockObserver {
    // Called by Subject whenever stock price changes
    String update(StockEvent event);

    // Unique name of this observer
    String getObserverName();


}
