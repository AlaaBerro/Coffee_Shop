package com.example.coffee_shop;

import com.example.coffee_shop.ProductModel.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.*;

import java.io.IOException;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.Optional;

public class ActivityController {


    @FXML
    private Hyperlink addproduct ;

    @FXML
    private GridPane gridpane;

    private DatabaseHandler db;

    private  int talabiyeId ;



    @FXML
    public void gridfiller() {
        // getting instance from database
        db = DatabaseHandler.getInstance();
        ArrayList<Object> array = new ArrayList<Object>();

        array = db.getProducts();

        // aam e5la2 l rows
        for (int i = 0; i < array.size(); i++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight(10000);
            gridpane.getRowConstraints().add(row);
        }



        int row = 1 ;
        int c=0;
        for (Object o : array) {

            String description;
            Double cost;
            int id ;
            int quantity ;

            if (o instanceof Espresso) {
                Espresso e = (Espresso) o;
                // Access methods of the Coffee class using the 'c' reference variable
                 id = e.getID();
                 description = e.getDescription();
                 cost = e.getCost();
            } else if (o instanceof IcedTea) {
                IcedTea i = (IcedTea) o;
                // Access methods of the Tea class using the 't' reference variable
                  id = i.getID();
                 description = i.getDescription();
                 cost = i.getCost();
            } else if (o instanceof Latte) {
                Latte l = (Latte) o;
                // Access methods of the Pastry class using the 'p' reference variable
                id = l.getID();
                 description = l.getDescription();
                 cost = l.getCost();
            }
            else if (o instanceof Water) {
                Water w = (Water) o;
                // Access methods of the Pastry class using the 'p' reference variable
                id = w.getID();
                description = w.getDescription();
                cost = w.getCost();
            }
        else if (o instanceof Muffin) {
            Muffin m = (Muffin) o;
            // Access methods of the Pastry class using the 'p' reference variable
            id = m.getID();
            description = m.getDescription();
            cost = m.getCost();
        }
    else{
        Donuts d = (Donuts) o;
        // Access methods of the Pastry class using the 'p' reference variable
        id = d.getID();
        description = d.getDescription();
        cost = d.getCost();
    }


            VBox vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);

            Label nameLabel1 = new Label(description);
            Label nameLabel2 = new Label(cost.toString()+"$");

            Button b = new Button("add");
            Label label = new Label("Choose a number:");
            ChoiceBox<Integer> choiceBox = new ChoiceBox<>();
            choiceBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            choiceBox.setValue(1);
            vBox.getChildren().addAll(nameLabel1,nameLabel2,choiceBox, b);


            vBox.setBorder(new Border(new BorderStroke(Color.web("#6F4E37"),BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            gridpane.add(vBox,c , row);
            c++;
            if(c>2)
            {
                c=0;
                row++;
            }
            // here to be modified : the order id
            b.setOnAction(e -> {
                db.addOrderItem(getTalabiyeId(),id,choiceBox.getValue(),cost);
                b.setDisable(true);
            });
        }
    }


    // methods to save the order id between pages .
    public int getTalabiyeId() {
        return this.talabiyeId ;
    }

    public void setTalabiyeId(int id) {
        this.talabiyeId = id ;
    }

    @FXML
    public void calculateTotal(ActionEvent event) throws IOException {

        db = DatabaseHandler.getInstance();
        // Call the method in the database that will return the sum and save it in the total variable.
        double total = db.getOrderTotal(getTalabiyeId());

        if (total == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Your order is empty!");
            alert.showAndWait();
            return;
        }

        // Redirect to the payment page where the payment type should be updated.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("payment.fxml"));
        Parent root = loader.load();

        PaymentController pc = loader.getController();
        // Send the id of the order to the PaymentController class.
        pc.setorderId(getTalabiyeId());
        // Save the total and send it to the payment page.
        pc.setTotal(total);

        Scene activityscene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Payment");
        stage.setScene(activityscene);

        stage.show();
    }



    @FXML
    public void cancelOrder(ActionEvent event) throws IOException {

        // Connect to the database .
        db = DatabaseHandler.getInstance();
        // Call the delete order method in DatabaseHandler .
        db.deleteOrder(getTalabiyeId());

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
