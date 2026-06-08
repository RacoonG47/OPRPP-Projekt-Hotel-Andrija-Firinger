package hr.algebra.hotel.application;

import hr.algebra.hotel.util.ConfigUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HotelApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        ConfigUtil config = ConfigUtil.getInstance();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/hr/algebra/hotel/login.fxml")
        );

        Scene scene = new Scene(loader.load(),
                config.getScreenWidth(),
                config.getScreenHeight()
        );

        stage.setTitle("Hotel Management");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}