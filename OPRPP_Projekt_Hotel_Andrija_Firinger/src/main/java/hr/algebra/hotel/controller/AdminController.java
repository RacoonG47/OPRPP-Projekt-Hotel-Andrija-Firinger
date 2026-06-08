package hr.algebra.hotel.controller;

import hr.algebra.hotel.dao.repo.AdminRepository;
import hr.algebra.hotel.service.AdminService;
import hr.algebra.hotel.util.SessionUtil;
import hr.algebra.hotel.util.XmlImportUtil;
import hr.algebra.hotel.util.XmlUtil;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class AdminController {

    @FXML private TextField importPathField;
    @FXML private Label statusLabel;

    private final AdminService adminService = new AdminService(new AdminRepository());

    @FXML
    private void handleDeleteAllData() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "This will delete ALL data. Are you sure?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            String username = SessionUtil.getLoggedInUser().getUsername();

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    adminService.deleteAllData();
                    return null;
                }
            };

            task.setOnSucceeded(e -> {
                XmlUtil.logAction(username, "DELETE_ALL_DATA");
                statusLabel.setText("All data deleted successfully.");
            });

            task.setOnFailed(e -> statusLabel.setText("Failed to delete data."));

            new Thread(task).start();
        }
    }

    @FXML
    private void handleBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML files", "*.xml")
        );
        File file = fileChooser.showOpenDialog(importPathField.getScene().getWindow());
        if (file != null) {
            importPathField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void handleImport() {
        String path = importPathField.getText().trim();
        if (path.isEmpty()) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please select a file first.");
            return;
        }

        File file = new File(path);
        if (!file.exists()) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("File not found.");
            return;
        }

        String username = SessionUtil.getLoggedInUser().getUsername();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                XmlImportUtil.importFromXml(file);
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            XmlUtil.logAction(username, "IMPORT_DATA: " + path);
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("Data imported successfully.");
        });

        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Import failed: " + task.getException().getMessage());
        });

        statusLabel.setText("Importing...");
        new Thread(task).start();
    }

    @FXML
    private void handleGoToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/algebra/hotel/main.fxml"));
            Stage stage = (Stage) importPathField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load main screen", e);
        }
    }
}