package socialnetwork.controller;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import socialnetwork.domain.Event;
import socialnetwork.domain.Password;
import socialnetwork.domain.User;
import socialnetwork.service.Service;

import javax.swing.*;
import java.util.ArrayList;

public class ControllerLogin {

    private Service service;
    private Stage loginStage;
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;


    public void setService(Service service, Stage loginStage){
        this.service=service;
        this.loginStage=loginStage;
    }


    @FXML
    public void handleClose(){
        loginStage.close();
    }

    @FXML
    public void handleSignUp() throws Exception{
        FXMLLoader loaderSignUp=new FXMLLoader();
        loaderSignUp.setLocation(getClass().getResource("/view/signUp.fxml"));
        Parent root = loaderSignUp.load();

        Stage signUpStage= new Stage();
        signUpStage.initStyle(StageStyle.TRANSPARENT);
        ControllerSignUp ctrSignUp=loaderSignUp.getController();
        ctrSignUp.setService(service, signUpStage, loginStage);

        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                signUpStage.setX(event.getScreenX() - xOffset);
                signUpStage.setY(event.getScreenY() - yOffset);
            }
        });

        Scene scene = new Scene(root, 500,350 );
        scene.setFill(Color.TRANSPARENT);
        signUpStage.setScene(scene);
        signUpStage.show();
        loginStage.close();

    }

    @FXML
    public void handleLogin() throws Exception{
        String username= usernameField.getText();
        String password= passwordField.getText();
        usernameField.clear();
        passwordField.clear();

        User user= service.getUserByUsername(username);
        if(user==null) {
            MessageAlert.showErrorMessage(null, "User not found!");
            return;
        }

        try{
            if(Password.check(password,user.getSaltHashedPassword())==false){
                MessageAlert.showErrorMessage(null,"Incorrect password!");
                return;
            }
        }
        catch(Exception ex){
            MessageAlert.showErrorMessage(null,"Incorrect password!");
            return;
        }


        FXMLLoader loaderMainMenu=new FXMLLoader();
        loaderMainMenu.setLocation(getClass().getResource("/view/mainMenu.fxml"));
        Parent root = loaderMainMenu.load();

        Stage mainMenuStage= new Stage();
        mainMenuStage.initStyle(StageStyle.TRANSPARENT);
        ControllerMainMenu ctrMainMenu=loaderMainMenu.getController();
        ctrMainMenu.setService(service, mainMenuStage, loginStage, user);
        service.addObserver(ctrMainMenu);

        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mainMenuStage.setX(event.getScreenX() - xOffset);
                mainMenuStage.setY(event.getScreenY() - yOffset);
            }
        });

        Scene scene = new Scene(root, 700, 500);
        scene.setFill(Color.TRANSPARENT);
        mainMenuStage.setScene(scene);
        mainMenuStage.show();
        loginStage.close();

        String notifications="";

        ArrayList<Event> events = service.getEventsToNotifyByUser(user);
        if(events.isEmpty())
            notifications="There are no upcoming events for you!";
        else for(Event e : events)
            notifications+=e+"\n";

        Alert alert = new Alert(Alert.AlertType.INFORMATION, notifications, ButtonType.OK);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setHeaderText("Upcoming events for the next week");
        alert.show();
    }

    @FXML
    public void handleAdminClick() throws Exception{
        FXMLLoader loaderAdminMenu=new FXMLLoader();
        loaderAdminMenu.setLocation(getClass().getResource("/view/admin.fxml"));
        Parent root = loaderAdminMenu.load();

        Stage adminStage= new Stage();
        adminStage.initStyle(StageStyle.TRANSPARENT);
        ControllerAdmin ctrAdmin=loaderAdminMenu.getController();
        ctrAdmin.setService(service, adminStage, loginStage);

        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                adminStage.setX(event.getScreenX() - xOffset);
                adminStage.setY(event.getScreenY() - yOffset);
            }
        });

        Scene scene = new Scene(root, 700, 500);
        scene.setFill(Color.TRANSPARENT);
        adminStage.setScene(scene);
        adminStage.show();
        loginStage.close();
    }

    @FXML
    public void handleForgot() throws Exception{
        String username= usernameField.getText();
        usernameField.clear();

        User user= service.getUserByUsername(username);
        if(user==null) {
            MessageAlert.showErrorMessage(null, "User not found!");
            return;
        }

        FXMLLoader loaderMainMenu=new FXMLLoader();
        loaderMainMenu.setLocation(getClass().getResource("/view/mainMenu.fxml"));
        Parent root = loaderMainMenu.load();

        Stage mainMenuStage= new Stage();
        mainMenuStage.initStyle(StageStyle.TRANSPARENT);
        ControllerMainMenu ctrMainMenu=loaderMainMenu.getController();
        ctrMainMenu.setService(service, mainMenuStage, loginStage, user);
        service.addObserver(ctrMainMenu);

        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mainMenuStage.setX(event.getScreenX() - xOffset);
                mainMenuStage.setY(event.getScreenY() - yOffset);
            }
        });

        Scene scene = new Scene(root, 700, 500);
        scene.setFill(Color.TRANSPARENT);
        mainMenuStage.setScene(scene);
        mainMenuStage.show();
        loginStage.close();

        String notifications="";

        ArrayList<Event> events = service.getEventsToNotifyByUser(user);
        if(events.isEmpty())
            notifications="There are no upcoming events for you!";
        else for(Event e : events)
            notifications+=e+"\n";

        Alert alert = new Alert(Alert.AlertType.INFORMATION, notifications, ButtonType.OK);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setHeaderText("Upcoming events for the next week");
        alert.show();
    }

}
