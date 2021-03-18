package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ControllerEvents {

    private Service service;
    private Stage eventsStage, mainMenuStage;
    private User user;
    private int indexPage=0;

    private ArrayList<CheckBox> checkBoxArrayList = new ArrayList<>();

    @FXML
    TextField textFieldName;

    @FXML
    TextField textFieldLocation;

    @FXML
    DatePicker datePicker;

    @FXML
    BorderPane borderPane;

    @FXML
    VBox eventsBox;

    public void setService(Service service, Stage eventsStage, Stage mainMenuStage, User user) {
        this.service = service;
        this.eventsStage = eventsStage;
        this.mainMenuStage = mainMenuStage;
        this.user = user;

        loadCheckbox(service.getPageWithEvents(indexPage));
    }

    private void loadCheckbox(ArrayList<Event> events) {

        eventsBox.getChildren().clear();
        eventsBox.setPadding(new Insets(10));
        ArrayList<Event> eventsOfTheUser = service.getEventsByUser(user);

        for (Event e : events) {
            CheckBox checkBox = new CheckBox(e.getName()+"\n"+e.getLocation()+"\n"+e.getDate());
            eventsBox.getChildren().add(checkBox);

            checkBox.setFont(Font.font("System", FontWeight.BOLD, 12.0));
            if(eventsOfTheUser.contains(e)){
                if(service.isEventNotifiable(e,user))
                    checkBox.setTextFill(Paint.valueOf("#97c1a9"));
                else checkBox.setTextFill(Paint.valueOf("#cbaacb"));
            }
            else checkBox.setTextFill(Paint.valueOf("ff968a"));
            checkBox.setLineSpacing(5);
            eventsBox.setSpacing(10);

            EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    if (checkBox.isSelected()) {
                        checkBoxArrayList.add(checkBox);
                    } else {
                        checkBoxArrayList.remove(checkBox);
                    }
                }

            };
            checkBox.setOnAction(event);
        }
    }

    @FXML
    public void initialize() {
        //textFieldName.textProperty().addListener(x -> handleFilterName());
    }

    public void handleClose() {
        eventsStage.close();
    }

//    private void handleFilterName() {
//        ArrayList<Event> events = (ArrayList<Event>) service.getAllEvents()
//                .stream()
//                .filter(e->e.getName().startsWith(textFieldName.getText()))
//                .collect(Collectors.toList());
//
//        loadCheckbox(events);
//    }

    public void handleReturn() {
        mainMenuStage.show();
        eventsStage.close();
    }

    public void handleNext() {
        try {
            loadCheckbox(service.getNextPageWithEvents(indexPage));
            indexPage+=10;
        } catch (Exception ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }

    public void handlePrev() {
        try {
            loadCheckbox(service.getPrevPageWithEvents(indexPage));
            indexPage-=10;
        } catch (Exception ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }

    public void handleCreateEvent() {

        String name = textFieldName.getText();
        String location = textFieldLocation.getText();
        LocalDate date = datePicker.getValue();

        try{
            Event result = service.addEvent(name,location,date);
            if(result==null) {
                MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Succes", "Event added");
                loadCheckbox(service.getPageWithEvents(indexPage));
            }
            else MessageAlert.showErrorMessage(null,"The event already exists!");
        }
        catch (ValidationException ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }

        textFieldName.clear();
        textFieldLocation.clear();
    }

    public void handleMute() {
        if (checkBoxArrayList.size() != 0) {
            try {
                for (CheckBox checkBox : checkBoxArrayList) {
                    String[] args = checkBox.getText().split("\n");
                    Event event = service.getEventByNameLocationDate(args[0], args[1], LocalDate.parse(args[2]));
                    Event_User result = service.muteEvent(event, user);
                    if (result != null)
                        MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Succes", "You will not receive notifications from "+args[0]+" anymore!");
                    else MessageAlert.showErrorMessage(null, "You cannot mute an event you don't go to!");
                }

            } catch (ValidationException ex) {
                MessageAlert.showErrorMessage(null, ex.getMessage());
            }
        } else MessageAlert.showErrorMessage(null, "Select the event you want to mute!");

        loadCheckbox(service.getPageWithEvents(indexPage));
        checkBoxArrayList.forEach(c->c.setSelected(false));
        checkBoxArrayList.clear();
    }

    public void handleNotGoing() {
        if (checkBoxArrayList.size() != 0) {
            try {
                for (CheckBox checkBox : checkBoxArrayList) {
                    String[] args = checkBox.getText().split("\n");
                    Event event = service.getEventByNameLocationDate(args[0], args[1], LocalDate.parse(args[2]));
                    Event_User result = service.notGoingToEvent(event, user);
                    if (result != null)
                        MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Succes", "The "+args[0]+" will not be the same without you :( !");

                    else MessageAlert.showErrorMessage(null, "You are already not going to this event!");
                }

            } catch (ValidationException ex) {
                MessageAlert.showErrorMessage(null, ex.getMessage());
            }
        } else MessageAlert.showErrorMessage(null, "Select the event you want to not to go to anymore!");

        loadCheckbox(service.getPageWithEvents(indexPage));
        checkBoxArrayList.forEach(c->c.setSelected(false));
        checkBoxArrayList.clear();

    }

    public void handleGoing() {

        if (checkBoxArrayList.size() != 0) {
            try {
                for (CheckBox checkBox : checkBoxArrayList) {
                    String[] args = checkBox.getText().split("\n");
                    Event event = service.getEventByNameLocationDate(args[0], args[1], LocalDate.parse(args[2]));
                    Event_User result = service.goingToEvent(event, user);
                    if (result == null)
                        MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Succes", "Cannot wait to see you at " + args[0] + "!!!");

                    else MessageAlert.showErrorMessage(null, "You are already going to this event!");
                }

            } catch (ValidationException ex) {
                MessageAlert.showErrorMessage(null, ex.getMessage());
            }
        } else MessageAlert.showErrorMessage(null, "Select the event you want to go to!");

        loadCheckbox(service.getPageWithEvents(indexPage));
        checkBoxArrayList.forEach(c->c.setSelected(false));
        checkBoxArrayList.clear();
    }

    public void handleUnmute() {

        if (checkBoxArrayList.size() != 0) {
            try {
                for (CheckBox checkBox : checkBoxArrayList) {
                    String[] args = checkBox.getText().split("\n");
                    Event event = service.getEventByNameLocationDate(args[0], args[1], LocalDate.parse(args[2]));
                    Event_User result = service.unmuteEvent(event, user);
                    if (result != null)
                        MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Succes", "You will start receiving notifications from "+args[0]+"!");
                    else MessageAlert.showErrorMessage(null, "You cannot unmute an event you don't go to!");
                }

            } catch (ValidationException ex) {
                MessageAlert.showErrorMessage(null, ex.getMessage());
            }
        } else MessageAlert.showErrorMessage(null, "Select the event you want to unmute!");

        loadCheckbox(service.getPageWithEvents(indexPage));
        checkBoxArrayList.forEach(c->c.setSelected(false));
        checkBoxArrayList.clear();
    }
}
