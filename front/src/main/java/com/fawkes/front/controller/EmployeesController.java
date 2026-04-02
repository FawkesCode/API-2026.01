package com.fawkes.front.controller;

import com.fawkes.front.components.EmployeeCard;
import com.fawkes.front.models.Employee;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.Objects;

public class EmployeesController {
    @FXML private HBox employeesContainer;

    @FXML
    public void initialize() {

        Image picture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/fawkes/front/img/menu-icon.png")));
        Employee data = new Employee("Ativo", "DEPARTAMENTO", picture, "CARGO", "FULANO", "fulano@gmail.com", "Cadastrado em raiva");
        EmployeeCard card = new EmployeeCard();
        card.setData(data);
        employeesContainer.getChildren().add(card);
    }


}
