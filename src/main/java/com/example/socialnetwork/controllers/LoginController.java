package com.example.socialnetwork.controllers;

import com.example.socialnetwork.SocialNetwork;
import com.example.socialnetwork.customExceptions.RepoException;
import com.example.socialnetwork.customExceptions.ValidatorException;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    private UserService srv;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private TextField passwordTextField;

    public void setSrv(UserService srv) {
        this.srv = srv;
    }

    @FXML
    protected void LogUserIn(ActionEvent event) {

        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String password = passwordTextField.getText();

        try {
            User user = srv.findUserByData(firstName, lastName, password);

            firstNameTextField.setText("");
            lastNameTextField.setText("");
            passwordTextField.setText("");

//            Stage loginStage = (Stage) firstNameTextField.getScene().getWindow();
//            loginStage.hide();

            startUserSession(user);
        } catch (RepoException e) {
            PopUpMessage.showErrorMessage(e.getMessage());
        } catch (ValidatorException e) {
            PopUpMessage.showErrorMessage(e.getMessage());
        }
    }

    @FXML
    public void registerUser(ActionEvent actionEvent) {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String password = passwordTextField.getText();

        try {
            srv.registerUser(firstName, lastName, password);

            firstNameTextField.setText("");
            lastNameTextField.setText("");
            passwordTextField.setText("");

            PopUpMessage.showInformationMessage("user account created");
        } catch (RepoException e) {
            PopUpMessage.showErrorMessage(e.getMessage());
        } catch (ValidatorException e) {
            PopUpMessage.showErrorMessage(e.getMessage());
        }
    }

    private void startUserSession(User user/*, Stage loginStage* - use this parameter if log in window will be hidden*/) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(SocialNetwork.class.getResource("views/userView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(SocialNetwork.class.getResource("CSS/dark.css").toExternalForm());

            stage.setTitle(user.getFirstName() + " " + user.getLastName());
            stage.setScene(scene);

            UserController userController = fxmlLoader.getController();
            userController.setData(srv, user);

            stage.show();
        } catch (IOException exception) {
            PopUpMessage.showErrorMessage("Session start error!");
        }
    }
}
