package com.example.demo;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Comparator;

public class DataModel {

    public ObservableList<Mail> emailList = FXCollections.observableArrayList();

    private final ObjectProperty<Mail> currentMail = new SimpleObjectProperty<>(null);

    public DataModel() throws FileNotFoundException {
    }

    public ObjectProperty<Mail> currentMailProperty() {
        return currentMail;
    }

    public final Mail getCurrentMail() {
        return currentMailProperty().get();
    }

    public final void setCurrentMail(Mail mail) {
        currentMailProperty().set(mail);
    }

    public ObservableList<Mail> getEmailList() {
        return emailList;
    }

    public void setEmailList(ObservableList<Mail> email) {
        this.emailList = email;
    }

    public void addEmail(Mail mail) {
        this.emailList.add(mail);
        //mail.creaOggettoJson(mail.getId(), mail.getMittente(), mail.getDestinatario(), mail.getOggetto(), mail.getTestoMail(), mail.getUniqueCode(), mail.getStato());
    }

    public void aggiornaTableVIew(TableView<Mail> tableView, int tipo) {
        if(tipo == 0) {
            tableView.setItems(emailList);
        }
        else if(tipo == 1) {
            tableView.setItems(emailList.filtered(mail1 -> mail1.getId()==0));
        }
        else if(tipo == 2) {
            tableView.setItems(emailList.filtered(mail1 -> mail1.getDestinatari().equals(getEmail_utente())));
        }
        else if(tipo == 3) {
            tableView.setItems(emailList.filtered(mail1 -> mail1.getStato().equals("Inviata")));
        }

    }

    public int getUltimoId() {

        if (emailList.isEmpty()) {
            return 0;
        }
        else {
            return emailList.getLast().getId();
        }
    }

    private final String jarPath = DataModel.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    private final File jarFile = new File(jarPath);
    private final File fileDati = new File(jarFile.getParent(), "dati.json");
    FileReader reader = new FileReader(fileDati);
    // Crea un JSONTokener dal file JSON
    JSONTokener tokener = new JSONTokener(reader);
    // Crea un JSONArray utilizzando il JSONTokener
    JSONArray jsonArray = new JSONArray(tokener);

    // Itera su ciascun oggetto JSON nell'array
    public void loadData() {

        for (Object obj : jsonArray) {
            JSONObject jsonObj = (JSONObject) obj;

            aggiungiMailDaJson(jsonObj);

        }
        emailList.sort(Comparator.comparing(Mail::getId));
    }

    public void aggiungiMailDaJson(JSONObject jsonObj) {
        // Leggi i singoli campi dall'oggetto JSON
        Integer id = jsonObj.getInt("id");
        String mittente = jsonObj.getString("mittente");
        String destinatari = jsonObj.getString("destinatari");
        String oggetto = jsonObj.getString("oggetto");
        String testoMail = jsonObj.getString("testoMail");
        String data = jsonObj.getString("data");
        String uniqueCode = jsonObj.getString("uniqueCode");
        String stato = jsonObj.getString("stato");

        //String dataFormattata = formattaData(data);

        Mail mail = new Mail(id, mittente, destinatari, oggetto, testoMail, data, uniqueCode, stato);

        this.addEmail(mail);
    }

/*
    private String formattaData(LocalDateTime data) {
        // Creazione di un oggetto LocalDateTime rappresentante la data e l'ora attuali
        // Formattazione dell'oggetto LocalDateTime come stringa
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return data.format(formatter);
    }
*/

    public void aggiornaFile() throws IOException {
        // Scrivi la struttura dati Java aggiornata nel file JSON
        FileWriter writer = new FileWriter(fileDati);
        JSONArray jsonArrayAgg = new JSONArray(emailList);
        writer.write(jsonArrayAgg.toString());
        writer.close();
    }

    public ObservableList<Mail> getEmailListUscita() {
        return emailList.filtered((mail1 -> mail1.getStato().equals("Da inviare")));
    }


    private String email_utente;
    private String server_ip;
    private int server_port;
    private int timer_invio;
    private int timer_ricezione;

    public void configurazioneIniziale() throws IOException {

        String pathCon = jarFile.getParent()+"\\configurazione.json";

        String content = new String(Files.readAllBytes(Path.of(pathCon)));

        JSONObject jobj = new JSONObject(content);

        email_utente = jobj.getString("email_utente");
        server_ip = jobj.getString("server_ip");
        server_port = jobj.getInt("server_port");
        timer_invio = jobj.getInt("timer_invio");
        timer_ricezione = jobj.getInt("timer_ricezione");
    }

    public String getEmail_utente() {
        return email_utente;
    }

    public String getServer_ip() {
        return server_ip;
    }

    public int getServer_port() {
        return server_port;
    }

    public int getTimer_invio() {
        return timer_invio;
    }

    public int getTimer_ricezione() {
        return timer_ricezione;
    }
}
