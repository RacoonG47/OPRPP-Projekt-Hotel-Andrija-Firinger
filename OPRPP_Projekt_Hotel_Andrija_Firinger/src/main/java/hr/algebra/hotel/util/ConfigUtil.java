package hr.algebra.hotel.util;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.InputStream;

public final class ConfigUtil {

    private static ConfigUtil instance;

    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private double screenWidth;
    private double screenHeight;

    private ConfigUtil() {
        try {
            InputStream is = getClass().getResourceAsStream("/hr/algebra/hotel/config.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            Element db = (Element) doc.getElementsByTagName("database").item(0);
            dbUrl      = db.getElementsByTagName("url").item(0).getTextContent();
            dbUsername = db.getElementsByTagName("username").item(0).getTextContent();
            dbPassword = db.getElementsByTagName("password").item(0).getTextContent();

            Element screen = (Element) doc.getElementsByTagName("screen").item(0);
            screenWidth  = Double.parseDouble(screen.getElementsByTagName("width").item(0).getTextContent());
            screenHeight = Double.parseDouble(screen.getElementsByTagName("height").item(0).getTextContent());

        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.xml", e);
        }
    }

    public static ConfigUtil getInstance() {
        if (instance == null) {
            instance = new ConfigUtil();
        }
        return instance;
    }

    public String getDbUrl()      { return dbUrl; }
    public String getDbUsername() { return dbUsername; }
    public String getDbPassword() { return dbPassword; }
    public double getScreenWidth()  { return screenWidth; }
    public double getScreenHeight() { return screenHeight; }
}