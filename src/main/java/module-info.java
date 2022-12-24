module com.example.socialnetwork {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.socialnetwork to javafx.fxml;
    exports com.example.socialnetwork;
}