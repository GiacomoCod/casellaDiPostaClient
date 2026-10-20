# Casella di Posta – Client

Client per un sistema di posta elettronica semplificato, realizzato in **Java 21** con **JavaFX**. Permette di scrivere, inviare, ricevere, leggere ed eliminare mail comunicando con il [server](https://github.com/GiacomoCod/casellaDiPostaServer) tramite socket TCP e messaggi JSON.

## Funzionalità

- Casella di posta con elenco delle mail (ricevute, inviate, in uscita) e anteprima del testo della mail selezionata.
- Composizione e invio di nuove mail a uno o più destinatari.
- Visualizzazione del dettaglio di una mail ed eliminazione dal menu contestuale.
- **Sessione automatica**: due timer periodici inviano le mail in uscita (stato "Da inviare") e richiedono al server le nuove mail. Sono avviabili/interrompibili dal menu dell'interfaccia.
- Indicatore di notifica quando arrivano nuove mail e barra di stato con i messaggi di log.
- Persistenza locale su file `dati.json`, compreso l'ultimo `id` ricevuto dal server.

## Requisiti

- JDK 21
- Maven (o il wrapper incluso `./mvnw`)
- Dipendenze gestite da Maven: JavaFX 21 (`javafx-controls`, `javafx-fxml`) e `org.json`
- Un [server](https://github.com/GiacomoCod/casellaDiPostaServer) in esecuzione e raggiungibile

## Struttura del progetto

```
src/main/java/com/example/demo/
├── HelloApplication.java        # Avvio dell'app JavaFX
├── App.java                     # Main "launcher" (usato per il jar)
├── CasellaController.java       # Finestra principale, timer della sessione automatica
├── NuovaMailController.java     # Finestra di composizione mail
├── VisualizzaMailController.java# Finestra di dettaglio mail
├── PopUpController.java         # Popup di notifica
├── GestioneMail.java            # Comunicazione col server via socket
├── GestioneComando.java         # Messaggio di richiesta inviato al server
├── DataModel.java               # Modello dati, lettura/scrittura JSON, configurazione
├── Mail.java / Stato.java       # Entità mail e stato
src/main/resources/              # Viste FXML, CSS e immagini
Library/                         # Copia della libreria org.json
out/artifacts/demo_jar/          # Jar pre-compilato con configurazione.json e dati.json di esempio
```

## Configurazione

All'avvio il client legge due file nella **stessa cartella del jar / delle classi compilate**. Esempi pronti in `out/artifacts/demo_jar/`.

`configurazione.json`:

```json
{
  "email_utente": "mario.rossi@example.com",
  "server_ip": "localhost",
  "server_port": 12345,
  "timer_invio": 20000,
  "timer_ricezione": 20000
}
```

| Campo | Significato |
|-------|-------------|
| `email_utente` | Indirizzo dell'utente di questa casella |
| `server_ip`, `server_port` | Indirizzo e porta del server (la porta deve coincidere con quella del server) |
| `timer_invio` | Intervallo in ms tra due invii automatici delle mail in uscita |
| `timer_ricezione` | Intervallo in ms tra due richieste di nuove mail al server |

`dati.json` contiene l'elenco delle mail dell'utente (array JSON); può essere un array vuoto al primo avvio.

> Nota: il percorso di `configurazione.json` è costruito con il separatore `\` di Windows (vedi `DataModel.java`), quindi l'avvio su macOS/Linux può richiedere un adattamento.

## Build ed esecuzione

Avvia prima il server, poi:

```bash
./mvnw clean javafx:run
```

In alternativa usa il jar pre-compilato in `out/artifacts/demo_jar/` (con `configurazione.json` e `dati.json` nella stessa cartella).

## Protocollo client–server

Ogni operazione apre una connessione TCP, invia una riga JSON e legge la risposta:

```json
{ "comando": "invia", "mittente": "mario.rossi@example.com", "ultimoId": 5, "jsonArray": [ { "...": "mail" } ] }
```

- `invia`: contiene le mail da inviare; il server risponde confermando le mail ricevute (tramite `uniqueCode`), che il client marca come "Inviata".
- `richiedi`: il client chiede le mail con `id` maggiore di `ultimoId`; `jsonArray` è vuoto.
