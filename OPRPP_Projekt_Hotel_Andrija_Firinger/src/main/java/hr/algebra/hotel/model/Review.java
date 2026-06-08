package hr.algebra.hotel.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Review extends Entity implements Comparable<Review> {

    private Hotel hotel;
    private User user;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public Review() {}

    public Review(Integer id, Hotel hotel, User user, int rating,
                  String comment, LocalDateTime createdAt) {
        super(id);
        this.hotel = hotel;
        this.user = user;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public int compareTo(Review other) {
        // newest first
        return other.createdAt.compareTo(this.createdAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review other)) return false;
        return super.equals(o) && Objects.equals(createdAt, other.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), createdAt);
    }

    @Override
    public String toString() {
        return (user != null ? user.getUsername() : "Unknown")
                + " - " + rating + "★ - "
                + (createdAt != null ? createdAt.toLocalDate() : "");
    }
}