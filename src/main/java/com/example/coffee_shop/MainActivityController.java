package com.example.coffee_shop;

import com.example.coffee_shop.ActivityController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EventListener;

public class MainActivityController {
    private DatabaseHandler db ;

    @FXML
    private Hyperlink logout ;

    @FXML
    private VBox vboxhistory;
    @FXML
    public void CreateNewOrder(ActionEvent e) throws IOException {

        // Create the order table in database where the ordered items will bes saved
        db = DatabaseHandler.getInstance();
        // save this method in int id to be assigned to each ordered item .
        int id = db.createOrder();

        // Redirect to activity page
        FXMLLoader loader = new FXMLLoader(getClass().getResource("activity.fxml"));
        Parent root = loader.load();

        // Create instance of ActivityController
        ActivityController ac = loader.getController();
        // call the gridfiller method that will create the rows .
        ac.gridfiller();
        // save the id of the new created order
        ac.setTalabiyeId(id);

        Scene activityscene = new Scene(root);
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setTitle("Activity");
        stage.setScene(activityscene);
        stage.show();
    }



    // Declare an ArrayList to hold all the labels that have been added
    private ArrayList<Label> labels = new ArrayList<>();

    @FXML
    public void ShowHistory(ActionEvent e) throws IOException {

        // Connect to database.
        db = DatabaseHandler.getInstance();
        // Declare the ArrayList that will save the history from the database.
        ArrayList<String> A = db.getAllOrders();

        // Loop through the ArrayList and create a label for each item.
        for (String s : A) {
            Label label = new Label(s + "\n");
            // Add some padding between labels.
            label.setPadding(new Insets(5, 0, 5, 0));
            // Add the label to the VBox.
            vboxhistory.getChildren().add(label);
            // Add the label to the ArrayList
            labels.add(label);
        }
    }


    // This method is to delete all records for orders and ordered items in database.
    // also this method will remove all the labels created by show history method .
    @FXML
    private void deleteorders(ActionEvent e) throws SQLException {

        // Remove all the labels from the VBox
        vboxhistory.getChildren().removeAll(labels);

        // Clear the ArrayList
        labels.clear();

        // From DatabaseHandler .
        db = DatabaseHandler.getInstance();
        db.deleteAllOrders();
    }

    // method to open the add product page .
    @FXML
    private void addProduct(ActionEvent e) throws IOException {

        // Redirect to activity page
        FXMLLoader loader = new FXMLLoader(getClass().getResource("manage.fxml"));
        Parent root = loader.load();

        // Create instance of ProductController
        ProductController pc = loader.getController();
        // This method will fill the choiceBoxes .
        pc.fill();

        Scene activityscene = new Scene(root);
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setTitle("Manage Products");
        stage.setScene(activityscene);
        stage.show();

    }


    @FXML
    private void handleLogout() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) logout.getScene().getWindow(); // Get the current stage
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

}
