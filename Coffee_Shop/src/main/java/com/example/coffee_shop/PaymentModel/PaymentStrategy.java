package com.example.coffee_shop.PaymentModel;


// This is the observable class .
public interface PaymentStrategy {

    // This will only return a String .
    String processPayment(double amount);

    void addObserver(PaymentObserver observer);

}
