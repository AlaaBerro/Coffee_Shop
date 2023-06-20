package com.example.coffee_shop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;

public class ProductController {

    @FXML
    private ChoiceBox typechoicebox , quantitychoicebox ;

    @FXML
    private TextField pricebox ;

    private DatabaseHandler db;

    public void fill() {
        // Clear the existing choices
        typechoicebox.getItems().clear();
        quantitychoicebox.getItems().clear();

        // Get the choices dynamically
        List<String> types = getTypes();
        List<String> quantities = getQuantities();

        // Add the choices to the ChoiceBox
        typechoicebox.getItems().addAll(types);
        quantitychoicebox.getItems().addAll(quantities);
        typechoicebox.setValue(types.get(0));
        quantitychoicebox.setValue(quantities.get(0));
    }

    private List<String> getTypes() {
        // set types
        return Arrays.asList("Muffin", "Donuts", "Espresso","IcedTea","Latte","Water");
    }

    private List<String> getQuantities() {
        // set quantity
        return Arrays.asList("10", "20", "30", "40", "50");
    }

    public void addProduct(ActionEvent event) throws IOException {
        db = DatabaseHandler.getInstance();
        String priceText = pricebox.getText();
        // check if priceText is a valid double
        try {
            double price = Double.parseDouble(priceText);
            if (price <= 0.00) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Price must be greater than 0.00");
                alert.showAndWait();
                return;
            }
            db.insertProduct(typechoicebox.getValue().toString(), price, Integer.parseInt((String) quantitychoicebox.getValue()));
            // Redirect to mainactivity page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainactivity.fxml"));
            Parent root = loader.load();
            Scene activityscene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Menu");
            stage.setScene(activityscene);
            stage.show();
        } catch (NumberFormatException e) {
            // display error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid price");
            alert.setContentText("Price should be a valid number.");
            alert.showAndWait();
        }
    }

    public void cancelProduct(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainactivity.fxml"));
        Parent root = loader.load();
        Scene activityscene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Menu");
        stage.setScene(activityscene);
        stage.show();
    }



}
