package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import socialnetwork.domain.Password;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.Service;

public class ControllerSignUp {

        private Service service;
        Stage signUpStage;
        Stage loginStage;

        @FXML
        private TextField usernameField;

        @FXML
        private TextField passwordField;

        @FXML
        private TextField firstnameField;

        @FXML
        private TextField lastnameField;



        public void setService(Service service, Stage signUpStage, Stage loginStage){
            this.service=service;
            this.signUpStage=signUpStage;
            this.loginStage=loginStage;
        }


        @FXML
        public void handleClose(){
            signUpStage.close();
        }

        @FXML
        public void handleSignUp(){

            String username =usernameField.getText();
            String password= passwordField.getText();
            String firstname= firstnameField.getText();
            String lastname= lastnameField.getText();

            usernameField.clear();
            passwordField.clear();
            firstnameField.clear();
            lastnameField.clear();

            try{
                User result = service.addUser(new User(firstname,lastname,username,Password.getSaltedHash(password),null));
                if(result!=null)
                    MessageAlert.showErrorMessage(null,"User already exists");
                else {
                    MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION,"Succes","Welcome!");
                    signUpStage.close();
                    loginStage.show();
                }
            }
            catch(ValidationException ex) {
                MessageAlert.showErrorMessage(null, ex.getMessage());
            }
            catch (NullPointerException ex){
                MessageAlert.showErrorMessage(null, ex.getMessage());
            }
            catch (Exception ex) {
                MessageAlert.showErrorMessage(null,ex.getMessage());
            }
        }

        @FXML
        public void handleCancel(){
            loginStage.show();
            signUpStage.close();

        }

}
