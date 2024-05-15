package com.example.demo;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
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

//    public void setStage(Stage stage) {
//        this.stage = stage;
//    }

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
        String mittente = model.getEmail_utente();
        String dest = destinatari.getText();
        String ogg = oggetto.getText();
        String tm = testoMail.getText();
        String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        String stato = "Da inviare";
        String codiceUnivoco = java.util.UUID.randomUUID().toString();

        Mail mail = new Mail(Id, mittente, dest, ogg, tm, data, codiceUnivoco, stato);

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
        boolean ris1 = messaggio.equals("L'indirizzo mail inserito non è corretto");
        boolean ris2 = messaggio.equals("Inserire un destinatario");
        return !(ris1||ris2);
    }
    private boolean controlloMessaggioOgg(String messaggio) {
        return !messaggio.equals("Inserire un oggetto");
    }

//    public void setMail(TableView<Mail> mailTableView) {
//        mailTableViewAgg = mailTableView;
//    }

    public void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model;
    }

    private String controllaDestinatario(String destDaControl) {

        if(destDaControl.isEmpty()) {
            return "Inserire un destinatario";
        }

        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}(?:;[\\s]*[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7})*$";


        // Compila l'espressione regolare in un oggetto Pattern
        Pattern pattern = Pattern.compile(regex);

        // Crea un oggetto Matcher per confrontare l'email con l'espressione regolare
        Matcher matcher = pattern.matcher(destDaControl);

        // Controlla se l'email corrisponde all'espressione regolare
        if (!(matcher.matches())) {
            return "L'indirizzo mail inserito non è corretto";
        }
        return "Messaggio inserito in posta in uscita!";
    }

    private String controllaOggetto(String oggDaContr) throws IOException {
        if (oggDaContr.isEmpty())
            return "Inserire un oggetto";
        else return oggDaContr;
    }

    public void setOggetto(String oggetto) {
        this.oggetto.setText(oggetto);
    }

    public void setTestoMail(String testoMail) {
        this.testoMail.setText(testoMail);
    }
}
