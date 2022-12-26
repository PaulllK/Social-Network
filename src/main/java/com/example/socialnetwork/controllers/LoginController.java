package com.example.socialnetwork.controllers;

import com.example.socialnetwork.customExceptions.RepoException;
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
            PopupMessage.showErrorMessage(e.getMessage());
        }
    }

    private void startUserSession(User user/*, Stage loginStage* - use this parameter if log in window will be hidden*/) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(SocialNetwork.class.getResource("userView.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 600);

            scene.getStylesheets().add(SocialNetwork.class.getResource("css/style.css").toExternalForm());

            stage.setTitle("Social Network");
            stage.setScene(scene);

            UserController userController = fxmlLoader.getController();
            userController.setData(networkService, user, loginStage);

            stage.show();
        } catch (IOException exception) {
            PopupMessage.showErrorMessage("Session start error!");
        }
    }
}
