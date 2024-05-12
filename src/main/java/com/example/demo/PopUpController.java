package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class PopUpController {

    @FXML
    public Label text;
    @FXML
    public Button cancelButton;
    @FXML
    public Button okButton;

    private Stage stage; // Dichiarazione dell'oggetto Stage

    private boolean response;

    // Metodo per iniettare l'oggetto Stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    @FXML
    private void closeWindow() {
        stage.close(); // Chiudi la finestra corrente
    }


    public void back(ActionEvent actionEvent) {
        response = false;
        closeWindow();
    }

    public void setText(String messaggio) {
        text.setText(messaggio);
    }

    @FXML
    public void confirm(ActionEvent actionEvent) {
        response = true;
        closeWindow();
    }

    public void setControllo(int controllo) {
        if (controllo == 1) {
            okButton.setVisible(true);
            cancelButton.setVisible(true);
        } else {
            okButton.setVisible(true);
            cancelButton.setVisible(false);
        }


    }

    public boolean getResponse() {
        return response;
    }

}
