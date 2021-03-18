package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import socialnetwork.domain.FriendshipRequest;
import socialnetwork.domain.FriendshipRequestDTO;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.Service;
import socialnetwork.utils.Constants;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ControllerSearchMenu {


    private Service service;
    private Stage searchStage, mainMenuStage;
    private User user;
    private int indexPage=0;

    private ArrayList<CheckBox> checkBoxArrayList=new ArrayList<>();

    @FXML
    TextField textFieldName;

    @FXML
    BorderPane borderPane;

    @FXML
    VBox usersBox;

    public void setService(Service service,Stage searchStage, Stage mainMenuStage, User user) {
        this.service = service;
        this.searchStage=searchStage;
        this.mainMenuStage = mainMenuStage;
        this.user = user;

        loadCheckbox(service.getPageWithUsers(indexPage));
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


    @FXML
    public void initialize() {
        textFieldName.textProperty().addListener(x->handleFilterName());
    }

    public void handleClose(){
        searchStage.close();
    }

    private void handleFilterName() {
        ArrayList<User> users = (ArrayList<User>) service.getAllUsers()
            .stream()
            .filter(u->u.getFirstname().startsWith(textFieldName.getText())||u.getLastname().startsWith(textFieldName.getText()))
            .collect(Collectors.toList());

        loadCheckbox(users);
    }

    public void handleSendRequest() {

        if(checkBoxArrayList.size()!=0) {
            try {
                for(CheckBox checkBox : checkBoxArrayList){
                    String[] args = checkBox.getText().split(" ");
                    User friend = service.getUserByName(args[0], args[1]);
                    FriendshipRequest result = service.sendFriendshipRequest(user, friend);
                    if (result == null) {
                        MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Succes", "Friendship request sent!");
                    } else if (result.getStatus().equals("approved"))
                        MessageAlert.showErrorMessage(null, friend.getFirstname() + " " + friend.getLastname() + " is already your friend!");
                    else if (result.getStatus().equals("pending")) {
                        if (result.getFrom().equals(service.getUserByName(user.getFirstname(), user.getLastname()).getId()))
                            MessageAlert.showErrorMessage(null, "The friendship request has already been sent and it is waiting for a response!");
                        else MessageAlert.showErrorMessage(null, "This friendship request is waiting for your response!");
                    } else if (result.getStatus().equals("rejected")) {
                        if (result.getFrom().equals(service.getUserByName(user.getFirstname(), user.getLastname()).getId()))
                            MessageAlert.showErrorMessage(null, friend.getFirstname() + " " + friend.getLastname() + " does not want to be your friend! :(");
                        else MessageAlert.showErrorMessage(null, "You already refused to befriend this user!");
                    }
                }

            }
            catch (ValidationException ex){
                MessageAlert.showErrorMessage(null,ex.getMessage());
            }
        }
        else MessageAlert.showErrorMessage(null,"Select the friend you want to remove!");
        textFieldName.clear();
        checkBoxArrayList.clear();
        loadCheckbox(service.getPageWithUsers(indexPage));
    }

    public void handleReturn() {
        searchStage.close();
        mainMenuStage.show();
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
