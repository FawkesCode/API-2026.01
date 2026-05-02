package com.fawkes.front.components;

import com.fawkes.front.models.Employee;
import com.fawkes.front.models.Supplier;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

public class SupplierCard extends AnchorPane {

    @FXML private Label status;
    @FXML private Button btnToggleStatus;
    @FXML private ImageView toggleStatusIcon;

    private Supplier supplier;
    private Consumer<Supplier> onEditAction;
    private Consumer<Supplier> onToggleStatusAction;

    public void setOnEditAction(Consumer<Supplier> action) {
        this.onEditAction = action;
    }

    public void setOnToggleStatusAction(Consumer<Supplier> action) {
        this.onToggleStatusAction = action;
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
        if (status != null) {
            status.setText("● " + supplier.getActive());
            System.out.println("PAGAMENTO" + supplier.getActive());
            if (supplier.getActive().equals("Ativo")) {
                status.getStyleClass().remove("status-label--inactive");
            } else {
                status.getStyleClass().add("status-label--inactive");
            }
        }

        if (supplier.getActive().equals("Inativo")) {
            getStyleClass().add("supplier--inactive");
        } else {
            getStyleClass().remove("supplier--inactive");
        }

        Label nameLabel = (Label) this.lookup(".suppiler__name");
        if (nameLabel != null) {
            nameLabel.setText(supplier.getName());
        }

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

    @FXML
    public void handleToggleStatus() {
        if (onToggleStatusAction != null) {
            onToggleStatusAction.accept(this.supplier);
        }
    }
}