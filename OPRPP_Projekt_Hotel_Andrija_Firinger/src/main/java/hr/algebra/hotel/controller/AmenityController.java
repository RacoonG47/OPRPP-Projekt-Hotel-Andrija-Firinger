package hr.algebra.hotel.controller;

import hr.algebra.hotel.dao.repo.AmenityRepository;
import hr.algebra.hotel.model.Amenity;
import hr.algebra.hotel.service.AmenityService;
import hr.algebra.hotel.util.SessionUtil;
import hr.algebra.hotel.util.XmlUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class AmenityController {

    @FXML private TableView<Amenity> amenityTable;
    @FXML private TableColumn<Amenity, String> nameColumn;
    @FXML private TextField nameField;
    @FXML private Label statusLabel;

    private final AmenityService amenityService = new AmenityService(new AmenityRepository());
    private Amenity selectedAmenity;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        refresh();
    }

    @FXML
    private void handleTableClick() {
        selectedAmenity = amenityTable.getSelectionModel().getSelectedItem();
        if (selectedAmenity == null) return;
        nameField.setText(selectedAmenity.getName());
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            statusLabel.setText("Please enter a name.");
            return;
        }
        amenityService.insert(new Amenity(null, name));
        XmlUtil.logAction(SessionUtil.getLoggedInUser().getUsername(), "ADD_AMENITY: " + name);
        refresh();
        clearForm();
        statusLabel.setText("Amenity added.");
    }

    @FXML
    private void handleUpdate() {
        if (selectedAmenity == null) {
            statusLabel.setText("Select an amenity first.");
            return;
        }
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            statusLabel.setText("Please enter a name.");
            return;
        }
        amenityService.update(new Amenity(selectedAmenity.getId(), name));
        XmlUtil.logAction(SessionUtil.getLoggedInUser().getUsername(), "UPDATE_AMENITY: " + name);
        refresh();
        clearForm();
        statusLabel.setText("Amenity updated.");
    }

    @FXML
    private void handleDelete() {
        if (selectedAmenity == null) {
            statusLabel.setText("Select an amenity first.");
            return;
        }
        amenityService.delete(selectedAmenity.getId());
        XmlUtil.logAction(SessionUtil.getLoggedInUser().getUsername(), "DELETE_AMENITY: " + selectedAmenity.getName());
        refresh();
        clearForm();
        statusLabel.setText("Amenity deleted.");
    }

    private void refresh() {
        amenityTable.setItems(FXCollections.observableArrayList(amenityService.findAll()));
        selectedAmenity = null;
    }

    private void clearForm() {
        nameField.clear();
    }
}