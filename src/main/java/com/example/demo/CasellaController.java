package com.example.demo;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class CasellaController {


    @FXML
    public Button cancella;
    private DataModel model;

    private final GestioneMail gm = new GestioneMail();

    @FXML
    public TableView<Mail> mailTableView;
    @FXML
    public TextArea barraDiStato;
    @FXML
    public Button notificationButton;

    @FXML
    void handleCreaNuovaMail(MouseEvent event) throws IOException {
        gm.creaNuovaMail(model, mailTableView, 0);
    }

    //controllo=1 serve per permettere di visualizzare il bottone "Rispondi" in visualizzazione
    @FXML
    public void handleApriMail(ActionEvent actionEvent) throws IOException {
        gm.apriMailSelezionata(model, mailTableView, 1);
    }

    @FXML
    public void handleApriMailDoubleClick(MouseEvent mouseEventVis) throws IOException {
        if (mouseEventVis.getButton().equals(MouseButton.PRIMARY) && mouseEventVis.getClickCount() == 2) {
            gm.apriMailSelezionata(model, mailTableView, 1);
        }
    }

    @FXML
    public void handleEliminaRigaSelezionata(ActionEvent event) throws IOException {
        gm.eliminaRigaSelezionata(model, mailTableView);
    }

    @FXML
    public void handelSvuotaCasellaDiPosta(ActionEvent actionEvent) throws IOException {
        gm.svuotaCasellaDiPosta(model, mailTableView);
    }

    @FXML
    public void handleMostraMailUscita(ActionEvent actionEvent) {
        model.aggiornaTableVIew(mailTableView, 1);
    }

    @FXML
    public void handleMostraMailInEntrata(ActionEvent actionEvent) {
        model.aggiornaTableVIew(mailTableView, 2);
    }

    @FXML
    public void handleMostraTutto(ActionEvent actionEvent) {
        model.aggiornaTableVIew(mailTableView, 0);
    }

    @FXML
    public Label utente;

    public void initModel(DataModel model) throws IOException {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model;

        model.configurazioneIniziale();
        model.loadData();
        model.aggiornaTableVIew(mailTableView, 0);
//        mailTableView.itemsProperty().bind((model.getEmailList()));
        gm.setCasellaController(this);
        utente.setText("di " + model.getEmail_utente());

    }


    @FXML
    public void handleMostraPostaInviata(ActionEvent actionEvent) {
        model.aggiornaTableVIew(mailTableView, 3);
    }

    String messaggio = "";

    @FXML
    public void handleInvio(ActionEvent actionEvent) throws IOException {
        gm.richiestaOperazioneAlServer(model, "invia");
    }

    @FXML
    public void handleRicevi(ActionEvent actionEvent) throws IOException {
        gm.richiestaOperazioneAlServer(model, "richiedi");
    }

    @FXML
    public void handleAvviaSessioneAutomatica(ActionEvent actionEvent) {

        Timer timerInvio = new Timer();

        TimerTask taskInvio = new TimerTask() {
            @Override
            public void run() {
                try {
                    gm.richiestaOperazioneAlServer(model, "invia");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        };

        // Esecuzione una volta dopo 5 secondi e ogni 10 secondi
        timerInvio.schedule(taskInvio, 5000, model.getTimer_invio());

        Timer timerRichiesta = new Timer();

        TimerTask taskRichiesta = new TimerTask() {
            @Override
            public void run() {
                try {
                    gm.richiestaOperazioneAlServer(model, "richiedi");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        };

        timerRichiesta.schedule(taskRichiesta, 10000, model.getTimer_ricezione());

    }
    public void setBarraDiStato(String tipo, String messaggioDiLog) {

        if(!messaggioDiLog.isEmpty()) {
            LocalDateTime localDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String dataeora = localDateTime.format(formatter);

            barraDiStato.appendText(dataeora+"- "+tipo+" - "+messaggioDiLog+"\n");
        }
    }
    public void setNotificationButtonColor() {
        notificationButton.setStyle("-fx-background-color:  #FF6347");
    }

    @FXML
    public void handleDecoloraNotButton(MouseEvent mouseEvent) {
        notificationButton.setStyle("");
    }

    @FXML
    public void handleCancella(ActionEvent actionEvent) {
        notificationButton.setStyle("");
    }


}




/*
    public void caricaMailDaStringa(String JsonString) {

        JSONArray jsonArray = new JSONArray(JsonString);
        // Itera su ciascun oggetto JSON nell'array
        for (Object obj : jsonArray) {
            JSONObject jsonObj = (JSONObject) obj;
            // Leggi i singoli campi dall'oggetto JSON
            Integer id = jsonObj.getInt("id");
            String mittente = jsonObj.getString("mittente");
            String destinatario = jsonObj.getString("destinatario");
            String oggetto = jsonObj.getString("oggetto");
            String testoMail = jsonObj.getString("testo_email");
            LocalDateTime data = LocalDateTime.parse(jsonObj.getString("data"));

            model.addEmail(new Mail(id, mittente, destinatario, oggetto, testoMail, data));
        }
    }
*/




