package hr.algebra.hotel.controller;

import hr.algebra.hotel.dao.repo.ReviewRepository;
import hr.algebra.hotel.model.Hotel;
import hr.algebra.hotel.model.Review;
import hr.algebra.hotel.model.User;
import hr.algebra.hotel.service.ReviewService;
import hr.algebra.hotel.util.SessionUtil;
import hr.algebra.hotel.util.XmlUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

public class ReviewController {

    @FXML private Label hotelNameLabel;
    @FXML private TableView<Review> reviewTable;
    @FXML private TableColumn<Review, String> usernameColumn;
    @FXML private TableColumn<Review, Integer> ratingColumn;
    @FXML private TableColumn<Review, String> commentColumn;
    @FXML private TableColumn<Review, String> dateColumn;
    @FXML private ComboBox<Integer> ratingCombo;
    @FXML private TextArea commentField;
    @FXML private Label statusLabel;

    private final ReviewService reviewService = new ReviewService(new ReviewRepository());
    private Hotel hotel;
    private Review selectedReview;

    @FXML
    public void initialize() {
        setupColumns();
        ratingCombo.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
        hotelNameLabel.setText("Reviews - " + hotel.getName());
        refresh();
    }

    private void setupColumns() {
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));

        usernameColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getUser() != null ? data.getValue().getUser().getUsername() : ""
                ));

        dateColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCreatedAt() != null ? data.getValue().getCreatedAt().toLocalDate().toString() : ""
                ));
    }

    @FXML
    private void handleTableClick() {
        selectedReview = reviewTable.getSelectionModel().getSelectedItem();
        if (selectedReview == null) return;
        ratingCombo.setValue(selectedReview.getRating());
        commentField.setText(selectedReview.getComment());
    }

    @FXML
    private void handleAdd() {
        if (hotel == null) return;
        Integer rating = ratingCombo.getValue();
        String comment = commentField.getText().trim();
        if (rating == null) {
            statusLabel.setText("Please select a rating.");
            return;
        }
        User loggedIn = SessionUtil.getLoggedInUser();
        Review review = new Review(null, hotel, loggedIn, rating, comment, null);
        reviewService.insert(review);
        XmlUtil.logAction(loggedIn.getUsername(), "ADD_REVIEW for hotel: " + hotel.getName());
        refresh();
        clearForm();
        statusLabel.setText("Review added.");
    }

    @FXML
    private void handleUpdate() {
        if (selectedReview == null) {
            statusLabel.setText("Select a review first.");
            return;
        }
        Integer rating = ratingCombo.getValue();
        String comment = commentField.getText().trim();
        if (rating == null) {
            statusLabel.setText("Please select a rating.");
            return;
        }
        selectedReview.setRating(rating);
        selectedReview.setComment(comment);
        reviewService.update(selectedReview);
        XmlUtil.logAction(SessionUtil.getLoggedInUser().getUsername(), "UPDATE_REVIEW: " + selectedReview.getId());
        refresh();
        clearForm();
        statusLabel.setText("Review updated.");
    }

    @FXML
    private void handleDelete() {
        if (selectedReview == null) {
            statusLabel.setText("Select a review first.");
            return;
        }
        reviewService.delete(selectedReview.getId());
        XmlUtil.logAction(SessionUtil.getLoggedInUser().getUsername(), "DELETE_REVIEW: " + selectedReview.getId());
        refresh();
        clearForm();
        statusLabel.setText("Review deleted.");
    }

    private void refresh() {
        if (hotel == null) return;
        reviewTable.setItems(FXCollections.observableArrayList(
                reviewService.findByHotel(hotel.getId())
        ));
        selectedReview = null;
    }

    private void clearForm() {
        ratingCombo.setValue(null);
        commentField.clear();
    }
}