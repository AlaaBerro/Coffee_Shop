package com.example.coffee_shop.PaymentModel;

import java.util.ArrayList;
import java.util.List;

public class CashPayment implements PaymentStrategy {

    // List to store the observer .
    private List<PaymentObserver> observers = new ArrayList<>();

    // This method will return the type and call the PaymentObserver method .
    @Override
    public String processPayment(double amount) {
        // Implement cash payment logic here
        String paymentType = "Cash";

        // Notify observers of payment completion
        for (PaymentObserver observer : observers) {
            observer.paymentCompleted(paymentType, amount);
        }

        return paymentType;
    }


    // This method always should be before the processPayment() in any other method or code .
    @Override
    public void addObserver(PaymentObserver observer) {
        observers.add(observer);
    }

}