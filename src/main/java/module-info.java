module com.example.socialnetwork {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.example.socialnetwork;
    exports com.example.socialnetwork;

    opens com.example.socialnetwork.controllers to javafx.fxml;
    exports com.example.socialnetwork.controllers;

    opens com.example.socialnetwork.domain to javafx.fxml;
    exports com.example.socialnetwork.domain;

    opens com.example.socialnetwork.domain.DTOs to javafx.fxml;
    exports com.example.socialnetwork.domain.DTOs;
}