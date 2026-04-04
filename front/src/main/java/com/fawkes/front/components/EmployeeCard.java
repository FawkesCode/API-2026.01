package com.fawkes.front.components;

import com.fawkes.front.models.Employee;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

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
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/fawkes/front/view/components/EmployeeCard.fxml"));

        String css = Objects.requireNonNull(
                        getClass().getResource("/com/fawkes/front/styles/components/employees.css"))
                .toExternalForm();
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
        this.name.setText(employee.getName().toUpperCase());
        this.email.setText(employee.getEmail());

        // Imagem: usa a do model se disponível, senão tenta carregar um placeholder
        if (employee.getPicture() != null) {
            this.picture.setImage(employee.getPicture());
        } else {
            try {
                Image placeholder = new Image(
                        Objects.requireNonNull(
                                getClass().getResourceAsStream(
                                        "/com/fawkes/front/img/placeholder-user.png")));
                this.picture.setImage(placeholder);
            } catch (Exception ignored) {
                // sem placeholder, deixa vazio
            }
        }

        this.status.setText("● " + employee.getStatus());
        // Verde para ativo, vermelho para inativo
        if ("Ativo".equalsIgnoreCase(employee.getStatus())) {
            this.status.setStyle("-fx-text-fill: #2e7d32;");
        } else {
            this.status.setStyle("-fx-text-fill: #FF4A50;");
        }

        this.position.setText(employee.getPosition().toUpperCase());
        this.department.setText("SETOR | " + employee.getDepartment().toUpperCase());
        this.signed.setText("Cadastrado em " + employee.getSigned());
    }
}