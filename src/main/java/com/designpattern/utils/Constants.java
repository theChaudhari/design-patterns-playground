package com.designpattern.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    // Stock Names
    public final String TATA = "TATA";
    public final String JIO = "JIO";
    public final String TESLA = "TESLA";

    // Observer Names
    public final String EMAIL_OBSERVER = "EmailAlertObserver";
    public final String SMS_OBSERVER = "SMSAlertObserver";
    public final String MOBILE_OBSERVER = "MobileAlertObserver";

    // Messages
    public final String EMAIL_ALERT = "Email Alert Sent → Stock: %s | Price: ₹%.2f | Change: %s";
    public final String SMS_ALERT = "SMS Alert Sent → Stock: %s | Price: ₹%.2f | Change: %s";
    public final String MOBILE_ALERT = "Push Notification  → Stock: %s | Price: ₹%.2f | Change: %s";

    public final String PRICE_UP = "📈 UP";
    public final String PRICE_DOWN = "📉 DOWN";
    public final String PRICE_SAME = "➡️ NO CHANGE";

    public final String STOCK_UPDATED = "Stock price updated & all observers notified.";
    public final String OBSERVER_ADDED = "Observer registered successfully.";
    public final String OBSERVER_REMOVED = "Observer unregistered successfully.";
}
