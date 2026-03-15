package com.designpattern.service;

import com.designpattern.model.OrderRequest;
import com.designpattern.model.OrderResponse;
import com.designpattern.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ZomatoDeliveryService implements IDeliveryService {

    @Override
    public OrderResponse placeOrder(OrderRequest request) {
        log.info("Placing order via Zomato - item: {}, address: {}", request.getItem(), request.getAddress());
        return new OrderResponse(String.format(Constants.ORDER_PLACED, getPlatformName()), getPlatformName(), request.getItem(), request.getAddress(), Constants.ZOMATO_PARTNER, getEstimatedTime(), getDeliveryCharge());
    }

    @Override
    public String getPlatformName() {
        return Constants.ZOMATO;
    }

    @Override
    public int getEstimatedTime() {
        return Constants.ZOMATO_TIME;
    }

    @Override
    public double getDeliveryCharge() {
        return Constants.ZOMATO_CHARGE;
    }

}