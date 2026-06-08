package hr.algebra.hotel.controller;

import hr.algebra.hotel.dao.repo.CityRepository;
import hr.algebra.hotel.model.City;
import hr.algebra.hotel.service.CityService;
import hr.algebra.hotel.util.SessionUtil;
import hr.algebra.hotel.util.XmlUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class CityController {

    @FXML private TableView<City> cityTable;
    @FXML private TableColumn<City, String> nameColumn;
    @FXML private TableColumn<City, String> countryColumn;
    @FXML private TextField nameField;
    @FXML private TextField countryField;
    @FXML private Label statusLabel;

    private final CityService cityService = new CityService(new CityRepository());
    private City selectedCity;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        refresh();
    }

    @FXML
    private void handleTableClick() {
        selectedCity = cityTable.getSelectionModel().getSelectedItem();
        if (selectedCity == null) return;
        nameField.setText(selectedCity.getName());
        countryField.setText(selectedCity.getCountry());
    }

    @FXML
    private void handleAdd() {
        City city = buildFromForm(null);
        if (city == null) return;
        cityService.insert(city);
        XmlUtil.logAction(SessionUtil.getLoggedInUser().getUsername(), "ADD_CITY: " + city.getName());
        refresh();
        clearForm();
        statusLabel.setText("City added.");
    }

    @FXML
    private void handleUpdate() {
        if (selectedCity == null) {
            statusLabel.setText("Select a city first.");
            return;
        }
        City city = buildFromForm(selectedCity.getId());
        if (city == null) return;
        cityService.update(city);
        XmlUtil.logAction(SessionUtil.getLoggedInUser().getUsername(), "UPDATE_CITY: " + city.getName());
        refresh();
        clearForm();
        statusLabel.setText("City updated.");
    }

    @FXML
    private void handleDelete() {
        if (selectedCity == null) {
            statusLabel.setText("Select a city first.");
            return;
        }
        cityService.delete(selectedCity.getId());
        XmlUtil.logAction(SessionUtil.getLoggedInUser().getUsername(), "DELETE_CITY: " + selectedCity.getName());
        refresh();
        clearForm();
        statusLabel.setText("City deleted.");
    }

    private City buildFromForm(Integer id) {
        String name = nameField.getText().trim();
        String country = countryField.getText().trim();
        if (name.isEmpty() || country.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            return null;
        }
        return new City(id, name, country);
    }

    private void refresh() {
        cityTable.setItems(FXCollections.observableArrayList(cityService.findAll()));
        selectedCity = null;
    }

    private void clearForm() {
        nameField.clear();
        countryField.clear();
    }
}