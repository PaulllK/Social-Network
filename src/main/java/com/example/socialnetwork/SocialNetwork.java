package com.example.socialnetwork;

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
        String url = "jdbc:postgresql://localhost:5432/social_network";
        String username = "postgres";
        String password = "postgres";

        UserDbRepo repo = new UserDbRepo(url, username, password);
        FriendshipDbRepo frndRepo = new FriendshipDbRepo(url, username, password);

        UserValidator val = new UserValidator();
        FriendshipValidator fVal = new FriendshipValidator();

        UserService usc = new UserService(repo, frndRepo, val, fVal);

        initView(primaryStage);
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("views/loginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 420, 260);
        scene.getStylesheets().add(SocialNetwork.class.getResource("css/login.css").toExternalForm());
        primaryStage.setTitle("Log In");
        primaryStage.setScene(scene);

        LogInController logInController = fxmlLoader.getController();
        logInController.setNetworkService(networkService);
    }
}
