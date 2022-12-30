package com.example.socialnetwork;

import com.example.socialnetwork.controllers.LoginController;
import com.example.socialnetwork.repositories.FriendshipDbRepo;
import com.example.socialnetwork.repositories.UserDbRepo;
import com.example.socialnetwork.services.UserService;
import com.example.socialnetwork.validators.FriendshipValidator;
import com.example.socialnetwork.validators.UserValidator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SocialNetwork extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            String url = "jdbc:postgresql://localhost:5432/social_network";
            String username = "postgres";
            String password = "postgres";

            UserDbRepo repo = new UserDbRepo(url, username, password);
            FriendshipDbRepo frndRepo = new FriendshipDbRepo(url, username, password);

            UserValidator val = new UserValidator();
            FriendshipValidator fVal = new FriendshipValidator();

            UserService srv = new UserService(repo, frndRepo, val, fVal);

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("views/loginView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(SocialNetwork.class.getResource("CSS/dark.css").toExternalForm());

            primaryStage.setTitle("Social Network App");
            primaryStage.setScene(scene);

            LoginController logInController = fxmlLoader.getController();
            logInController.setSrv(srv);

            primaryStage.show();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
