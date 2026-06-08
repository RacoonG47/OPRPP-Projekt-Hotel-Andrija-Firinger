package hr.algebra.hotel.controller;

import hr.algebra.hotel.dao.repo.UserRepository;
import hr.algebra.hotel.model.User;
import hr.algebra.hotel.service.UserService;
import hr.algebra.hotel.util.SessionUtil;
import hr.algebra.hotel.util.XmlUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private TextField regUsernameField;
    @FXML private PasswordField regPasswordField;

    private final UserService userService = new UserService(new UserRepository());

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            return;
        }

        userService.login(username, password).ifPresentOrElse(
                user -> {
                    SessionUtil.setLoggedInUser(user);
                    XmlUtil.logAction(username, "LOGIN");
                    navigateToMain(user);
                },
                () -> errorLabel.setText("Invalid username or password.")
        );
    }

    @FXML
    private void handleRegister() {
        String username = regUsernameField.getText().trim();
        String password = regPasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all registration fields.");
            return;
        }

        if (userService.usernameExists(username)) {
            errorLabel.setText("Username already taken.");
            return;
        }

        User newUser = new User(null, username, password, User.Role.KORISNIK);
        userService.insert(newUser);
        XmlUtil.logAction(username, "REGISTER");
        errorLabel.setStyle("-fx-text-fill: green;");
        errorLabel.setText("Registration successful. You can now log in.");
        regUsernameField.clear();
        regPasswordField.clear();
    }

    private void navigateToMain(User user) {
        try {
            String fxml = user.isAdmin() ? "/hr/algebra/hotel/admin.fxml" : "/hr/algebra/hotel/main.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load main screen", e);
        }
    }
}