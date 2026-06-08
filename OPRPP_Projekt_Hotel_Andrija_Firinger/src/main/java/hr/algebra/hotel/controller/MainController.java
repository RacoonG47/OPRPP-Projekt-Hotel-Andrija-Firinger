package hr.algebra.hotel.controller;

import hr.algebra.hotel.util.SessionUtil;
import hr.algebra.hotel.util.XmlUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML private BorderPane root;

    @FXML
    public void initialize() {
        loadCenter("/hr/algebra/hotel/hotel.fxml");
    }

    @FXML
    private void handleHotels() {
        loadCenter("/hr/algebra/hotel/hotel.fxml");
    }

    @FXML
    private void handleCities() {
        loadCenter("/hr/algebra/hotel/city.fxml");
    }

    @FXML
    private void handleAmenities() {
        loadCenter("/hr/algebra/hotel/amenity.fxml");
    }

    @FXML
    private void handleItinerary() {
        loadCenter("/hr/algebra/hotel/itinerary.fxml");
    }

    @FXML
    private void handleLogout() {
        try {
            XmlUtil.logAction(SessionUtil.getLoggedInUser().getUsername(), "LOGOUT");
            SessionUtil.clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/algebra/hotel/login.fxml"));
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load login screen", e);
        }
    }

    private void loadCenter(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            root.setCenter(loader.load());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load view: " + fxmlPath, e);
        }
    }
}