package com.fawkes.front.components;

import com.fawkes.front.models.Employee;
import com.fawkes.front.models.Supplier;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

public class SupplierCard extends AnchorPane {

    // fx:id disponíveis no SupplierCard.fxml:
    //   fx:id="status" → label "● Ativo"
    // Sem fx:id: .suppiler__name (typo no FXML), .supplier__signed
    @FXML private Label status;

    private Supplier supplier;
    private Consumer<Supplier> onEditAction;

    public void setOnEditAction(Consumer<Supplier> action) {
        this.onEditAction = action;
    }

    public SupplierCard() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/fawkes/front/view/components/SupplierCard.fxml"));
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
            status.setText("● " + supplier.getActive());
            System.out.println("PAGAMENTO" + supplier.getActive());
        }

        // Label do nome (styleClass "suppiler__name" — typo original do FXML mantido)
        Label nameLabel = (Label) this.lookup(".suppiler__name");
        if (nameLabel != null) {
            nameLabel.setText(supplier.getName());
        }

        // Label de rodapé — exibe o CNPJ
        Label signedLabel = (Label) this.lookup(".supplier__signed");
        if (signedLabel != null) {
            signedLabel.setText("CNPJ: " + supplier.getCnpj());
        }
        this.supplier = supplier;
    }

    @FXML
    public void openEditModal(){
        if (onEditAction != null) {
            onEditAction.accept(this.supplier);
        }
    }
}