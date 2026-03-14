package com.designpattern.observer;

import com.designpattern.model.StockEvent;
import com.designpattern.utils.Constants;
import org.springframework.stereotype.Component;

@Component
public class MobileAlertObserver implements StockObserver {

    @Override
    public String update(StockEvent event) {
        // Simulate sending push notification
        return String.format(Constants.MOBILE_ALERT,
                event.getSymbol(),
                event.getNewPrice(),
                event.getTrend());
    }

    @Override
    public String getObserverName() {
        return Constants.MOBILE_OBSERVER;
    }

}