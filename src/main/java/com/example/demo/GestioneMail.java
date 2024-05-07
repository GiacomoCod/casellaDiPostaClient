package com.example.demo;

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

    private DataModel model;

    public void creaNuovaMail(DataModel model, TableView<Mail> mailTableView, int controllo) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("formInviaMail.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        NuovaMailController nmc = loader.getController();
        nmc.setStage(stage);
        nmc.setMail(mailTableView);
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
        }
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();

    }

    public void apriMailSelezionata(DataModel model, TableView<Mail> tv, int controllo) throws IOException {
        //Mail selectedItem = recordTableView.getSelectionModel().getSelectedItem();
        //Mail selectedItem = mailTableView.getSelectionModel().getSelectedItem();
        model.setCurrentMail(tv.getSelectionModel().getSelectedItem());
        Mail selectedItem = model.getCurrentMail();
        if (selectedItem != null) {

            FXMLLoader loaderVisualizza = new FXMLLoader(getClass().getResource("formVisualizzaMail.fxml"));
            Parent root = loaderVisualizza.load();
            Stage stage = new Stage();
            VisualizzaMailController vmc = loaderVisualizza.getController();

            vmc.initModel(model);
            vmc.setStage(stage);
            vmc.setMail(tv);
            if (selectedItem.getMittente().equals(model.getEmail_utente())) {
                controllo = 0;
            }
            int stat = 1;
            if(selectedItem.getDestinatari().equals(model.getEmail_utente())) {
                stat = 0;
            }
            vmc.setControllo(controllo);
            vmc.setStato(stat);

            vmc.mostraOggetto();
            vmc.mostraTesto();
            vmc.mostraMittente();
            vmc.mostraData();
            vmc.mostraCodice();
            vmc.mostraStatoInvio(selectedItem.getStato());

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
        popUp.initialize(root);
        stage.showAndWait();
        return popUp.getResponse();
    }



    public void richiestaOperazioneAlServer(DataModel modello, String comando) throws IOException {

        //compongo il io array json con tutte le mail da inviare (che hanno stato "Da inviare")

        System.out.println(comando);

        JSONArray jsonArray;
        if(comando.equals("invia")) {
            jsonArray = new JSONArray(modello.getEmailListUscita());
        } else {
            jsonArray = new JSONArray();
        }

        //altrimenti il Jsonarray mi si riempi  a prescindere, se il comando è "richiedi" vorrei mandare un json vuoto
        /*
        if(comando.equals("Invia")) {
            for (Mail mail : modello.getEmailListUscita()) {

                JSONObject jobj = mail.getJobj();
                jsonArray.put(jobj);
            }
        }
       */

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

            socket.setSoTimeout(10000);


            // Risposta dal server
            JSONArray jsonResponse = getJsonArray(socket);

            System.out.println("risposta dal server:\n" + jsonResponse);

            //effettuo controllo sui codici univoci ricevuti in risposta dal server, se corrispondono posso fleggare le mail come inviate

            if (comando.equals("invia")) {
                for (Object obj : jsonResponse) {
                    JSONObject jsonObj = (JSONObject) obj;
                    String codiceDaContr = jsonObj.getString("uniqueCode");
                    int idDaIns = jsonObj.getInt("id");

                    // da implementare tramite filter
                    for (Mail mail : modello.getEmailList()) {
                        if (codiceDaContr.equals(mail.getUniqueCode())) {
                            mail.setStato("Inviata");
                            mail.setId(idDaIns);
                            //modello.getEmailList().remove(mail);
                        }
                    }
                    modello.aggiornaFile();
                }
            } else {
                //il server ha effettuato operazione di restituzione delle mail che il client ha ricevuto
                for (Object obj : jsonResponse) {
                    JSONObject jsonObj = (JSONObject) obj;
                    modello.aggiungiMailDaJson(jsonObj);
                }
                modello.aggiornaFile();
            }

            //ciclo di controllo su tutta la lista controllando se il codice univoco corrispond ee segnare lo stato della mail come inviato
/*
            if ((mail.getId() == idDaContr) && (mail.getUniqueCode().equals(codiceDaContr))) {
                mail.setStato("Inviata.");
                mail.creaOggettoJson(mail.getId(), mail.getMittente(), mail.getDestinatario(), mail.getOggetto(), mail.getTestoMail(), mail.getUniqueCode(), mail.getStato());
                modello.addEmail(mail);
                //model.aggiornaTableVIew(mailTableViewAgg, 1);
                modello.incrementUltimoId();
                modello.aggiornaFile(jobj);
                modello.removeFromeListaMailUscita(mail);


                System.out.println(jsonResponse);
            }


 */

            socket.close();

        } catch (IOException e) {
            if (e.getMessage().equals("Connessione rifiutata")) {
                System.out.println("Il server rifiuta la connessione. Riprova più tardi.");
            } else {
                System.out.println("Errore di connessione al server. " + e.getMessage());
                PopUp("Errore di connessione al server. " + e.getMessage(), 0);
            }
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }



    }

    private static JSONArray getJsonArray(Socket socket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        line = reader.readLine();
        responseBuilder.append(line);

        //JSONObject jobjRecived = new JSONObject(responseBuilder.toString());
        return new JSONArray(responseBuilder.toString());
    }

}
