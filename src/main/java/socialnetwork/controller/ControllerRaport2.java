package socialnetwork.controller;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.service.Service;
import socialnetwork.utils.Constants;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class ControllerRaport2 {

    private Service service;
    private Stage raport2Stage;
    private Stage adminStage;
    private User user, sender;
    private ArrayList<Message> messages;

    private LocalDate startDate, endDate;

    @FXML
    VBox messagesBox;

    @FXML
    LineChart messagesChart;

    @FXML
    Label labelChart;


    public void setService(Service service, Stage raport2Stage, Stage adminStage, ArrayList<Message> messages, User user, User sender, Map<LocalDate,Integer> mapMessages, LocalDate startDate, LocalDate endDate) {
        this.service = service;
        this.raport2Stage = raport2Stage;
        this.adminStage = adminStage;
        this.messages=messages;
        this.user=user;
        this.sender=sender;
        this.startDate=startDate;
        this.endDate=endDate;

        loadLabels(messages, mapMessages);

    }

    private void loadLabels(ArrayList<Message> messages, Map<LocalDate,Integer> mapMessages) {

        messagesBox.getChildren().clear();
        messagesBox.setSpacing(10);
        messagesBox.setPadding(new Insets(10));

        for (Message m : messages) {
            User from = service.getUser(m.getFrom());
            Label label = new Label("from: " + from.getFullname() + "\n" + "date: " + m.getDate().format(Constants.DATE_TIME_FORMATTER) + "\n" + "message: " + m.getMessage()+"\n");
            label.setFont(Font.font("System", FontWeight.BOLD, 12.0));
            label.setTextFill(Paint.valueOf("#ffebcd"));
            messagesBox.getChildren().add(label);
        }

        XYChart.Series series = new XYChart.Series();
        series.setName("Messages received");
        for(Map.Entry<LocalDate,Integer> mapEntry : mapMessages.entrySet()) {
            series.getData().add(new XYChart.Data(mapEntry.getKey().toString(), mapEntry.getValue()));
        }

        messagesChart.getData().add(series);
        labelChart.setText("Messages received by " + user.getFullname() + "\nand sent by " +  sender.getFullname() + " in between\n" + startDate + " and " + endDate);
    }

    @FXML
    public void handleReturn() {
        adminStage.show();
        raport2Stage.close();
    }

    @FXML
    public void handleClose() {
        raport2Stage.close();
    }

    public void handleSave() {

        Document document = new Document();
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("Raport2.pdf"));
            document.open();

            document.add(new Paragraph("Messages received by: "+user.getFullname()+" and sent by: "+sender.getFullname()));
            document.add(new Paragraph("Time interval: " + startDate + " ... " + endDate));
            document.add(new Paragraph("Document generated at: " + LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER)));
            document.add(new Paragraph("\n\n\n\n"));
            for(Message m : messages){
                String text = "From: " + service.getUser(m.getFrom()).getFullname() + "\n" + "Date: "+m.getDate().format(Constants.DATE_TIME_FORMATTER) + "\n" + "Message: " + m.getMessage()+"\n\n\n";
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

