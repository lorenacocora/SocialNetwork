package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import socialnetwork.domain.FriendDataDTO;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.FriendshipRequestDTO;
import socialnetwork.domain.User;
import socialnetwork.service.Service;
import socialnetwork.utils.Constants;
import socialnetwork.utils.MyObserver;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.text.Style;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;


public class ControllerMainMenu implements MyObserver {

    private Service service;
    private Stage mainMenuStage, loginStage;
    private User user;
    private ArrayList<CheckBox> checkBoxArrayList=new ArrayList<>();
    private double xOffset = 0;
    private double yOffset = 0;
    private int indexPage=0;

    @FXML
    TextField textFieldName;

    @FXML
    ImageView imageView;

    @FXML
    BorderPane borderPane;

    @FXML
    Label labelUsername;

    @FXML
    Label labelName;

    @FXML
    ScrollPane scrollPane;

    @FXML
    VBox friendsBox;

    public void setService(Service service, Stage mainMenuStage, Stage loginStage, User user) {
        this.service = service;
        this.mainMenuStage = mainMenuStage;
        this.loginStage = loginStage;
        this.user = user;

        labelUsername.setText(user.getUsername());
        labelName.setText(user.getFullname());
        loadCheckbox(service.getPageWithFriendshipsByUser(user,indexPage));

        if(user.getPhotoPath()!=null) {
            File file = new File(user.getPhotoPath());
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
        }
    }


    private void loadCheckbox(ArrayList<FriendDataDTO> friends){

        friendsBox.getChildren().clear();
        friendsBox.setPadding(new Insets(10));

        for(FriendDataDTO f : friends){
            CheckBox checkBox=new CheckBox(f.getFirstname() + " " + f.getLastname()+" \n"+f.getDate().format(Constants.DATE_TIME_FORMATTER));
            friendsBox.getChildren().add(checkBox);

            checkBox.setFont(Font.font("System", FontWeight.BOLD, 12.0));
            checkBox.setTextFill(Paint.valueOf("#ffebcd"));
            checkBox.setLineSpacing(5);
            friendsBox.setSpacing(10);

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
        mainMenuStage.close();
    }

    private void handleFilterName() {
        friendsBox.getChildren().clear();

        service.getFriendshipsByUser(user)
                .stream()
                .filter(fr->fr.getFirstname().startsWith(textFieldName.getText())||fr.getLastname().startsWith(textFieldName.getText()))
                .forEach(f->{
                    CheckBox checkBox=new CheckBox(f.getFirstname()+" "+f.getLastname()+" "+f.getDate().format(Constants.DATE_TIME_FORMATTER));
                    checkBox.setFont(Font.font("System", FontWeight.BOLD, 12.0));
                    checkBox.setTextFill(Paint.valueOf("#ffebcd"));
                    friendsBox.getChildren().add(checkBox);
                    friendsBox.setSpacing(5);

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
                });
    }

    public void handleSearch() throws Exception{
        FXMLLoader loaderSearch=new FXMLLoader();
        loaderSearch.setLocation(getClass().getResource("/view/searchMenu.fxml"));
        Parent root = loaderSearch.load();

        Stage searchStage= new Stage();
        searchStage.initStyle(StageStyle.TRANSPARENT);
        ControllerSearchMenu ctrSearch=loaderSearch.getController();
        ctrSearch.setService(service, searchStage, mainMenuStage, user);

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
                searchStage.setX(event.getScreenX() - xOffset);
                searchStage.setY(event.getScreenY() - yOffset);
            }
        });

        Scene scene = new Scene(root, 700, 500);
        scene.setFill(Color.TRANSPARENT);
        searchStage.setScene(scene);
        searchStage.show();
        mainMenuStage.close();

    }

    public void handleRequests() throws Exception{
        FXMLLoader loaderRequests=new FXMLLoader();
        loaderRequests.setLocation(getClass().getResource("/view/requestsMenu.fxml"));
        Parent root = loaderRequests.load();

        Stage requestsStage= new Stage();
        requestsStage.initStyle(StageStyle.TRANSPARENT);
        ControllerRequestsMenu ctrRequests=loaderRequests.getController();
        ctrRequests.setService(service, requestsStage, mainMenuStage, user);

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
                requestsStage.setX(event.getScreenX() - xOffset);
                requestsStage.setY(event.getScreenY() - yOffset);
            }
        });

        Scene scene =new Scene(root, 700, 500);
        scene.setFill(Color.TRANSPARENT);
        requestsStage.setScene(scene);
        requestsStage.show();
        mainMenuStage.close();

    }

    public void handleDeleteFriend() {

        if(checkBoxArrayList.size()!=0){
            for(CheckBox checkBox: checkBoxArrayList){
                String[] args= checkBox.getText().split(" ");
                Friendship result = service.unfriend(user,service.getUserByName(args[0],args[1]));

                if (result==null)
                    MessageAlert.showErrorMessage(null,"Could not remove "+args[0]+" "+args[1]+"!");
            }
        }
        else{
            MessageAlert.showErrorMessage(null,"Select the friend you want to remove!");
        }
        textFieldName.clear();
        loadCheckbox(service.getPageWithFriendshipsByUser(user,indexPage));
        checkBoxArrayList.clear();
    }

    public void handleLogout() {
        loginStage.show();
        mainMenuStage.close();
    }

    public void handleDelete(){
        try{
            User result = service.removeUser(user.getId());
            if(result!=null){
                MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION,"Succes","Account deleted forever!");
                loginStage.show();
                mainMenuStage.close();
            }
            else MessageAlert.showErrorMessage(null,"Could not delete your account!");
        }
        catch (IllegalArgumentException ex){
            MessageAlert.showErrorMessage(null,ex.getMessage());
        }

    }

    public void handleInbox() throws IOException {
        FXMLLoader loaderInbox=new FXMLLoader();
        loaderInbox.setLocation(getClass().getResource("/view/inbox.fxml"));
        Parent root = loaderInbox.load();

        Stage inboxStage= new Stage();
        inboxStage.initStyle(StageStyle.TRANSPARENT);
        ControllerInbox controllerInbox=loaderInbox.getController();
        controllerInbox.setService(service, inboxStage, mainMenuStage, user);

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
                inboxStage.setX(event.getScreenX() - xOffset);
                inboxStage.setY(event.getScreenY() - yOffset);
            }
        });

        Scene scene = new Scene(root, 700, 500);
        scene.setFill(Color.TRANSPARENT);
        inboxStage.setScene(scene);
        inboxStage.show();
        mainMenuStage.close();
    }

    @Override
    public void update() {
        loadCheckbox(service.getPageWithFriendshipsByUser(user,indexPage));
    }

    public void handleNext() {
        try {
            loadCheckbox(service.getNextPageWithFriendshipsByUser(user,indexPage));
            indexPage+=10;
        } catch (Exception ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }

    public void handlePrev() {
        try {
            loadCheckbox(service.getPrevPageWithFriendshipsByUser(user,indexPage));
            indexPage-=10;
        } catch (Exception ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }

    public void handleEvents() throws IOException {
        FXMLLoader loaderEvents=new FXMLLoader();
        loaderEvents.setLocation(getClass().getResource("/view/events.fxml"));
        Parent root = loaderEvents.load();

        Stage eventsStage= new Stage();
        eventsStage.initStyle(StageStyle.TRANSPARENT);
        ControllerEvents ctrEvents=loaderEvents.getController();
        ctrEvents.setService(service, eventsStage, mainMenuStage, user);

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
                eventsStage.setX(event.getScreenX() - xOffset);
                eventsStage.setY(event.getScreenY() - yOffset);
            }
        });

        Scene scene = new Scene(root, 700, 500);
        scene.setFill(Color.TRANSPARENT);
        eventsStage.setScene(scene);
        eventsStage.show();
        mainMenuStage.close();
    }

    public void handleChange() {
        try{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("src/poze/"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image file", "*.png","*.jpg"));
            File file = fileChooser.showOpenDialog(mainMenuStage);
            if(file!=null){
                FileInputStream stream = new FileInputStream(file.getAbsoluteFile());
                imageView.setImage(new Image(stream));
                user.setPhotoPath("src/poze/"+file.getName());
                service.updateUser(user);
            }
        } catch (FileNotFoundException e) {
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
    }
}
