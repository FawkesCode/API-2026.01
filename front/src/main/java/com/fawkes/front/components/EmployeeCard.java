package com.fawkes.front.components;

import com.fawkes.front.models.Employee;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Objects;

public class EmployeeCard extends AnchorPane {
    @FXML private Label status;
    @FXML private Label department;
    @FXML private ImageView picture;
    @FXML private Label position;
    @FXML private Label name;
    @FXML private Label email;
    @FXML private Label signed;

    public EmployeeCard() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/components/EmployeeCard.fxml"));

        String css = Objects.requireNonNull(getClass().getResource("/com/fawkes/front/styles/components/employees.css")).toExternalForm();
        this.getStylesheets().add(css);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setData(Employee employee) {
        this.name.setText(employee.getName());
        this.email.setText(employee.getEmail());
        this.picture.setImage(employee.getPicture());
        this.status.setText(employee.getStatus());
        this.position.setText(employee.getPosition());
        this.department.setText(employee.getDepartment());
        this.signed.setText(employee.getSigned());
    }


}
