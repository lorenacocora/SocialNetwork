package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.service.Service;
import socialnetwork.utils.MyObserver;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ControllerConversation implements MyObserver {

    private Service service;
    private Stage conversationStage, inboxStage;
    private User user, friend;

    @FXML
    BorderPane borderPane;

    @FXML
    VBox leftBox, rightBox, messagesBox;

    @FXML
    TextField textFieldMessage;

    @FXML
    ScrollPane scrollPane;


    public void setService(Service service, Stage conversationStage, Stage inboxStage, User user, User friend) {
        this.service = service;
        this.conversationStage = conversationStage;
        this.inboxStage = inboxStage;
        this.user = user;
        this.friend = friend;

        loadLabels(service.getConversation(user, friend));
    }

    private void loadLabels(ArrayList<Message> messages){
        leftBox.getChildren().clear();
        leftBox.setAlignment(Pos.TOP_LEFT);
        leftBox.setSpacing(15);
        leftBox.setPadding(new Insets(10));

        rightBox.getChildren().clear();
        rightBox.setAlignment(Pos.TOP_RIGHT);
        rightBox.setSpacing(15);
        rightBox.setPadding(new Insets(10));

        for (Message m : messages) {
            Label label = new Label(m.getMessage());

            label.setFont(Font.font("System", FontWeight.BOLD, 12.0));
            label.setTextFill(Paint.valueOf("#9370DB"));
            BackgroundFill backgroundFill = new BackgroundFill(Paint.valueOf("#ffffff"), new CornerRadii(20), new Insets(-3));
            Background background = new Background(backgroundFill);
            label.setBackground(background);

            if (user.getId().equals(m.getFrom())) {
                rightBox.getChildren().add(label);
                leftBox.getChildren().add(new Label());
            } else {
                leftBox.getChildren().add(label);
                rightBox.getChildren().add(new Label());
            }
        }
        scrollPane.vvalueProperty().bind(rightBox.heightProperty());
    }

    public void handleClose() {
        conversationStage.close();
    }

    public void handleReturn() {
        inboxStage.show();
        conversationStage.close();
    }

    public void handleSend() {
        String message = textFieldMessage.getText();
        try {
            ArrayList<User> friends = new ArrayList<>();
            friends.add(friend);
            Message result = service.sendMessage(user, friends, message, LocalDateTime.now());
            if (result != null)
                MessageAlert.showErrorMessage(null, "Message could not be sent!");

        } catch (Exception ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }

        textFieldMessage.clear();
    }

    @Override
    public void update() {
        loadLabels(service.getConversation(user, friend));
    }


}
