package com.fawkes.front.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fawkes.front.service.ApiClient;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

public class AddStockItemForm {
    @FXML private JFXButton btnClose;
    @FXML private Label errorLabel;

    // FORM INPUTS
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField qtdField;
    @FXML private TextField minValField;
    @FXML private ImageView productView;
    @FXML private ComboBox<String> suppilerField;

    private final Map<String, Long> supplierNameToId = new LinkedHashMap<>();

    @FXML
    public void initialize() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();

            if(text.matches("\\d*")) {
                return change;
            }

            return null;
        };

        priceField.setTextFormatter(new TextFormatter<>(filter));
        qtdField.setTextFormatter(new TextFormatter<>(filter));
        minValField.setTextFormatter(new TextFormatter<>(filter));

        loadSuppliers();
    }

    private void loadSuppliers() {
        try {
            JsonNode data = ApiClient.get("/api/suppliers");
            for (JsonNode node : data) {
                String name = node.path("nomeFornecedor").asText("Sem nome");
                Long id = node.path("id").asLong();
                supplierNameToId.put(name, id);
                suppilerField.getItems().add(name);
            }
        } catch (Exception e) {
            errorLabel.setText("Erro ao carregar fornecedores: " + e.getMessage());
        }
    }


    @FXML
    private void closeModal(ActionEvent event) {
        ((Stage) btnClose.getScene().getWindow()).close();
    }

    @FXML
    private void handleOpenExplorer(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar a Imagem do Funcionário");

        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Arquivos de Imagem", "*.png", "*.jpg", "*.jpeg"));

        Window stage = ((Node) event.getSource()).getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            productView.setImage(image);
        }
    }

    @FXML
    private void handleOnSubmit(ActionEvent event) {
        if (nameField.getText().isEmpty() || priceField.getText().isEmpty() || qtdField.getText().isEmpty() || minValField.getText().isEmpty() || suppilerField.getSelectionModel().getSelectedItem() == null) {
            errorLabel.setText("Verfique se todos os campos obrigatórios foram preenchidos.");
        } else {
            System.out.println("Fazer a lógica de upload aqui");
        }

    }
}
