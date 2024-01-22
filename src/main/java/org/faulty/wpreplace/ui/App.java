package org.faulty.wpreplace.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.IOException;

@Configuration
@ComponentScan(basePackages = "org.faulty.wpreplace")
public class App extends Application {

    private AbstractApplicationContext context;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("LoadMission.fxml"));
        fxmlLoader.setControllerFactory(context::getBean);
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        primaryStage.setTitle("Select Source Mission");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    @Override
    public void init() {
        context = new AnnotationConfigApplicationContext(App.class);
    }

    @Override
    public void stop() {
        context.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
