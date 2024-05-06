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
    public Button si;
    @FXML
    public Button no;
    @FXML
    public Button ok;

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

    public void setText(String text) {
        this.text.setText(text);
    }

    @FXML
    public void confirm(ActionEvent actionEvent) {
        response = true;
        closeWindow();
    }

    public void setControllo(int controllo) {
        if (controllo == 1) {
            si.setVisible(true);
            no.setVisible(true);
            ok.setVisible(false);
        } else {
            ok.setVisible(true);
        }


    }

    public boolean getResponse() {
        return response;
    }


    public void initialize(Parent root) {
        // Calcola la larghezza necessaria per visualizzare l'intero testo
        double width = text.prefWidth(-1);

        // Imposta la larghezza preferita della Label e della finestra
        text.setPrefWidth(width);
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setWidth(width);
    }
}
