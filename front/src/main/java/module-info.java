module com.fawkes.front {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.fawkes.front to javafx.fxml;
    exports com.fawkes.front;
    exports com.fawkes.front.controller;
    exports com.fawkes.front.models;
    opens com.fawkes.front.controller to javafx.fxml;
    opens com.fawkes.front.utils to javafx.graphics, javafx.controls;
    opens com.fawkes.front.view to javafx.fxml;
    opens com.fawkes.front.components to javafx.fxml;

}