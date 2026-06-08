package hr.algebra.hotel.util;

import hr.algebra.hotel.dao.repo.AmenityRepository;
import hr.algebra.hotel.dao.repo.CityRepository;
import hr.algebra.hotel.dao.repo.HotelRepository;
import hr.algebra.hotel.model.Amenity;
import hr.algebra.hotel.model.Category;
import hr.algebra.hotel.model.City;
import hr.algebra.hotel.model.Hotel;
import hr.algebra.hotel.service.AmenityService;
import hr.algebra.hotel.service.CityService;
import hr.algebra.hotel.service.HotelService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public final class XmlImportUtil {

    private XmlImportUtil() {}

    public static void importFromXml(File file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            CityService cityService = new CityService(new CityRepository());
            AmenityService amenityService = new AmenityService(new AmenityRepository());
            HotelService hotelService = new HotelService(new HotelRepository());

            Map<String, City> cityMap = importCities(doc, cityService);
            Map<String, Amenity> amenityMap = importAmenities(doc, amenityService);
            importHotels(doc, hotelService, cityMap, amenityMap);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to import data from XML", e);
        }
    }

    private static Map<String, City> importCities(Document doc, CityService cityService) {
        Map<String, City> cityMap = new HashMap<>();

        Element citiesRoot = (Element) doc.getElementsByTagName("cities").item(0);
        NodeList cityNodes = citiesRoot.getElementsByTagName("city");

        for (int i = 0; i < cityNodes.getLength(); i++) {
            Element cityEl = (Element) cityNodes.item(i);
            String name = cityEl.getElementsByTagName("name").item(0).getTextContent().trim();
            String country = cityEl.getElementsByTagName("country").item(0).getTextContent().trim();

            City city = new City(null, name, country);
            cityService.insert(city);

            cityService.findByCondition(c -> c.getName().equals(name))
                    .stream()
                    .findFirst()
                    .ifPresent(saved -> cityMap.put(name, saved));
        }
        return cityMap;
    }

    private static Map<String, Amenity> importAmenities(Document doc, AmenityService amenityService) {
        Map<String, Amenity> amenityMap = new HashMap<>();

        Element amenitiesRoot = (Element) doc.getElementsByTagName("amenities").item(0);
        NodeList amenityNodes = amenitiesRoot.getElementsByTagName("amenity");

        for (int i = 0; i < amenityNodes.getLength(); i++) {
            String name = amenityNodes.item(i).getTextContent().trim();
            if (name.isEmpty()) continue;

            amenityService.insert(new Amenity(null, name));

            amenityService.findByCondition(a -> a.getName().equals(name))
                    .stream()
                    .findFirst()
                    .ifPresent(saved -> amenityMap.put(name, saved));
        }
        return amenityMap;
    }

    private static void importHotels(Document doc, HotelService hotelService,
                                     Map<String, City> cityMap, Map<String, Amenity> amenityMap) {
        Element hotelsRoot = (Element) doc.getElementsByTagName("hotels").item(0);
        NodeList hotelNodes = hotelsRoot.getElementsByTagName("hotel");

        for (int i = 0; i < hotelNodes.getLength(); i++) {
            Element hotelEl = (Element) hotelNodes.item(i);

            String name = hotelEl.getElementsByTagName("name").item(0).getTextContent().trim();
            String address = hotelEl.getElementsByTagName("address").item(0).getTextContent().trim();
            int numRooms = Integer.parseInt(hotelEl.getElementsByTagName("numRooms").item(0).getTextContent().trim());
            BigDecimal price = new BigDecimal(hotelEl.getElementsByTagName("pricePerNight").item(0).getTextContent().trim());
            String description = hotelEl.getElementsByTagName("description").item(0).getTextContent().trim();
            int stars = Integer.parseInt(hotelEl.getElementsByTagName("category").item(0).getTextContent().trim());
            String cityName = hotelEl.getElementsByTagName("city").item(0).getTextContent().trim();

            City city = cityMap.get(cityName);
            Category category = Category.fromStars(stars);

            if (city == null) continue;

            Hotel hotel = new Hotel(null, name, address, numRooms, price, description, null, city, category);
            Integer hotelId = hotelService.insertAndReturnId(hotel);
            hotel.setId(hotelId);

            Element hotelAmenitiesEl = (Element) hotelEl.getElementsByTagName("amenities").item(0);
            if (hotelAmenitiesEl != null) {
                NodeList hotelAmenityNodes = hotelAmenitiesEl.getElementsByTagName("amenity");
                for (int j = 0; j < hotelAmenityNodes.getLength(); j++) {
                    String amenityName = hotelAmenityNodes.item(j).getTextContent().trim();
                    Amenity amenity = amenityMap.get(amenityName);
                    if (amenity != null) {
                        hotelService.linkAmenity(hotelId, amenity.getId());
                    }
                }
            }
        }
    }
}