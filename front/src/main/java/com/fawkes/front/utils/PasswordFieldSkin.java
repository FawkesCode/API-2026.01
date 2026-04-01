package com.fawkes.front.utils;

import javafx.scene.control.PasswordField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.skin.TextFieldSkin;

public class PasswordFieldSkin extends TextFieldSkin {
    private final ToggleButton showButton;

    public PasswordFieldSkin(PasswordField control, ToggleButton showButton) {
        super(control);
        this.showButton = showButton;

        showButton.selectedProperty().addListener((obs, oldVal, newVal) -> {
            control.setText(control.getText());
        });
    }

    @Override
    protected String maskText(String txt) {
        if (showButton != null && showButton.isSelected()) {
            return txt;
        }

        if(getSkinnable() instanceof PasswordField) {
            int n = txt.length();
            return "•".repeat(n);
        }

        return txt;
    }


}
