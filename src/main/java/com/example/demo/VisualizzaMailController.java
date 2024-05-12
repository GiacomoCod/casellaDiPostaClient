package com.example.demo;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    private Stage stage; // Dichiarazione dell'oggetto Stage

    private DataModel model;

    private TableView<Mail> mailTableViewAgg;

    private final GestioneMail gm = new GestioneMail();

    // Metodo per iniettare l'oggetto Stage
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

    public void setMail(TableView<Mail> mailTableView) {
        mailTableViewAgg = mailTableView;
    }

    public void handleReply(ActionEvent actionEvent) throws IOException {
        //controllo==1 mi permette di settare in automatico il destinatario del messaggio, visto che è una risposta diretta
        gm.creaNuovaMail(model, mailTableViewAgg, 1);
    }

    public void handleReplyAll(ActionEvent actionEvent) throws IOException {
        gm.creaNuovaMail(model, mailTableViewAgg, 2);
    }

    public void forwarding(ActionEvent actionEvent) throws IOException {
        gm.creaNuovaMail(model, mailTableViewAgg, 3);
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
