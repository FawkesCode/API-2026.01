package com.fawkes.front.utils;

import com.fawkes.front.MainApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Objects;

public class NavigationManager {

    public static class PageViewer {
        private static String curPage;
        private static String curDescr;


        public static void setCurPage(String page, String pageDescr) {
            curPage = page;
            curDescr = pageDescr;
        }

        public static String getCurPage() {
            return curPage;
        }

        public static String getCurDescr() {
            return curDescr;
        }

    }


    public static Object navigateToPage(Pane container, String pageUrl) {
        try {
            FXMLLoader loader =  new FXMLLoader(Objects.requireNonNull(MainApplication.class.getResource(pageUrl)));
            Parent root = loader.load();

            container.getChildren().clear();
            container.getChildren().add(root);

            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);

            container.getChildren().setAll(root);

            return loader.getController();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
