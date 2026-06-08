package hr.algebra.hotel.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "hotel")
@XmlAccessorType(XmlAccessType.FIELD)
public class HotelXml {

    @XmlElement
    private String name;

    @XmlElement
    private String address;

    @XmlElement
    private int numRooms;

    @XmlElement
    private String pricePerNight;

    @XmlElement
    private String description;

    @XmlElement
    private int category;

    @XmlElement
    private String city;

    @XmlElementWrapper(name = "amenities")
    @XmlElement(name = "amenity")
    private List<String> amenities;

    @XmlElementWrapper(name = "reviews")
    @XmlElement(name = "review")
    private List<ReviewXml> reviews;

    public HotelXml() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getNumRooms() { return numRooms; }
    public void setNumRooms(int numRooms) { this.numRooms = numRooms; }

    public String getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(String pricePerNight) { this.pricePerNight = pricePerNight; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCategory() { return category; }
    public void setCategory(int category) { this.category = category; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }

    public List<ReviewXml> getReviews() { return reviews; }
    public void setReviews(List<ReviewXml> reviews) { this.reviews = reviews; }
}