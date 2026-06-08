package hr.algebra.hotel.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "hotels")
@XmlAccessorType(XmlAccessType.FIELD)
public class HotelsXml {

    @XmlElement(name = "hotel")
    private List<HotelXml> hotels;

    public HotelsXml() {}

    public List<HotelXml> getHotels() { return hotels; }
    public void setHotels(List<HotelXml> hotels) { this.hotels = hotels; }
}