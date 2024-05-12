package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.json.JSONObject;
import java.io.IOException;



public class HelloApplication extends Application {

    public DataModel model1;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("casellaPostaView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        CasellaController casellaController = fxmlLoader.getController();

        DataModel model = new DataModel();
        casellaController.initModel(model);
        //casellaController.inviaComando();


        //casellaController.init();
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}