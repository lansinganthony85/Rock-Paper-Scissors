module com.example.project_2_lansing {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.project_2_lansing to javafx.fxml;
    exports com.example.project_2_lansing;
}