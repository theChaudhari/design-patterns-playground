package com.designpattern.service;

import com.designpattern.model.OrderRequest;
import com.designpattern.model.OrderResponse;

public interface IDeliveryService {

    OrderResponse placeOrder(OrderRequest request);

    String getPlatformName();

    int getEstimatedTime();

    double getDeliveryCharge();

}