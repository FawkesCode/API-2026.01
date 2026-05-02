package com.fawkes.front.controller;

import com.fawkes.front.components.OrdersCard;
import com.fawkes.front.utils.ModalManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class DashboardController {
    @FXML private VBox container;
    @FXML private Button btnOpen;
    @FXML
    public void initialize(){
        OrdersCard card=new OrdersCard();
        System.out.println("DashboardController.initialize() chamado");
        container.getChildren().add(card);
    }
    @FXML
    private void openModal() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/forms/new-solicitor-form.fxml"));
        Parent formulario = loader.load();
        AddSolicitorForm controller = loader.getController();
        Stage curStage = ((Stage) btnOpen.getScene().getWindow());
        ModalManager.openModal(curStage, formulario, "Solicitar Pedido");
    }
    @FXML
    private void openModal2() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/forms/director-request-form.fxml"));
        Parent formulario = loader.load();
        DirectorRequestForm controller = loader.getController();
        Stage curStage = ((Stage) btnOpen.getScene().getWindow());
        ModalManager.openModal(curStage, formulario, "Confirmar/Recusar Pedido");
    }
    @FXML
    private void openModal4() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/forms/pending-request-form.fxml"));
        Parent formulario = loader.load();
        PendingRequestForm controller = loader.getController();
        Stage curStage = ((Stage) btnOpen.getScene().getWindow());
        ModalManager.openModal(curStage, formulario, "Pedido Pendente");
    }
    @FXML
    private void openModal5() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fawkes/front/view/forms/shopping-request-form.fxml"));
        Parent formulario = loader.load();
        RequestCheckForm controller = loader.getController();
        Stage curStage = ((Stage) btnOpen.getScene().getWindow());
        ModalManager.openModal(curStage, formulario, "Itens a serem pedidos");
    }
}
