package socialnetwork.controller;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import socialnetwork.domain.FriendDataDTO;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.service.Service;
import socialnetwork.utils.Constants;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ControllerRaport1 {

    private Service service;
    private Stage raport1Stage;
    private Stage adminStage;
    private User user;
    private ArrayList<Message> messages;
    private ArrayList<FriendDataDTO> friends;
    private LocalDate startDate, endDate;


    @FXML
    VBox messagesBox;

    @FXML
    VBox friendsBox;

    @FXML
    PieChart messagesChart;

    @FXML
    Label messagesLabel;

    @FXML
    Label friendsLabel;


    public void setService(Service service, Stage raport1Stage, Stage adminStage, ArrayList<Message> messages, ArrayList<FriendDataDTO> friends, Map<String,Integer> mapMessages, User user, LocalDate startDate, LocalDate endDate) {
        this.service = service;
        this.raport1Stage = raport1Stage;
        this.adminStage = adminStage;
        this.messages=messages;
        this.friends=friends;
        this.user=user;
        this.startDate=startDate;
        this.endDate=endDate;

        loadLabels(messages, friends, mapMessages);

    }

    private void loadLabels(ArrayList<Message> messages, ArrayList<FriendDataDTO> friends, Map<String,Integer> mapMessages) {

        messagesBox.getChildren().clear();
        messagesBox.setSpacing(10);
        messagesBox.setPadding(new Insets(10));
        friendsBox.setSpacing(10);
        friendsBox.getChildren().clear();
        friendsBox.setPadding(new Insets(10));

        for (Message m : messages) {
            User from = service.getUser(m.getFrom());
            Label label = new Label("from: " + from.getFullname() + "\n" + "date: " + m.getDate().format(Constants.DATE_TIME_FORMATTER) + "\n" + "message: " + m.getMessage());
            label.setFont(Font.font("System", FontWeight.BOLD, 12.0));
            label.setTextFill(Paint.valueOf("#ffebcd"));

            messagesBox.getChildren().add(label);
        }

        ObservableList<PieChart.Data> pieChartMessages = FXCollections.observableArrayList();
        for(Map.Entry<String,Integer> mapEntry : mapMessages.entrySet()){
            PieChart.Data pie = new PieChart.Data(mapEntry.getKey(),mapEntry.getValue());
            pieChartMessages.add(pie);
        }

        messagesChart.setData(pieChartMessages);
        messagesLabel.setText("Messages received by " + user.getFullname());

        for (FriendDataDTO f : friends) {
            Label label = new Label("name: " + f.getFirstname() + " " + f.getLastname() + "\n" + "date: " + f.getDate().format(Constants.DATE_TIME_FORMATTER));
            label.setFont(Font.font("System", FontWeight.BOLD, 12.0));
            label.setTextFill(Paint.valueOf("#ffebcd"));
            friendsBox.getChildren().add(label);
        }

        friendsLabel.setText("New friends for " + user.getFullname() + "\nin between " + startDate + " and " + endDate);

    }

    @FXML
    public void handleReturn() {
        adminStage.show();
        raport1Stage.close();
    }

    @FXML
    public void handleClose() {
        raport1Stage.close();
    }

    public void handleSave() {

        Document document = new Document();
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("Raport1.pdf"));
            document.open();

            document.add(new Paragraph("Messages received by: " + user.getFullname()));
            document.add(new Paragraph("Time interval: " + startDate + " ... " + endDate));
            document.add(new Paragraph("Document generated at: " + LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER)));
            document.add(new Paragraph("\n\n\n"));
            for(Message m : messages){
                String text = "From: " + service.getUser(m.getFrom()).getFullname() + "\n" + "Date: "+m.getDate().format(Constants.DATE_TIME_FORMATTER) + "\n" + "Message: " + m.getMessage()+"\n\n\n";
                document.add(new Paragraph(text));
            }

            document.add(new Paragraph("\n\n\n\n"));
            document.add(new Paragraph("Friends for: " + user.getFullname()));
            document.add(new Paragraph("Time interval: " + startDate + " ... " + endDate + "\n\n\n"));
            for(FriendDataDTO f : friends){
                String text = "Name: " + f.getFirstname() + " " + f.getLastname() + "\n" + f.getDate().format(Constants.DATE_TIME_FORMATTER)+"\n\n\n";
                document.add(new Paragraph(text));
            }

            document.close();
            writer.close();
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION,"Succes", "Raport saved as pdf!");
        }
        catch (DocumentException ex) {
            ex.printStackTrace();
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

    }


}

