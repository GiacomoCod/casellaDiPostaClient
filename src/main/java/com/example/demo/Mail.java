package com.example.demo;

import javafx.beans.property.*;

public class Mail {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty mittente = new SimpleStringProperty();
    private final StringProperty destinatari = new SimpleStringProperty();
    private final StringProperty oggetto = new SimpleStringProperty();
    private final StringProperty testoMail = new SimpleStringProperty();
    private final StringProperty data = new SimpleStringProperty();
    private final StringProperty uniqueCode = new SimpleStringProperty();
    private final StringProperty stato = new SimpleStringProperty();

    public Mail(Integer id, String mittente, String destinatari, String oggetto, String testoMail, String data, String uniqueCode, String stato) {
        this.id.set(id);
        this.mittente.set(mittente);
        this.destinatari.set(destinatari);
        this.oggetto.set(oggetto);
        this.testoMail.set(testoMail);
        this.data.set(data);
        this.uniqueCode.set(uniqueCode);
        this.stato.set(stato);
    }

    public IntegerProperty idProperty() {return id;}
    public StringProperty mittenteProperty() {
        return mittente;
    }
    public StringProperty destinatariProperty() {
        return destinatari;
    }
    public StringProperty oggettoProperty() {
        return oggetto;
    }
    public StringProperty testoMailProperty() {
        return testoMail;
    }
    public StringProperty dataProperty() {return data; }
    public StringProperty uniqueCodeProperty() {return uniqueCode;}
    public StringProperty statoProperty() {return stato;}

    public int getId() {
        return id.get();
    }
    public void setId(Integer idDaIns) {
        id.set(idDaIns);
    }

    public String getMittente() {
        return mittente.get();
    }

    public String getDestinatari() {
        return destinatari.get();
    }

    public String getOggetto() {
        return oggetto.get();
    }

    public String getTestoMail() {
        return testoMail.get();
    }

    public String getData() {
        return data.get();
    }

    public String getUniqueCode() {
        return uniqueCode.get();
    }

    public String getStato() {
        return stato.get();
    }
    public void setStato(String stato) {
        this.stato.set(stato);
    }


}
