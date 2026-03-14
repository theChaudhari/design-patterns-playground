package com.designpattern.observer;

import com.designpattern.model.StockEvent;
import com.designpattern.utils.Constants;
import org.springframework.stereotype.Component;

@Component
public class EmailAlertObserver implements StockObserver {

    @Override
    public String update(StockEvent event) {
        // Simulate sending email alert
        return String.format(Constants.EMAIL_ALERT,
                event.getSymbol(),
                event.getNewPrice(),
                event.getTrend());
    }

    @Override
    public String getObserverName() {
        return Constants.EMAIL_OBSERVER;
    }

}