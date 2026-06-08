package hr.algebra.hotel.util;

import hr.algebra.hotel.model.Hotel;
import hr.algebra.hotel.model.Review;
import hr.algebra.hotel.xml.HotelXml;
import hr.algebra.hotel.xml.HotelsXml;
import hr.algebra.hotel.xml.ReviewXml;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class XmlUtil {

    private static final String LOG_FILE = "log.xml";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private XmlUtil() {}

    public static void exportHotel(Hotel hotel, List<Review> reviews, File destination) {
        try {
            HotelXml hotelXml = mapToHotelXml(hotel, reviews);
            JAXBContext context = JAXBContext.newInstance(HotelXml.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(hotelXml, destination);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to export hotel to XML", e);
        }
    }

    public static void exportHotelsByCity(List<Hotel> hotels, Map<Integer, List<Review>> reviewsMap, File destination) {
        try {
            List<HotelXml> hotelXmls = hotels.stream()
                    .map(h -> mapToHotelXml(h, reviewsMap.getOrDefault(h.getId(), List.of())))
                    .collect(Collectors.toList());

            HotelsXml hotelsXml = new HotelsXml();
            hotelsXml.setHotels(hotelXmls);

            JAXBContext context = JAXBContext.newInstance(HotelsXml.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(hotelsXml, destination);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to export hotels by city to XML", e);
        }
    }

    public static void logAction(String username, String action) {
        try {
            File logFile = new File(LOG_FILE);
            Document doc;
            Element root;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            if (logFile.exists()) {
                doc = builder.parse(logFile);
                root = doc.getDocumentElement();
            } else {
                doc = builder.newDocument();
                root = doc.createElement("log");
                doc.appendChild(root);
            }

            Element entry = doc.createElement("entry");

            Element time = doc.createElement("time");
            time.setTextContent(LocalDateTime.now().format(FORMATTER));
            entry.appendChild(time);

            Element user = doc.createElement("user");
            user.setTextContent(username);
            entry.appendChild(user);

            Element actionEl = doc.createElement("action");
            actionEl.setTextContent(action);
            entry.appendChild(actionEl);

            root.appendChild(entry);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(logFile));

        } catch (ParserConfigurationException | TransformerException e) {
            throw new RuntimeException("Failed to write log", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write log", e);
        }
    }

    public static void backupHotels(List<Hotel> hotels, Map<Integer, List<Review>> reviewsMap, File destination) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("hotels-backup");
            doc.appendChild(root);

            for (Hotel hotel : hotels) {
                List<Review> reviews = reviewsMap.getOrDefault(hotel.getId(), List.of());
                HotelXml hotelXml = mapToHotelXml(hotel, reviews);

                JAXBContext context = JAXBContext.newInstance(HotelXml.class);
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.marshal(hotelXml, new File(destination.getParent(), hotel.getName() + "_backup.xml"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to backup hotels", e);
        }
    }

    private static HotelXml mapToHotelXml(Hotel hotel, List<Review> reviews) {
        HotelXml xml = new HotelXml();
        xml.setName(hotel.getName());
        xml.setAddress(hotel.getAddress());
        xml.setNumRooms(hotel.getNumRooms());
        xml.setPricePerNight(hotel.getPricePerNight().toString());
        xml.setDescription(hotel.getDescription());
        xml.setCategory(hotel.getCategory().getStars());
        xml.setCity(hotel.getCity() != null ? hotel.getCity().getName() : "");

        xml.setAmenities(hotel.getAmenities().stream()
                .map(a -> a.getName())
                .collect(Collectors.toList()));

        xml.setReviews(reviews.stream()
                .map(r -> {
                    ReviewXml rx = new ReviewXml();
                    rx.setUsername(r.getUser().getUsername());
                    rx.setRating(r.getRating());
                    rx.setComment(r.getComment());
                    rx.setCreatedAt(r.getCreatedAt() != null ? r.getCreatedAt().format(FORMATTER) : "");
                    return rx;
                })
                .collect(Collectors.toList()));

        return xml;
    }
}