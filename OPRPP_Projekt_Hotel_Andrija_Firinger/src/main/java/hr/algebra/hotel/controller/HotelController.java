package hr.algebra.hotel.controller;

import hr.algebra.hotel.dao.repo.AmenityRepository;
import hr.algebra.hotel.dao.repo.CityRepository;
import hr.algebra.hotel.dao.repo.HotelRepository;
import hr.algebra.hotel.dao.repo.ReviewRepository;
import hr.algebra.hotel.model.Amenity;
import hr.algebra.hotel.model.Category;
import hr.algebra.hotel.model.City;
import hr.algebra.hotel.model.Hotel;
import hr.algebra.hotel.model.Review;
import hr.algebra.hotel.service.AmenityService;
import hr.algebra.hotel.service.CityService;
import hr.algebra.hotel.service.HotelService;
import hr.algebra.hotel.service.ReviewService;
import hr.algebra.hotel.util.FileUtil;
import hr.algebra.hotel.util.SessionUtil;
import hr.algebra.hotel.util.XmlUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HotelController {

    @FXML private TableView<Hotel> hotelTable;
    @FXML private TableColumn<Hotel, String> nameColumn;
    @FXML private TableColumn<Hotel, String> cityColumn;
    @FXML private TableColumn<Hotel, String> categoryColumn;
    @FXML private TableColumn<Hotel, BigDecimal> priceColumn;
    @FXML private TableColumn<Hotel, Integer> roomsColumn;

    @FXML private TextField searchField;
    @FXML private ComboBox<City> cityFilter;
    @FXML private ComboBox<Category> categoryFilter;
    @FXML private ComboBox<Amenity> amenityFilter;

    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField roomsField;
    @FXML private TextField priceField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<City> cityCombo;
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private TextField imagePathField;
    @FXML private ImageView hotelImageView;
    @FXML private ListView<Amenity> amenityListView;
    @FXML private Label statusLabel;

    private final HotelService hotelService = new HotelService(new HotelRepository());
    private final CityService cityService = new CityService(new CityRepository());
    private final AmenityService amenityService = new AmenityService(new AmenityRepository());
    private final ReviewService reviewService = new ReviewService(new ReviewRepository());

    private Hotel selectedHotel;

    @FXML
    public void initialize() {
        setupColumns();
        loadFilters();
        loadTable(hotelService.findAll());
        amenityListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        amenityListView.setItems(FXCollections.observableArrayList(amenityService.findAll()));
    }

    private void setupColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));
        roomsColumn.setCellValueFactory(new PropertyValueFactory<>("numRooms"));

        cityColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCity() != null ? data.getValue().getCity().getName() : ""
                ));

        categoryColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCategory() != null ? data.getValue().getCategory().toString() : ""
                ));
    }

    private void loadFilters() {
        List<City> cities = cityService.findAll();
        cityFilter.setItems(FXCollections.observableArrayList(cities));
        cityCombo.setItems(FXCollections.observableArrayList(cities));
        categoryFilter.setItems(FXCollections.observableArrayList(Category.values()));
        categoryCombo.setItems(FXCollections.observableArrayList(Category.values()));
        amenityFilter.setItems(FXCollections.observableArrayList(amenityService.findAll()));
    }

    private void loadTable(List<Hotel> hotels) {
        hotelTable.setItems(FXCollections.observableArrayList(hotels));
    }

    @FXML
    private void handleTableClick() {
        selectedHotel = hotelTable.getSelectionModel().getSelectedItem();
        if (selectedHotel == null) return;

        nameField.setText(selectedHotel.getName());
        addressField.setText(selectedHotel.getAddress());
        roomsField.setText(String.valueOf(selectedHotel.getNumRooms()));
        priceField.setText(selectedHotel.getPricePerNight().toString());
        descriptionField.setText(selectedHotel.getDescription());
        imagePathField.setText(selectedHotel.getImagePath());
        cityCombo.setValue(selectedHotel.getCity());
        categoryCombo.setValue(selectedHotel.getCategory());

        if (selectedHotel.getImagePath() != null && !selectedHotel.getImagePath().isEmpty()) {
            File imgFile = new File(selectedHotel.getImagePath());
            if (imgFile.exists()) {
                hotelImageView.setImage(new Image(imgFile.toURI().toString()));
            } else {
                hotelImageView.setImage(null);
            }
        } else {
            hotelImageView.setImage(null);
        }

        List<Amenity> hotelAmenities = hotelService.findAmenities(selectedHotel.getId());
        amenityListView.getSelectionModel().clearSelection();
        amenityListView.getItems().forEach(a -> {
            if (hotelAmenities.contains(a)) {
                amenityListView.getSelectionModel().select(a);
            }
        });
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim();
        City city = cityFilter.getValue();
        Category category = categoryFilter.getValue();
        Amenity amenity = amenityFilter.getValue();

        List<Hotel> hotels;

        if (city != null) {
            hotels = hotelService.findByCity(city.getId());
        } else if (category != null) {
            hotels = hotelService.findByCategory(category);
        } else if (amenity != null) {
            hotels = hotelService.findByAmenity(amenity.getId());
        } else {
            hotels = hotelService.findAll();
        }

        if (!keyword.isEmpty()) {
            hotels = hotelService.findByCondition(hotels, h -> h.getName().toLowerCase().contains(keyword.toLowerCase()));
        }

        loadTable(hotels);
    }

    @FXML
    private void handleRefresh() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/algebra/hotel/hotel.fxml"));
            BorderPane root = (BorderPane) nameField.getScene().getRoot();
            root.setCenter(loader.load());
        } catch (IOException e) {
            throw new RuntimeException("Failed to refresh", e);
        }
    }

    @FXML
    private void handleClear() {
        searchField.clear();
        cityFilter.setValue(null);
        categoryFilter.setValue(null);
        amenityFilter.setValue(null);
        loadTable(hotelService.findAll());
    }

    @FXML
    private void handleAdd() {
        Hotel hotel = buildHotelFromForm(null);
        if (hotel == null) return;
        Integer newId = hotelService.insertAndReturnId(hotel);
        hotel.setId(newId);
        linkSelectedAmenities(hotel);
        XmlUtil.logAction(SessionUtil.getLoggedInUser().getUsername(), "ADD_HOTEL: " + hotel.getName());
        refresh();
        statusLabel.setText("Hotel added.");
    }

    @FXML
    private void handleUpdate() {
        if (selectedHotel == null) {
            statusLabel.setText("Select a hotel first.");
            return;
        }
        Hotel hotel = buildHotelFromForm(selectedHotel.getId());
        if (hotel == null) return;

        if (selectedHotel.getImagePath() != null && !selectedHotel.getImagePath().equals(hotel.getImagePath())) {
            FileUtil.deleteImage(selectedHotel.getImagePath());
        }

        hotelService.update(hotel);
        relinkAmenities(hotel);
        XmlUtil.logAction(SessionUtil.getLoggedInUser().getUsername(), "UPDATE_HOTEL: " + hotel.getName());
        refresh();
        statusLabel.setText("Hotel updated.");
    }

    @FXML
    private void handleDelete() {
        if (selectedHotel == null) {
            statusLabel.setText("Select a hotel first.");
            return;
        }
        FileUtil.deleteImage(selectedHotel.getImagePath());
        hotelService.delete(selectedHotel.getId());
        XmlUtil.logAction(SessionUtil.getLoggedInUser().getUsername(), "DELETE_HOTEL: " + selectedHotel.getName());
        refresh();
        clearForm();
        statusLabel.setText("Hotel deleted.");
    }

    @FXML
    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(nameField.getScene().getWindow());
        if (file != null) {
            String path = FileUtil.copyImageToAssets(file);
            imagePathField.setText(path);
            hotelImageView.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void handleExportXml() {
        if (selectedHotel == null) {
            statusLabel.setText("Select a hotel first.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML files", "*.xml")
        );
        fileChooser.setInitialFileName(selectedHotel.getName() + ".xml");
        File file = fileChooser.showSaveDialog(nameField.getScene().getWindow());
        if (file != null) {
            List<Review> reviews = reviewService.findByHotel(selectedHotel.getId());
            selectedHotel.setAmenities(hotelService.findAmenities(selectedHotel.getId()));
            XmlUtil.exportHotel(selectedHotel, reviews, file);
            XmlUtil.logAction(SessionUtil.getLoggedInUser().getUsername(), "EXPORT_HOTEL: " + selectedHotel.getName());
            statusLabel.setText("Hotel exported to XML.");
        }
    }

    @FXML
    private void handleExportByCity() {
        City city = cityFilter.getValue();
        if (city == null) {
            statusLabel.setText("Select a city filter first.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML files", "*.xml")
        );
        fileChooser.setInitialFileName(city.getName() + "_hotels.xml");
        File file = fileChooser.showSaveDialog(nameField.getScene().getWindow());
        if (file != null) {
            List<Hotel> hotels = hotelService.findByCity(city.getId());
            hotels.forEach(h -> h.setAmenities(hotelService.findAmenities(h.getId())));
            Map<Integer, List<Review>> reviewsMap = new HashMap<>();
            hotels.forEach(h -> reviewsMap.put(h.getId(), reviewService.findByHotel(h.getId())));
            XmlUtil.exportHotelsByCity(hotels, reviewsMap, file);
            XmlUtil.logAction(SessionUtil.getLoggedInUser().getUsername(), "EXPORT_CITY: " + city.getName());
            statusLabel.setText("Hotels exported to XML.");
        }
    }

    @FXML
    private void handleViewReviews() {
        if (selectedHotel == null) {
            statusLabel.setText("Select a hotel first.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/algebra/hotel/review.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            ReviewController controller = loader.getController();
            controller.setHotel(selectedHotel);
            stage.setTitle("Reviews - " + selectedHotel.getName());
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load review screen", e);
        }
    }

    private Hotel buildHotelFromForm(Integer id) {
        try {
            String name = nameField.getText() == null ? "" : nameField.getText().trim();
            String address = addressField.getText() == null ? "" : addressField.getText().trim();
            String roomsText = roomsField.getText() == null ? "" : roomsField.getText().trim();
            String priceText = priceField.getText() == null ? "" : priceField.getText().trim();
            String description = descriptionField.getText() == null ? "" : descriptionField.getText().trim();
            String imagePath = imagePathField.getText() == null ? "" : imagePathField.getText().trim();

            if (name.isEmpty() || address.isEmpty() || roomsText.isEmpty() ||
                    priceText.isEmpty() || cityCombo.getValue() == null || categoryCombo.getValue() == null) {
                statusLabel.setText("Please fill in all required fields.");
                return null;
            }

            int rooms = Integer.parseInt(roomsText);
            BigDecimal price = new BigDecimal(priceText);
            City city = cityCombo.getValue();
            Category category = categoryCombo.getValue();

            return new Hotel(id, name, address, rooms, price, description, imagePath, city, category);
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid number format in rooms or price.");
            return null;
        }
    }

    private void linkSelectedAmenities(Hotel hotel) {
        amenityListView.getSelectionModel().getSelectedItems()
                .forEach(a -> hotelService.linkAmenity(hotel.getId(), a.getId()));
    }

    private void relinkAmenities(Hotel hotel) {
        List<Amenity> current = hotelService.findAmenities(hotel.getId());
        current.forEach(a -> hotelService.unlinkAmenity(hotel.getId(), a.getId()));
        linkSelectedAmenities(hotel);
    }

    private void refresh() {
        loadTable(hotelService.findAll());
        selectedHotel = null;
    }

    private void clearForm() {
        nameField.clear();
        addressField.clear();
        roomsField.clear();
        priceField.clear();
        descriptionField.clear();
        imagePathField.clear();
        cityCombo.setValue(null);
        categoryCombo.setValue(null);
        amenityListView.getSelectionModel().clearSelection();
        hotelImageView.setImage(null);
    }
}