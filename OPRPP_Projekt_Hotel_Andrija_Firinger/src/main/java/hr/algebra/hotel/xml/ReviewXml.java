package hr.algebra.hotel.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "review")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReviewXml {

    @XmlElement
    private String username;

    @XmlElement
    private int rating;

    @XmlElement
    private String comment;

    @XmlElement
    private String createdAt;

    public ReviewXml() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}