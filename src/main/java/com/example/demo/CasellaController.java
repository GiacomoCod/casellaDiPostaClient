package com.example.demo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class CasellaController {

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
        gm.creaNuovaMail(model, 0);
    }

    //controllo=1 serve per permettere di visualizzare il bottone "Rispondi" in visualizzazione
    @FXML
    public void handleApriMail(ActionEvent actionEvent) throws IOException {
        gm.apriMailSelezionata(model, mailTableView);
    }

    @FXML
    public void handleApriMailDoubleClick(MouseEvent mouseEventVis) throws IOException {
        if (mouseEventVis.getButton().equals(MouseButton.PRIMARY) && mouseEventVis.getClickCount() == 2) {
            gm.apriMailSelezionata(model, mailTableView);
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

    public void initModel(DataModel model, Stage stage) throws IOException {
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

        stage.setOnCloseRequest(event -> {
            chiudiTimer();
        });

    }

    private void chiudiTimer() {
        if(timerInvio != null) {
            timerInvio.cancel();
            taskInvio.cancel();
            timerRichiesta.cancel();
            taskRichiesta.cancel();
        }
    }

    @FXML
    public void handleMostraPostaInviata(ActionEvent actionEvent) {
        model.aggiornaTableVIew(mailTableView, 3);
    }

    @FXML
    public void handleInvio(ActionEvent actionEvent) throws IOException {
        gm.richiestaOperazioneAlServer(model, "invia");
    }

    @FXML
    public void handleRicevi(ActionEvent actionEvent) throws IOException {
        gm.richiestaOperazioneAlServer(model, "richiedi");
    }

    @FXML
    public void handleDecoloraNotButton(MouseEvent mouseEvent) {
        notificationButton.setStyle("");
    }

    private Timer timerInvio;
    private TimerTask taskInvio;
    private Timer timerRichiesta;
    private TimerTask taskRichiesta;

    @FXML
    public void handleAvviaSessioneAutomatica(ActionEvent actionEvent) {

        timerInvio = new Timer();

        taskInvio = new TimerTask() {
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

        timerRichiesta = new Timer();

        taskRichiesta = new TimerTask() {
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



}





