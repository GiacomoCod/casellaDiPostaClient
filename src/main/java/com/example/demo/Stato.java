package com.example.demo;

public class Stato {

    private boolean inviata;
    private boolean inCorsoDiInvio = true;
    private boolean statoCorrente = true;

    public void invia() {
        if (!inviata) {
            inCorsoDiInvio = true;
            // Invio della mail in corso...
            System.out.println("Invio dell'ordine in corso...");
            inviata = true;
            inCorsoDiInvio = false;
        } else {
            System.out.println("L'ordine è già stato inviato.");
        }
    }

    public boolean isInviata() {
        return inviata;
    }

    public boolean isInCorsoDiInvio() {
        return inCorsoDiInvio;
    }

    public void setInCorsoDiInvio(boolean inCorsoDiInvio) {
        this.inCorsoDiInvio = inCorsoDiInvio;
    }

    public void setInviata(boolean inviata) {
        this.inviata = inviata;
    }

    public boolean getStatoCorrente() {
        return statoCorrente;
    }

    public void setStatoCorrente(boolean statoCorrente) {
        this.statoCorrente = statoCorrente;
    }
}