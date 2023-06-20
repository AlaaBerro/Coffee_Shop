package com.example.coffee_shop.PaymentModel;

// This is th observer class .
public interface PaymentObserver {

    // This method will be overridden when creating instance of the PaymentObserver .
    void paymentCompleted(String paymentType, double amount);
}
