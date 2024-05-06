package com.example.demo;

import org.json.JSONArray;

public class GestioneComando {

    private String comando;
    private String mittente;
    private JSONArray jsonArray;
    private int ultimoId;

    public GestioneComando(String comando, String mittente, JSONArray jsonArraytoSend, int ultimoId) {
        this.comando = comando;
        this.mittente = mittente;
        this.jsonArray = jsonArraytoSend;
        this.ultimoId = ultimoId;
    }

    public String getComando() {
        return comando;
    }
    public void setComando(String comando) {
        this.comando = comando;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }
    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }
    public String getMittente() {
        return mittente;
    }
    public void setMittente(String mittente) {
        this.mittente = mittente;
    }
    public int getUltimoId() {
        return ultimoId;
    }
    public void setUltimoId(int ultimoId) {
        this.ultimoId = ultimoId;
    }

}
