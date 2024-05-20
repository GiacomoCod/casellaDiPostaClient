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

    private Timer timerInvio;
    private TimerTask taskInvio;
    private Timer timerRichiesta;
    private TimerTask taskRichiesta;

    @FXML
    public Label utente;
    @FXML
    public Label testoMailPerVisualizzazione;
    @FXML
    public TextArea visualizzaTestoMail;
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
    public void handleSvuotaCasellaDiPosta(ActionEvent actionEvent) throws IOException {
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
    public void handleMostraPostaInviata(ActionEvent actionEvent) {
        model.aggiornaTableVIew(mailTableView, 3);
    }

    @FXML
    public void handleDecoloraNotButton(MouseEvent mouseEvent) {
        notificationButton.setStyle("");
    }


    public void initModel(DataModel model, Stage stage) throws IOException {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model;

        model.configurazioneIniziale();
        model.loadData();
        model.aggiornaTableVIew(mailTableView, 0);
        gm.setCasellaController(this);
        utente.setText("di " + model.getEmail_utente());

        setVisualizzaTestoMail();

        stage.setOnCloseRequest(event -> {
            interrompiTimer();
        });

        mailTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> model.setCurrentMail(newSelection));
        model.currentMailProperty().addListener((obs, oldMail, newMail) -> {
            if (newMail == null) {
                mailTableView.getSelectionModel().clearSelection();
            } else {
                mailTableView.getSelectionModel().select(newMail);
                testoMailPerVisualizzazione.setVisible(true);
            }
        });

        avviaSessioneAutomatica();
    }

    private void interrompiTimer() {
        if(timerInvio != null) {
            timerInvio.cancel();
            taskInvio.cancel();
            timerRichiesta.cancel();
            taskRichiesta.cancel();
        }
    }

    private void avviaSessioneAutomatica() {
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
        timerInvio.schedule(taskInvio, 10000, model.getTimer_invio());

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

        timerRichiesta.schedule(taskRichiesta, 20000, model.getTimer_ricezione());
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

    public void setVisualizzaTestoMail(){

        model.currentMailProperty().addListener((obs, oldText, newText) -> {
            if (oldText != null) {
                visualizzaTestoMail.textProperty().unbindBidirectional(oldText.testoMailProperty());
            }
            if (newText == null) {
                visualizzaTestoMail.setText("");
            } else {
                visualizzaTestoMail.textProperty().bindBidirectional(newText.testoMailProperty());
            }
        });
    }


    public void handleInterrompiSessione(ActionEvent actionEvent) {
        interrompiTimer();
        setBarraDiStato("INFO","Sessione di collegamento al server interrotta: servizio di invio e ricezione mail non attivo");
        setNotificationButtonColor();
    }

    public void handleAvviaSessione(ActionEvent actionEvent) {
        avviaSessioneAutomatica();
    }
}





