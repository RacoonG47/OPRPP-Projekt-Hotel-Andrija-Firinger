package hr.algebra.hotel.controller;

import hr.algebra.hotel.dao.repo.HotelRepository;
import hr.algebra.hotel.model.Hotel;
import hr.algebra.hotel.service.HotelService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class ItineraryController {

    @FXML private ListView<Hotel> availableHotelsListView;
    @FXML private ListView<Hotel> itineraryListView;

    private final HotelService hotelService = new HotelService(new HotelRepository());
    private final ObservableList<Hotel> itinerary = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        availableHotelsListView.setItems(
                FXCollections.observableArrayList(hotelService.findAll())
        );
        itineraryListView.setItems(itinerary);

        setupDragSource();
        setupDropTarget();
    }

    private void setupDragSource() {
        availableHotelsListView.setOnDragDetected(event -> {
            Hotel selected = availableHotelsListView.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            Dragboard db = availableHotelsListView.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString(selected.getName());
            db.setContent(content);
            event.consume();
        });
    }

    private void setupDropTarget() {
        itineraryListView.setOnDragOver(event -> {
            if (event.getGestureSource() == availableHotelsListView
                    && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        itineraryListView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                Hotel selected = availableHotelsListView.getSelectionModel().getSelectedItem();
                if (selected != null && !itinerary.contains(selected)) {
                    itinerary.add(selected);
                }
                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });
    }

    @FXML
    private void handleRemove() {
        Hotel selected = itineraryListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            itinerary.remove(selected);
        }
    }

    @FXML
    private void handleClear() {
        itinerary.clear();
    }
}