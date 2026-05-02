package com.fawkes.front.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddSolicitorForm {
    @FXML private Button btnClose;
    @FXML private Button btnSubmit;
    @FXML private Label solicitorName;
    @FXML private Label districtName;
    @FXML private Label paymentMethod;
    @FXML private Label costCenter;
    @FXML private Label description;
    @FXML private Label productList;
    @FXML private Label productName;
    @FXML private Label supplierList;
    @FXML private Label supplierName;
    @FXML private Label finalTotal;
    @FXML private Label finalQuantity;

    // FORM INPUTS
    @FXML private TextField districtField;
    @FXML private TextField descriptionField;
    @FXML private ComboBox<String> paymentField;
    @FXML private ComboBox<String> costField;
}
