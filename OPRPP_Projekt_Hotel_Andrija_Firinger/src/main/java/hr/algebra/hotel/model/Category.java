package hr.algebra.hotel.model;

public enum Category {

    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    private final int stars;

    Category(int stars) {
        this.stars = stars;
    }

    public int getStars() {
        return stars;
    }

    public static Category fromStars(int stars) {
        for (Category c : values()) {
            if (c.stars == stars) return c;
        }
        throw new IllegalArgumentException("Invalid star rating: " + stars);
    }

    @Override
    public String toString() {
        return stars + "★";
    }
}