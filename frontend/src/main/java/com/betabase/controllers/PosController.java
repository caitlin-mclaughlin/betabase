package com.betabase.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PosController implements Initializable {

    @FXML private BorderPane mainPane;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/betabase/views/header.fxml"));
            Parent header = loader.load();

            HeaderController headerController = loader.getController();
            headerController.setTitle("Dashboard");

            mainPane.setTop(header);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
