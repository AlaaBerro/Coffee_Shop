package com.example.coffee_shop;

import com.example.coffee_shop.PaymentModel.*;
import com.example.coffee_shop.PaymentModel.CreditCardPayment;
import com.example.coffee_shop.PaymentModel.PaymentStrategy;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;

import java.io.IOException;

public class PaymentController {

    @FXML
    private Label totallabel ;

    @FXML
    private RadioButton cashradio , creditcardradio ;

    private  int orderId ;
    private double total ;
    private DatabaseHandler db;
    public int getorderId() {
        return orderId;
    }

    public void setorderId(int id) {
        this.orderId = id;
    }

    public double gettotal() {
        return total ;
    }

    public void setTotal(double t) {
        this.total = t ;
        totallabel.setText(gettotal() + " $\nSelect payment method : ");
    }


    @FXML
    public void confirmpaymenttype(ActionEvent event) throws IOException {

        // Connect to the database.
        db = DatabaseHandler.getInstance();

        // Select payment strategy.
        PaymentStrategy strategy;
        String s;

        if(cashradio.isSelected() && creditcardradio.isSelected()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Payment Method Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please choose only one");
            alert.showAndWait();
            return;
        }

        if (cashradio.isSelected()) {
            strategy = new CashPayment();
            // Add observer to payment strategy
            strategy.addObserver(new PaymentObserver() {
                @Override
                public void paymentCompleted(String paymentType, double amount) {
                    // Handle payment completion event.
                    String message = paymentType + " payment of $" + amount + " completed.";
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Payment Completed");
                    alert.setHeaderText(null);
                    alert.setContentText(message);
                    Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                    stage.setAlwaysOnTop(true);
                    stage.toFront();
                    alert.showAndWait();
                }
            });
            // here it seems recursion , this method will recall the written paymentCompleted() method above .
            s = strategy.processPayment(gettotal());
            creditcardradio.setSelected(false);

        } else if (creditcardradio.isSelected()) {
            strategy = new CreditCardPayment("XXX", "XXX", "XXX");
            // Add observer to payment strategy
            strategy.addObserver(new PaymentObserver() {
                @Override
                public void paymentCompleted(String paymentType, double amount) {
                    // Handle payment completion event.
                    String message = paymentType + " payment of $" + amount + " completed.";
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Payment Completed");
                    alert.setHeaderText(null);
                    alert.setContentText(message);
                    Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                    stage.setAlwaysOnTop(true);
                    stage.toFront();
                    alert.showAndWait();
                }
            });
            // here it seems recursion , this method will recall the written paymentCompleted() method above .
            s = strategy.processPayment(gettotal());
            cashradio.setSelected(false);

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Payment Method Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a payment method.");
            alert.showAndWait();
            return;
        }

        // Call the update payment type method in the order in DatabaseHandler.
        db.setPaymentType(getorderId(), s);

        // Redirect to the main activity page.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainactivity.fxml"));
        Parent root = loader.load();
        Scene activityscene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Menu");
        stage.setScene(activityscene);
        stage.show();
    }


    @FXML
    public void cancelOrder(ActionEvent event) throws IOException {

        // Connect to the database .
        db = DatabaseHandler.getInstance();
        // Call the delete order method in DatabaseHandler .
        db.deleteOrder(getorderId());

        // Redirect to the mainactivty page .
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainactivity.fxml"));
        Parent root = loader.load();
        Scene activityscene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Menu");
        stage.setScene(activityscene);
        stage.show();

        //System.out.println("deleted");
    }


}
