package socialnetwork;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import socialnetwork.config.ApplicationContext;
import socialnetwork.controller.ControllerLogin;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.database.*;
import socialnetwork.service.Service;

public class MainFX extends Application {
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loaderLogin=new FXMLLoader();
        loaderLogin.setLocation(getClass().getResource("/view/login.fxml"));
        Parent root = loaderLogin.load();

        String username= ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
        String password=ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
        String url=ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");

        UserDB userDBRepository = new UserDB(url, username, password, new UserValidator());
        FriendshipDB friendshipDBRepository= new FriendshipDB(url, username, password, new FriendshipValidator());
        MessagesDB messageDBRepository=new MessagesDB(url, username, password, new MessageValidator());
        FriendshipRequestDB friendshipRequestDBRepository=new FriendshipRequestDB(url, username, password, new FriendshipRequestValidator());
        EventsDB eventsDBRepository= new EventsDB(url,username, password, new EventValidator());
        EventsUsersDB eventsUsersDBRepository = new EventsUsersDB(url,username,password);

        Service srv= new Service(userDBRepository, friendshipDBRepository, messageDBRepository, friendshipRequestDBRepository, eventsDBRepository, eventsUsersDBRepository);
        ControllerLogin ctrLogin= loaderLogin.getController();
        ctrLogin.setService(srv,primaryStage);

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
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            }
        });

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        Scene scene= new Scene(root,500,350);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}