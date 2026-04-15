package com.fawkes.front.components;

import com.fawkes.front.models.Supplier;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;

public class SupplierCard extends AnchorPane {

    // fx:id disponíveis no SupplierCard.fxml:
    //   fx:id="status" → label "● Ativo"
    // Sem fx:id: .suppiler__name (typo no FXML), .supplier__signed
    @FXML private Label status;

    public SupplierCard() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/fawkes/front/view/components/SupplierCard.fxml"));
        //String css = Objects.requireNonNull(
                        //getClass().getResource("/com/fawkes/front/styles/components/supplier.scss"))
                //.toExternalForm();
        //this.getStylesheets().add(css);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setData(Supplier supplier) {
        // fx:id disponível: status (ativo/inativo — fornecedores não têm isActive no back ainda,
        // então exibimos o meio de pagamento aqui como identificador rápido)
        if (status != null) {
            status.setText("● " + supplier.getPaymentMethod());
            status.setStyle("-fx-text-fill: #1565C0;");
        }

        // Label do nome (styleClass "suppiler__name" — typo original do FXML mantido)
        Label nameLabel = (Label) this.lookup(".suppiler__name");
        if (nameLabel != null) {
            nameLabel.setText(supplier.getName().toUpperCase());
        }

        // Label de rodapé — exibe o CNPJ
        Label signedLabel = (Label) this.lookup(".supplier__signed");
        if (signedLabel != null) {
            signedLabel.setText("CNPJ: " + supplier.getCnpj());
        }
    }
}