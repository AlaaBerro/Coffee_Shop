package com.example.coffee_shop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;


public class AuthenticationController {
    private DatabaseHandler db ; // Database Instance

    // In login.fxml
    @FXML
    private Label loginmessagelabel;
    @FXML
    private TextField emailfield;
    @FXML
    private PasswordField passwordfield;


    // In register.fxml
    @FXML
    private TextField newemailfield , newnamefield , newphonefield ;

    @FXML
    private PasswordField newpassfield , confirmnewpassfield ;

    @FXML
    private Label registerlabelmessage ;

    @FXML
    private Hyperlink backtologin;


    // method for login validation
    public void loginButtonOnAction(ActionEvent e) throws IOException {
        db = DatabaseHandler.getInstance();
        String email = emailfield.getText() ;
        String pass =  passwordfield.getText();
        if ( !email.isBlank() && !pass.isBlank()) { // if not empty input
            if(db.validateLogin(email, pass)) { // method from database handler
                loginmessagelabel.setText("welcome");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("mainactivity.fxml"));
                Parent root = loader.load();

                Scene activityscene = new Scene(root);
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setTitle("Menu");
                stage.setScene(activityscene);

                stage.show();

            }
            else loginmessagelabel.setText("invalid email or password!");
        }
        else {
            loginmessagelabel.setText("Please Enter your email and password");
        }
    }
    @FXML
    private void handleHyperlinkClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("register.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Register");
        stage.setScene(scene);
        stage.show();
    }

    public void registerButtonOnAction(ActionEvent e) throws IOException {
        db  = DatabaseHandler.getInstance();;
        String name = newnamefield.getText();
        String password = newpassfield.getText();
        String email = newemailfield.getText();
        String phone = newphonefield.getText();
        String confirmpass = confirmnewpassfield.getText();

        String regex = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$"; // Regular expression for email validation


        if(!name.isBlank() && !password.isBlank() && !email.isBlank() && !confirmpass.isBlank() && !phone.isBlank()) {
            if (password.equals(confirmpass) && password.matches(".{8,}")) {
                if (email.matches(regex)) {
                    if(!db.checkEmailUniqueness(email)) {
                        Employee employee = new Employee(name, email, phone, password);
                        db.registerUser(employee.getName(), employee.getEmail(), employee.getPhone(), employee.getPassword());

                        // To be modified ...
                        registerlabelmessage.setText("Your account was created successively");



                        // redirect to acitvity page
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainactivity.fxml"));
                        Parent root = loader.load();

                        Scene activityscene = new Scene(root);
                        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                        stage.setTitle("Menu");
                        stage.setScene(activityscene);

                        stage.show();
                    }
                    else registerlabelmessage.setText("email already taken!");
                }

                else registerlabelmessage.setText("Invalid email!");
            }

            else registerlabelmessage.setText("Passwords didn't match! or invalid password");
        }

        else {
            registerlabelmessage.setText("Please fill all the fields");
        }
    }

    @FXML
    private void goBack() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) backtologin.getScene().getWindow(); // Get the current stage
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }








}