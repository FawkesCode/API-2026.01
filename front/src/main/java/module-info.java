module com.fawkes.front {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.fawkes.front to javafx.fxml;
    exports com.fawkes.front;
    exports com.fawkes.front.controller;
    opens com.fawkes.front.controller to javafx.fxml;
}