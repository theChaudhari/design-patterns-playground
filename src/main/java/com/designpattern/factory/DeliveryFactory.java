package com.designpattern.factory;

import com.designpattern.service.IDeliveryService;
import com.designpattern.service.SwiggyDeliveryService;
import com.designpattern.service.UberEatsDeliveryService;
import com.designpattern.service.ZomatoDeliveryService;
import com.designpattern.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class DeliveryFactory {

    private final Map<String, IDeliveryService> deliveryServiceMap;

    // Spring auto-injects all IDeliveryService beans into the map
    // key = bean name (class name with lowercase first letter)
    // We build a cleaner platform-name keyed map manually
    public DeliveryFactory(ZomatoDeliveryService zomato, SwiggyDeliveryService swiggy, UberEatsDeliveryService uberEats) {
        this.deliveryServiceMap = Map.of(Constants.ZOMATO, zomato, Constants.SWIGGY, swiggy, Constants.UBEREATS, uberEats);
        log.info("DeliveryFactory initialized with {} platforms: {}", deliveryServiceMap.size(), deliveryServiceMap.keySet());
    }

    public IDeliveryService getDeliveryService(String platform) {
        if (platform == null || platform.isBlank()) {
            log.warn("Factory called with null/blank platform");
            throw new IllegalArgumentException(String.format(Constants.UNSUPPORTED_PLATFORM, platform));
        }

        String key = platform.toUpperCase();
        IDeliveryService service = deliveryServiceMap.get(key);

        if (service == null) {
            log.warn("Unsupported platform requested: {}", platform);
            throw new IllegalArgumentException(String.format(Constants.UNSUPPORTED_PLATFORM, platform));
        }

        log.info("Factory resolved platform: {} → {}", platform, service.getClass().getSimpleName());
        return service;
    }

}