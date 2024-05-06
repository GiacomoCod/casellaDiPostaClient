package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NuovaMailController {


    private Stage stage; // Dichiarazione dell'oggetto Stage

    private TableView<Mail> mailTableViewAgg;

    private DataModel model;

    private final GestioneMail gm = new GestioneMail();

    public void setDestinatariReply(String mittente) {
        destinatari.setText(mittente);
    }
    public void setDestinatariReplyAll(String destinatari) {
        this.destinatari.setText(destinatari);
    }
    // Metodo per iniettare l'oggetto Stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    @FXML
    private void closeWindow() {
        stage.close(); // Chiudi la finestra corrente
    }

    @FXML
    private Button back;
    @FXML
    private TextField destinatari;
    @FXML
    private Button eliminaTutto;
    @FXML
    private Button inviaMail;
    @FXML
    private TextArea testoMail;
    @FXML
    private TextField oggetto;
    @FXML
    void cancellaTestoMail(ActionEvent event) {
        testoMail.clear();
        destinatari.clear();
        oggetto.clear();
    }
    @FXML
    public void back(ActionEvent event) {
        testoMail.clear();
        closeWindow();
    }


    @FXML
    void inviaMail(ActionEvent event) throws IOException {

        Integer Id = 0;
        String mittente = model.getMail_utente();
        String dest = destinatari.getText();
        String ogg = oggetto.getText();
        String tm = testoMail.getText();
        String stato = "Da inviare";
        String codiceUnivoco = java.util.UUID.randomUUID().toString();

        Mail mail = new Mail(Id, mittente, dest, ogg, tm, LocalDateTime.now().toString(), codiceUnivoco, stato);
        //mail.creaOggettoJson(Id, mittente, dest, ogg, tm, codiceUnivoco, stato);

        String messaggio = controllaDestinatario(destinatari.getText());
        String oggDaContr = oggetto.getText();
        String oggetto = controllaOggetto(oggDaContr);


        if (controlloMessaggioDest(messaggio)) {
            if (controlloMessaggioOgg(oggetto)) {

                model.addEmail(mail);
                model.aggiornaFile();

                //messaggio di invio con successo
                gm.PopUp(messaggio, 0);
                closeWindow();

            } else {
                //messaggio di errore sull'oggetto
                gm.PopUp(oggetto, 0);
            }
        } else {
            //messaggio di errore sul destinatario
            gm.PopUp(messaggio, 0);
        }


    }

    private boolean controlloMessaggioDest(String messaggio) {
        return !messaggio.equals("Destinatario non trovato");
    }
    private boolean controlloMessaggioOgg(String messaggio) {
        return !messaggio.equals("Oggetto non trovato");
    }

    public void setMail(TableView<Mail> mailTableView) {
        mailTableViewAgg = mailTableView;
    }

    public void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model;
    }

    private String controllaDestinatario(String destDaControl) {
        // espressione regolare per validare l'email
        //String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}(?:;[\\s]*[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7})*$";


        // Compila l'espressione regolare in un oggetto Pattern
        Pattern pattern = Pattern.compile(regex);

        // Crea un oggetto Matcher per confrontare l'email con l'espressione regolare
        Matcher matcher = pattern.matcher(destDaControl);

        // Controlla se l'email corrisponde all'espressione regolare
        if (!(matcher.matches())) {
            return "Destinatario non trovato";
        }
        return "Messaggio inserito in posta in uscita!";
    }

    private String controllaOggetto(String oggDaContr) throws IOException {
        if (oggDaContr.isEmpty())
            return "Oggetto non trovato";
        else return oggDaContr;
    }
}
