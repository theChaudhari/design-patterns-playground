package com.designpattern.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    // Platform names
    public final String ZOMATO = "ZOMATO";
    public final String SWIGGY = "SWIGGY";
    public final String UBEREATS = "UBEREATS";

    // Delivery partners
    public final String ZOMATO_PARTNER = "Zomato Delivery";
    public final String SWIGGY_PARTNER = "Swiggy Genie";
    public final String UBEREATS_PARTNER = "Uber Eats Runner";

    // Estimated delivery times (minutes)
    public final int ZOMATO_TIME = 30;
    public final int SWIGGY_TIME = 25;
    public final int UBEREATS_TIME = 35;

    // Delivery charges
    public final double ZOMATO_CHARGE = 29.00;
    public final double SWIGGY_CHARGE = 0.00;
    public final double UBEREATS_CHARGE = 49.00;

    // Messages
    public final String ORDER_PLACED = "Order placed successfully via %s!";
    public final String UNSUPPORTED_PLATFORM = "Unsupported delivery platform: %s. Supported: ZOMATO, SWIGGY, UBEREATS";
}
