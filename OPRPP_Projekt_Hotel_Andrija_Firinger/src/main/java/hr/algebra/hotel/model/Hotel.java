package hr.algebra.hotel.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Hotel extends NamedEntity {

    private String address;
    private int numRooms;
    private BigDecimal pricePerNight;
    private String description;
    private String imagePath;
    private City city;
    private Category category;
    private List<Amenity> amenities;

    public Hotel() {
        this.amenities = new ArrayList<>();
    }

    public Hotel(Integer id, String name, String address, int numRooms,
                 BigDecimal pricePerNight, String description, String imagePath,
                 City city, Category category) {
        super(id, name);
        this.address = address;
        this.numRooms = numRooms;
        this.pricePerNight = pricePerNight;
        this.description = description;
        this.imagePath = imagePath;
        this.city = city;
        this.category = category;
        this.amenities = new ArrayList<>();
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getNumRooms() { return numRooms; }
    public void setNumRooms(int numRooms) { this.numRooms = numRooms; }

    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public List<Amenity> getAmenities() { return amenities; }
    public void setAmenities(List<Amenity> amenities) { this.amenities = amenities; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hotel other)) return false;
        return super.equals(o) && Objects.equals(address, other.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), address);
    }

    @Override
    public String toString() {
        return getName() + " - " + (city != null ? city.getName() : "N/A")
                + " (" + (category != null ? category.getStars() : "?") + "★)";
    }
}