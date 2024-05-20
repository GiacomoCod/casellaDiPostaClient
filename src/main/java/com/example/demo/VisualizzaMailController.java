package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class VisualizzaMailController {

    @FXML
    public javafx.scene.control.TextArea TextArea;
    @FXML
    public javafx.scene.control.TextField mittente;
    @FXML
    public javafx.scene.control.TextField oggetto;
    @FXML
    public javafx.scene.control.Label data;
    @FXML
    public javafx.scene.control.MenuButton MenuButton;
    @FXML
    public javafx.scene.control.Label codice;
    @FXML
    public javafx.scene.control.Label stato;
    @FXML
    public javafx.scene.control.Button forwardButton;

    private Stage stage;

    private DataModel model;

    private final GestioneMail gm = new GestioneMail();

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void closeWindow() {
        stage.close(); // Chiudi la finestra corrente
    }
    @FXML
    private Button Indietro;
    @FXML
    public void back(javafx.event.ActionEvent actionEvent) {
        closeWindow();
    }

    public void mostraTesto() {
        TextArea.setText(model.getCurrentMail().getTestoMail());
    }

    public void mostraMittente() {
        mittente.setText(model.getCurrentMail().getMittente());
    }

    public void mostraOggetto() {
        oggetto.setText(model.getCurrentMail().getOggetto());
    }

    public void mostraData() {
        data.setText(model.getCurrentMail().getData());
    }
    public void mostraCodice() {
        codice.setText(model.getCurrentMail().getUniqueCode());
    }
    public void mostraStatoInvio(String st) {
        stato.setText(st);
    }

    public void handleReply(ActionEvent actionEvent) throws IOException {
        //controllo==1 mi permette di settare in automatico il destinatario del messaggio, visto che è una risposta diretta
        gm.creaNuovaMail(model, 1);
    }

    public void handleReplyAll(ActionEvent actionEvent) throws IOException {
        gm.creaNuovaMail(model, 2);
    }

    public void forwarding(ActionEvent actionEvent) throws IOException {
        gm.creaNuovaMail(model, 3);
    }

    public void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model;
    }


    public void setControllo(int controllo) {
        if (controllo==0) {
            MenuButton.setVisible(false);
            forwardButton.setVisible(false);
        }
    }


}
