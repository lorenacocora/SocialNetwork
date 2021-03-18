package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.service.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ControllerInbox {

    private Service service;
    private Stage inboxStage, mainMenuStage;
    private User user;
    private ArrayList<CheckBox> checkBoxArrayList = new ArrayList<>();
    private double xOffset = 0;
    private double yOffset = 0;
    private int indexPage=0;

    @FXML
    TextField textFieldSearch;

    @FXML
    VBox usersBox;

    public void setService(Service service, Stage inboxStage, Stage mainMenuStage, User user) {
        this.service = service;
        this.inboxStage = inboxStage;
        this.mainMenuStage = mainMenuStage;
        this.user = user;
        loadCheckbox(service.getPageWithUsers(indexPage));
    }


    @FXML
    public void initialize() {
        textFieldSearch.textProperty().addListener(x->handleFilterName());
    }



    private void loadCheckbox(ArrayList<User> users){

        usersBox.getChildren().clear();
        usersBox.setPadding(new Insets(10));

        for(User u : users){
            CheckBox checkBox=new CheckBox(u.getFullname());
            usersBox.getChildren().add(checkBox);

            checkBox.setFont(Font.font("System", FontWeight.BOLD, 12.0));
            checkBox.setTextFill(Paint.valueOf("#ffebcd"));
            checkBox.setLineSpacing(5);
            usersBox.setSpacing(10);

            EventHandler<ActionEvent> event = new EventHandler<ActionEvent>()
            {
                public void handle(ActionEvent e)
                {
                    if (checkBox.isSelected())
                    {
                        checkBoxArrayList.add(checkBox);
                    }

                    else
                    {
                        checkBoxArrayList.remove(checkBox);
                    }
                }

            };
            checkBox.setOnAction(event);
        }
    }

    private void handleFilterName() {
        ArrayList<User> users = (ArrayList<User>) service.getAllUsers()
                .stream()
                .filter(u->u.getFirstname().startsWith(textFieldSearch.getText())||u.getLastname().startsWith(textFieldSearch.getText()))
                .collect(Collectors.toList());

        loadCheckbox(users);
    }

    public void handleConversate() throws Exception{

        if(checkBoxArrayList.size()!=0) {
            for(CheckBox checkBox : checkBoxArrayList){
                FXMLLoader loaderConversation=new FXMLLoader();
                loaderConversation.setLocation(getClass().getResource("/view/conversation.fxml"));
                Parent root = loaderConversation.load();

                Stage conversationStage= new Stage();
                conversationStage.initStyle(StageStyle.TRANSPARENT);
                ControllerConversation controllerConversation = loaderConversation.getController();
                String[] args = checkBox.getText().split(" ");
                controllerConversation.setService(service, conversationStage, inboxStage, user, service.getUserByName(args[0],args[1]));
                service.addObserver(controllerConversation);

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
                        conversationStage.setX(event.getScreenX() - xOffset);
                        conversationStage.setY(event.getScreenY() - yOffset);
                    }
                });

                Scene scene = new Scene(root, 700, 500);
                scene.setFill(Color.TRANSPARENT);
                conversationStage.setScene(scene);
                conversationStage.show();
                inboxStage.close();
            }
        }
        else MessageAlert.showErrorMessage(null,"Choose at least one friend to talk to!");
        checkBoxArrayList.clear();
        textFieldSearch.clear();
        //loadCheckbox(service.getPageWithUsers());

    }

    public void handleReturn() {
        mainMenuStage.show();
        inboxStage.close();
    }

    public void handleClose() {
        inboxStage.close();
    }

    public void handleNext(){
        try {
            loadCheckbox(service.getNextPageWithUsers(indexPage));
            indexPage+=10;
        } catch (Exception ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }

    public void handlePrev(){
        try {
            loadCheckbox(service.getPrevPageWithUsers(indexPage));
            indexPage-=10;
        } catch (Exception ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }
}

