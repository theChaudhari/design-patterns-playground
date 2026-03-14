package com.designpattern.service;

import com.designpattern.model.StockEvent;
import com.designpattern.model.StockResponse;
import com.designpattern.model.StockUpdateRequest;
import com.designpattern.observer.StockObserver;
import com.designpattern.observer.StockSubject;
import com.designpattern.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StockService implements StockSubject {

    private final Map<String, StockObserver> observerRegistry = new HashMap<>();
    private final List<StockObserver> activeObservers = new ArrayList<>();
    private final Map<String, Double> stockPrices = new HashMap<>();

    public StockService(List<StockObserver> allObservers) {
        allObservers.forEach(observer -> {
            observerRegistry.put(observer.getObserverName(), observer);
            activeObservers.add(observer);
            log.info("Observer registered at startup: {}", observer.getObserverName());
        });
    }

    @Override
    public void registerObserver(StockObserver observer) {
        activeObservers.add(observer);
        log.info("Observer added: {}", observer.getObserverName());
    }

    @Override
    public void removeObserver(StockObserver observer) {
        activeObservers.removeIf(o -> o.getObserverName().equals(observer.getObserverName()));
        log.info("Observer removed: {}", observer.getObserverName());
    }

    @Override
    public List<String> notifyObservers(StockEvent event) {
        log.info("Notifying {} active observer(s) for stock: {}", activeObservers.size(), event.getSymbol());
        return activeObservers.stream().map(observer -> observer.update(event)).toList();
    }

    public String registerObserverByName(String name) {
        StockObserver observer = observerRegistry.get(name);
        if (observer == null) {
            log.warn("Register failed - observer not found: {}", name);
            return "Observer not found: " + name;
        }
        if (activeObservers.contains(observer)) {
            log.warn("Register skipped - observer already active: {}", name);
            return name + " is already registered.";
        }
        activeObservers.add(observer);
        log.info("Observer activated at runtime: {}", name);
        return Constants.OBSERVER_ADDED + " → " + name;
    }

    public String removeObserverByName(String name) {
        StockObserver observer = observerRegistry.get(name);
        if (observer == null) {
            log.warn("Remove failed - observer not found: {}", name);
            return "Observer not found: " + name;
        }
        activeObservers.remove(observer);
        log.info("Observer deactivated at runtime: {}", name);
        return Constants.OBSERVER_REMOVED + " → " + name;
    }

    public StockResponse updateStockPrice(StockUpdateRequest request) {
        String symbol = request.getSymbol().toUpperCase();
        double newPrice = request.getPrice();
        double oldPrice = stockPrices.getOrDefault(symbol, 0.0);

        stockPrices.put(symbol, newPrice);
        log.info("Stock price updated - symbol: {}, oldPrice: {}, newPrice: {}", symbol, oldPrice, newPrice);

        StockEvent event = new StockEvent(symbol, oldPrice, newPrice, resolveChange(oldPrice, newPrice));
        List<String> notifications = notifyObservers(event);

        return new StockResponse(Constants.STOCK_UPDATED, event, notifications);
    }

    private String resolveChange(double oldPrice, double newPrice) {
        if (newPrice > oldPrice) return Constants.PRICE_UP;
        if (newPrice < oldPrice) return Constants.PRICE_DOWN;
        return Constants.PRICE_SAME;
    }

}