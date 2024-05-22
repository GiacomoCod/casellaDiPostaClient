package com.example.demo;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

public class GestioneMail {

    private CasellaController controller;

    public void creaNuovaMail(DataModel model, int controllo) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("formInviaMail.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        NuovaMailController nmc = loader.getController();
        nmc.setStage(stage);
        nmc.initModel(model);
        if (controllo == 1) {
            nmc.setDestinatariReply(model.getCurrentMail().getMittente());
        } else if (controllo == 2) {

            String[] nomiArray = model.getCurrentMail().getDestinatari().split(";");

            StringBuilder destinatari = new StringBuilder(model.getCurrentMail().getMittente());

            for(String str : nomiArray) {
                if (!str.equals(model.getEmail_utente())) {
                    destinatari.append(";").append(str);
                }
            }
            nmc.setDestinatariReplyAll(String.valueOf(destinatari));
        } else if (controllo == 3) {
            nmc.setOggetto(model.getCurrentMail().getOggetto());
            nmc.setTestoMail(model.getCurrentMail().getTestoMail());
        }
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();


    }

    public void apriMailSelezionata(DataModel model, TableView<Mail> tv) throws IOException {
        model.setCurrentMail(tv.getSelectionModel().getSelectedItem());
        Mail selectedItem = model.getCurrentMail();
        if (selectedItem != null) {

            FXMLLoader loaderVisualizza = new FXMLLoader(getClass().getResource("formVisualizzaMail.fxml"));
            Parent root = loaderVisualizza.load();
            Stage stage = new Stage();
            VisualizzaMailController vmc = loaderVisualizza.getController();

            vmc.initModel(model);
            vmc.setStage(stage);

            int controllo;
            if (selectedItem.getMittente().equals(model.getEmail_utente())) {
                controllo = 0;
            }
            else controllo = 1;

            vmc.setControllo(controllo);
            vmc.mostraOggetto();
            vmc.mostraTesto();
            vmc.mostraMittente();
            vmc.mostraData();
            vmc.mostraStatoInvio(selectedItem.getStato());
            //stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        }

    }

    public void eliminaRigaSelezionata(DataModel model, TableView<Mail> mailTableView) throws IOException {
        Mail selectedItem = mailTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if(this.PopUp("Sicuro di voler eliminare?", 1)) {
                model.getEmailList().remove(selectedItem);
                model.aggiornaFile();
            }
        }
    }

    public void svuotaCasellaDiPosta(DataModel model, TableView<Mail> mailTableView) throws IOException {
        if(this.PopUp("Sicuro di voler eliminare tutto?", 1)){
            mailTableView.getItems().clear();
            model.getEmailList().clear();
            model.aggiornaFile();
        }
    }

    //il controllo serve per poter settare i bottoni si/no oppure il bottone ok, in base a che tipo
    //di messaggio viene fuori dal pop up
    public boolean PopUp(String messaggio, int controllo) throws IOException {
        FXMLLoader loadPopUp = new FXMLLoader(getClass().getResource("popUp.fxml"));
        Stage stage = new Stage();
        Parent root = loadPopUp.load();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        PopUpController popUp = loadPopUp.getController();
        popUp.setStage(stage);
        popUp.setText(messaggio);
        popUp.setControllo(controllo);
        stage.showAndWait();
        return popUp.getResponse();
    }

    public void richiestaOperazioneAlServer(DataModel modello, String comando) throws IOException {

        //compongo il io array json con tutte le mail da inviare (che hanno stato "Da inviare")
        JSONArray jsonArray;
        if(comando.equals("invia")) {
            jsonArray = new JSONArray(modello.getEmailListUscita());
            if(jsonArray.isEmpty()) {
                return;
            }
        } else {
            jsonArray = new JSONArray();
        }

        System.out.println("lista mail da inviare:\n" + jsonArray);

        try (Socket socket = new Socket(modello.getServer_ip(), modello.getServer_port())) {

            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true);
            String toSend;

            //se il comando è "Invia" vuol dire che il client vuole inviare mail, se il comando è "Ricevi", vuol dire che il client sta richiedendo al server le mail da ricevere

            GestioneComando im = new GestioneComando(comando, modello.getEmail_utente(), jsonArray, modello.getUltimoId());
            JSONObject jobj = new JSONObject(im);
            toSend = jobj.toString();

            System.out.println("toSend: "+toSend);

            writer.println(toSend);

            socket.setSoTimeout(60000);


            // Sezione che si occupa dell risposta dal server

            JSONArray jsonResponse = getJsonArray(socket);

            System.out.println("risposta dal server:\n" + jsonResponse);

            //effettuo controllo sui codici univoci ricevuti in risposta dal server, se corrispondono posso fleggare le mail come inviate

            if (comando.equals("invia")) {
                //controller.setBarraDiStato("DEBUG", "jsonresponse: "+jsonResponse);
                for (Object obj : jsonResponse) {
                    JSONObject jsonObj = (JSONObject) obj;
                    String codiceDaContr = jsonObj.getString("uniqueCode");

                    // da implementare tramite filter
                    for (Mail mail : modello.getEmailList().filtered(mail -> mail.getId()==0)) {
                        if (codiceDaContr.equals(mail.getUniqueCode())) {
                            mail.setStato("Inviata");
                            controller.setBarraDiStato("INFO","Messaggi in posta in uscita inviati correttamente");
                            controller.setNotificationButtonColor();
                        }
                    }
                    modello.aggiornaFile();
                }

            } else { //l'operazione sarà di "richiesta" al server
                //il server ha effettuato operazione di restituzione delle mail che il client ha ricevuto
                for (Object obj : jsonResponse) {
                    JSONObject jsonObj = (JSONObject) obj;
                    modello.aggiungiMailDaJson(jsonObj);

                }
                modello.aggiornaFile();

                if(!jsonResponse.isEmpty()) { 
                    String numeroMessaggi = String.valueOf(jsonResponse.length());
                    controller.setBarraDiStato("INFO", "Messaggi ricevuti: "+numeroMessaggi);
                    controller.setNotificationButtonColor();
                }
            }

        } catch (IOException e) {
            if (e.getMessage().equals("Connessione rifiutata")) {
                System.out.println("Il server rifiuta la connessione. Riprova più tardi.");
            } else {
                System.out.println("Errore di connessione al server. " + e.getMessage());
                Platform.runLater(() -> {
                    try {
                        controller.setBarraDiStato("ERRORE","Errore di connessione al server. " + e.getMessage());
                        controller.setNotificationButtonColor();
                        PopUp("Errore di connessione al server. " + e.getMessage(), 0);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        } catch (JSONException e) {
            controller.setBarraDiStato("ERRORE", "Errore nella lettura del JSON. " + e.getMessage());
            controller.setNotificationButtonColor();
            System.out.println(e.getMessage());
        }

    }

    private static JSONArray getJsonArray(Socket socket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        line = reader.readLine();
        responseBuilder.append(line);
        return new JSONArray(responseBuilder.toString());
    }

    public void setCasellaController(CasellaController controller) {
        this.controller = controller;
    }

}
