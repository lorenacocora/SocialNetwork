package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import socialnetwork.domain.FriendDataDTO;
import socialnetwork.domain.FriendshipRequest;
import socialnetwork.domain.FriendshipRequestDTO;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.Service;
import socialnetwork.utils.Constants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ControllerRequestsMenu{

    private Service service;
    private Stage requestsStage, mainMenuStage;
    private User user;
    private int indexPage=0;

    ArrayList<CheckBox> checkBoxArrayList=new ArrayList<>();

    @FXML
    Button buttonAccept;

    @FXML
    Button buttonReject;

    @FXML
    TextField textFieldFrom;

    @FXML
    TextField textFieldTo;

    @FXML
    BorderPane borderPane;

    @FXML
    VBox requestsBox;

    public void setService(Service service, Stage requestsStage, Stage mainMenuStage, User user) {
        this.service = service;
        this.requestsStage = requestsStage;
        this.mainMenuStage = mainMenuStage;
        this.user = user;


        loadCheckbox(service.getPageWithRequestsByUsers(user,indexPage));
    }

    private void loadCheckbox(ArrayList<FriendshipRequestDTO> requestsDTOS){

        requestsBox.getChildren().clear();
        requestsBox.setPadding(new Insets(10));

        for(FriendshipRequestDTO fr : requestsDTOS){
            CheckBox checkBox=new CheckBox("from: "+ fr.getFrom()+"\n"+"to: "+fr.getTo()+"\n"+fr.getDate().format(Constants.DATE_TIME_FORMATTER));
            requestsBox.getChildren().add(checkBox);

            checkBox.setFont(Font.font("System", FontWeight.BOLD, 12.0));
            checkBox.setTextFill(Paint.valueOf("#ffebcd"));
            checkBox.setLineSpacing(5);
            requestsBox.setSpacing(10);

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
        textFieldFrom.textProperty().addListener(x -> handleFilterFrom());
        textFieldTo.textProperty().addListener(x -> handleFilterTo());
    }

    public void handleClose(){
        requestsStage.close();
    }

    private void handleFilterFrom() {

        ArrayList<FriendshipRequestDTO> requestDTOS = (ArrayList<FriendshipRequestDTO>) service.getRequestsByUser(user)
                .stream()
                .filter(fr->fr.getFrom().startsWith(textFieldFrom.getText()))
                .collect(Collectors.toList());

        loadCheckbox(requestDTOS);

    }

    private void handleFilterTo() {
        ArrayList<FriendshipRequestDTO> requestDTOS = (ArrayList<FriendshipRequestDTO>) service.getRequestsByUser(user)
                .stream()
                .filter(fr->fr.getTo().startsWith(textFieldTo.getText()))
                .collect(Collectors.toList());

        loadCheckbox(requestDTOS);
    }

    public void handleAccept() throws Exception {

        if (checkBoxArrayList.size() != 0) {
            try {
                for (CheckBox checkBox: checkBoxArrayList) {
                    String[] args = checkBox.getText().split("\n");
                    String[] u1 = args[0].split(" ");
                    String[] u2 = args[1].split(" ");
                    User from = service.getUserByName(u1[1],u1[2]);
                    User to = service.getUserByName(u2[1],u2[2]);
                    FriendshipRequest request = service.getRequestByUsers(from, to);
                    FriendshipRequest result = service.respondRequest(request.getId(), user, "Yes");
                    if (result == null)
                        MessageAlert.showErrorMessage(null, "You could not response this friendship request!");

                    else MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Success", "Friend added!");
                }

            }
            catch (ValidationException ex) {
                MessageAlert.showErrorMessage(null, ex.getMessage());
            }
            catch (Exception ex){
                MessageAlert.showErrorMessage(null, ex.getMessage());
            }
        }
        else MessageAlert.showErrorMessage(null, "Select the friendship request you want to respond to!");
        textFieldFrom.clear();
        textFieldTo.clear();
        checkBoxArrayList.clear();
        loadCheckbox(service.getPageWithRequestsByUsers(user, indexPage));
    }

    public void handleReject() throws Exception {
        if (checkBoxArrayList.size() != 0) {
            try {
                for (CheckBox checkBox: checkBoxArrayList) {
                    String[] args = checkBox.getText().split("\n");
                    String[] u1 = args[0].split(" ");
                    String[] u2 = args[1].split(" ");
                    User from = service.getUserByName(u1[1],u1[2]);
                    User to = service.getUserByName(u2[1],u2[2]);
                    FriendshipRequest request = service.getRequestByUsers(from, to);
                    FriendshipRequest result = service.respondRequest(request.getId(), user, "Yes");
                    if (result == null)
                        MessageAlert.showErrorMessage(null, "You could not response this friendship request!");

                    else
                        MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Success", "Friendship request rejected!");
                }

            }
            catch (ValidationException ex) {
                MessageAlert.showErrorMessage(null, ex.getMessage());
            }
            catch (Exception ex){
                MessageAlert.showErrorMessage(null, ex.getMessage());
            }
        }
        else MessageAlert.showErrorMessage(null, "Select the friendship request you want to respond to!");
        textFieldFrom.clear();
        textFieldTo.clear();
        checkBoxArrayList.clear();
        loadCheckbox(service.getPageWithRequestsByUsers(user, indexPage));
    }

    public void handleReturn() {
        requestsStage.close();
        mainMenuStage.show();
    }

    public void handleCancel() {

        if (checkBoxArrayList.size() != 0) {
            try {
                for (CheckBox checkBox: checkBoxArrayList) {
                    String[] args = checkBox.getText().split("\n");
                    String[] u1 = args[0].split(" ");
                    String[] u2 = args[1].split(" ");
                    User from = service.getUserByName(u1[1],u1[2]);
                    User to = service.getUserByName(u2[1],u2[2]);
                    FriendshipRequest request = service.getRequestByUsers(from, to);
                    FriendshipRequest result = service.cancelRequest(request.getId(),user);
                if (result == null)
                    MessageAlert.showErrorMessage(null, "You could not cancel this friendship request!");
                else MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Success", "Friendship request canceled!");

                }
            }
            catch (ValidationException ex) {
                MessageAlert.showErrorMessage(null, ex.getMessage());
            }
            catch (Exception ex){
                MessageAlert.showErrorMessage(null,ex.getMessage());
            }

        }
        else MessageAlert.showErrorMessage(null, "Select the friendship request you want to cancel!");
        textFieldFrom.clear();
        textFieldTo.clear();
        checkBoxArrayList.clear();
        loadCheckbox(service.getPageWithRequestsByUsers(user, indexPage));
    }

    public void handleNext() {
        try {
            loadCheckbox(service.getNextPageWithRequestsByUsers(user, indexPage));
            indexPage+=10;
        } catch (Exception ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }

    public void handlePrev() {
        try {
            loadCheckbox(service.getPrevPageWithRequestsByUsers(user, indexPage));
            indexPage-=10;
        } catch (Exception ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }
}
