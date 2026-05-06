package com.fawkes.front.components;

import com.fawkes.front.controller.AddEmployeeForm;
import com.fawkes.front.controller.EditEmployeeForm;
import com.fawkes.front.models.Employee;
import com.fawkes.front.utils.ModalManager;
import com.fawkes.front.utils.StringUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

import static com.fawkes.front.MainApplication.GLOBAL_CSS;

public class EmployeeCard extends AnchorPane {

    @FXML private AnchorPane container;
    @FXML private Label status;
    @FXML private Label department;
    @FXML private ImageView picture;
    @FXML private Label position;
    @FXML private Label name;
    @FXML private Label email;
    @FXML private Label signed;
    @FXML private Button editEmployee;
//    @FXML private Button btnToggleStatus;

    private Employee employee;
    private Consumer<Employee> onEditAction;
    private Consumer<Employee> onToggleStatusAction;

    public void setOnEditAction(Consumer<Employee> action) {
        this.onEditAction = action;
    }

    public void setOnToggleStatusAction(Consumer<Employee> action) {
        this.onToggleStatusAction = action;
    }

    public EmployeeCard() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/fawkes/front/view/components/EmployeeCard.fxml"));
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
        // Azul para ativo, cinza para inativo
        if ("Ativo".equalsIgnoreCase(employee.getStatus())) {
            this.getStyleClass().remove("employee--inactive");
            this.status.getStyleClass().remove("status-label--inactive");
        } else {
            this.getStyleClass().add("employee--inactive");
            this.status.getStyleClass().add("status-label--inactive");
        }

        this.position.setText(StringUtils.roleTranslation(employee.getPosition().toUpperCase()));
        this.department.setText("SETOR | " + employee.getDepartment().toUpperCase());
        this.signed.setText("Cadastrado em " + employee.getSigned());
        this.employee = employee;
    }

    @FXML
    public void openEditModal(){
        if (onEditAction != null) {
            onEditAction.accept(this.employee);
        }
    }

//    @FXML
//    public void handleToggleStatus() {
//        if (onToggleStatusAction != null) {
//            onToggleStatusAction.accept(this.employee);
//        }
//    }
}