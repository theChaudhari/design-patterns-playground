package com.designpattern.observer;

import com.designpattern.model.StockEvent;
import com.designpattern.utils.Constants;
import org.springframework.stereotype.Component;

@Component
public class SMSAlertObserver implements StockObserver {

    @Override
    public String update(StockEvent event) {
        // Simulate sending SMS alert
        return String.format(Constants.SMS_ALERT,
                event.getSymbol(),
                event.getNewPrice(),
                event.getTrend());
    }

    @Override
    public String getObserverName() {
        return Constants.SMS_OBSERVER;
    }

}