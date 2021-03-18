package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import socialnetwork.domain.FriendDataDTO;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.service.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class ControllerAdmin {


    private Service service;
    private Stage adminStage;
    private Stage loginStage;
    private ArrayList<CheckBox> checkBoxArrayListUsers = new ArrayList<>();
    private ArrayList<CheckBox> checkBoxArrayListFriends = new ArrayList<>();
    private double xOffset = 0;
    private double yOffset = 0;
    private int indexUsers=0;
    private int indexSenders=0;

    @FXML
    VBox usersBox;

    @FXML
    VBox sendersBox;

    @FXML
    DatePicker startDatePicker, endDatePicker;


    public void setService(Service service, Stage adminStage, Stage loginStage) {
        this.service = service;
        this.adminStage = adminStage;
        this.loginStage = loginStage;

        loadCheckboxUsers(service.getPageWithUsers(indexUsers));
        loadCheckboxSenders(service.getPageWithUsers(indexUsers));
    }

    private void loadCheckboxSenders(ArrayList<User> senders) {

        sendersBox.getChildren().clear();
        sendersBox.setSpacing(10);
        sendersBox.setPadding(new Insets(10));

        for (User s : senders) {
            CheckBox checkBoxSender = new CheckBox(s.getFullname());
            sendersBox.getChildren().add(checkBoxSender);

            checkBoxSender.setFont(Font.font("System", FontWeight.BOLD, 12.0));
            checkBoxSender.setTextFill(Paint.valueOf("#ffebcd"));
            checkBoxSender.setLineSpacing(5);


            EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    if(checkBoxSender.isSelected()){
                        checkBoxArrayListFriends.add(checkBoxSender);
                    }else{
                        checkBoxArrayListFriends.remove(checkBoxSender);
                    }
                }

            };
            checkBoxSender.setOnAction(event);
        }
    }

    private void loadCheckboxUsers(ArrayList<User> users){
        usersBox.getChildren().clear();
        usersBox.setSpacing(10);
        usersBox.setPadding(new Insets(10));

        for (User u : users) {
            CheckBox checkBoxUser = new CheckBox(u.getFullname());
            usersBox.getChildren().add(checkBoxUser);

            checkBoxUser.setFont(Font.font("System", FontWeight.BOLD, 12.0));
            checkBoxUser.setTextFill(Paint.valueOf("#ffebcd"));
            checkBoxUser.setLineSpacing(5);

            EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    if (checkBoxUser.isSelected()) {
                        checkBoxArrayListUsers.add(checkBoxUser);
                    } else {
                        checkBoxArrayListUsers.remove(checkBoxUser);
                    }
                }

            };
            checkBoxUser.setOnAction(event);
        }

    }


    @FXML
    public void handleNoCommunities() {
        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Number of communities", Integer.toString(service.numberOfCommunities()));
    }

    @FXML
    public void handleLargestCommunity() {

        String largestCommunity = "";
        ArrayList<Long> path = service.getLongestPath();

        if (path.size() != 0) {
            for (Long id : path) {
                largestCommunity += service.getUser(id).getFullname() + "\n";
            }
        } else largestCommunity = "There is no comunity!";

        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Largest community", largestCommunity);
    }

    public void handleRaport1() throws Exception {

        if(checkBoxArrayListUsers.size()!=0){
            if(startDatePicker.getValue()!=null && endDatePicker.getValue()!=null){
                for(CheckBox checkBox : checkBoxArrayListUsers){
                    String[] args = checkBox.getText().split(" ");
                    User user = service.getUserByName(args[0], args[1]);
                    ArrayList<FriendDataDTO> friends = service.getFriendshipsByPeriod(startDatePicker.getValue(),endDatePicker.getValue(),user);
                    ArrayList<Message> messages = service.getMessagesByPeriod(startDatePicker.getValue(),endDatePicker.getValue(),user);
                    Map<String, Integer> mapMessages= service.getRaportNumberOfMessagesBySender(startDatePicker.getValue(),endDatePicker.getValue(),user);

                    FXMLLoader raport1Loader=new FXMLLoader();
                    raport1Loader.setLocation(getClass().getResource("/view/raport1.fxml"));
                    Parent root = raport1Loader.load();

                    Stage raport1Stage= new Stage();
                    raport1Stage.initStyle(StageStyle.TRANSPARENT);
                    ControllerRaport1 ctrRaport1=raport1Loader.getController();
                    ctrRaport1.setService(service, raport1Stage, adminStage, messages, friends, mapMessages, user, startDatePicker.getValue(), endDatePicker.getValue());

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
                            raport1Stage.setX(event.getScreenX() - xOffset);
                            raport1Stage.setY(event.getScreenY() - yOffset);
                        }
                    });

                    Scene scene = new Scene(root, 700, 500);
                    scene.setFill(Color.TRANSPARENT);
                    raport1Stage.setScene(scene);
                    raport1Stage.show();
                    adminStage.close();
                }
            }
            else MessageAlert.showErrorMessage(null,"Set the period for the raport!");

        }
        else MessageAlert.showErrorMessage(null,"Choose at least one user for the raport!");

    }

    public void handleRaport2() throws Exception {

        if(checkBoxArrayListFriends.size() != 0 && checkBoxArrayListUsers.size() != 0){
            if(startDatePicker.getValue()!=null && endDatePicker.getValue()!=null){
                for(CheckBox checkBox : checkBoxArrayListUsers){
                    String[] args = checkBox.getText().split(" ");
                    User user = service.getUserByName(args[0], args[1]);
                    for(CheckBox checkBoxSender : checkBoxArrayListFriends){
                        String[] args2 = checkBoxSender.getText().split(" ");
                        User sender = service.getUserByName(args2[0], args2[1]);
                        ArrayList<Message> messages = service.getMessagesByPeriodAndSender(startDatePicker.getValue(),endDatePicker.getValue(),user,sender);
                        Map<LocalDate, Integer> mapMessages= service.getRaportNumberOfMessagesByDate(startDatePicker.getValue(),endDatePicker.getValue(),user, sender);

                        FXMLLoader raport2Loader=new FXMLLoader();
                        raport2Loader.setLocation(getClass().getResource("/view/raport2.fxml"));
                        Parent root = raport2Loader.load();

                        Stage raport2Stage= new Stage();
                        raport2Stage.initStyle(StageStyle.TRANSPARENT);
                        ControllerRaport2 ctrRaport2=raport2Loader.getController();
                        ctrRaport2.setService(service, raport2Stage, adminStage, messages, user, sender, mapMessages, startDatePicker.getValue(), endDatePicker.getValue());

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
                                raport2Stage.setX(event.getScreenX() - xOffset);
                                raport2Stage.setY(event.getScreenY() - yOffset);
                            }
                        });

                        Scene scene = new Scene(root, 700, 500);
                        scene.setFill(Color.TRANSPARENT);
                        raport2Stage.setScene(scene);
                        raport2Stage.show();
                        adminStage.close();
                    }

                }
            }
            else MessageAlert.showErrorMessage(null,"Set the period for the raport!");

        }
        else MessageAlert.showErrorMessage(null,"Choose at least one user and one sender for the raport!");
    }

    @FXML
    public void handleReturn() {
        loginStage.show();
        adminStage.close();
    }

    @FXML
    public void handleClose() {
        adminStage.close();
    }

    public void handlePrevUser() {
        try {
            loadCheckboxUsers(service.getPrevPageWithUsers(indexUsers));
            indexUsers-=10;
        } catch (Exception ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }

    public void handleNextUser() {
        try {
            loadCheckboxUsers(service.getNextPageWithUsers(indexUsers));
            indexUsers+=10;
        } catch (Exception ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }

    public void handlePrevSender() {
        try {
            loadCheckboxSenders(service.getPrevPageWithUsers(indexSenders));
            indexSenders-=10;
        } catch (Exception ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }

    public void handleNextSender() {
        try {
            loadCheckboxSenders(service.getNextPageWithUsers(indexSenders));
            indexSenders+=10;
        } catch (Exception ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }
}
