package hr.algebra.hotel.dao.repo;

import hr.algebra.hotel.dao.IHotelRepository;
import hr.algebra.hotel.model.Amenity;
import hr.algebra.hotel.model.Category;
import hr.algebra.hotel.model.City;
import hr.algebra.hotel.model.Hotel;
import hr.algebra.hotel.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HotelRepository implements IHotelRepository {

    private Connection getConnection() {
        return DatabaseUtil.getInstance().getConnection();
    }

    @Override
    public void insert(Hotel hotel) {
        try (PreparedStatement ps = getConnection().prepareStatement("CALL insert_hotel(?, ?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, hotel.getName());
            ps.setString(2, hotel.getAddress());
            ps.setInt(3, hotel.getNumRooms());
            ps.setBigDecimal(4, hotel.getPricePerNight());
            ps.setString(5, hotel.getDescription());
            ps.setString(6, hotel.getImagePath());
            ps.setInt(7, hotel.getCategory().getStars());
            ps.setInt(8, hotel.getCity().getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert hotel", e);
        }
    }

    @Override
    public Integer insertAndReturnId(Hotel hotel) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT insert_hotel_returning_id(?, ?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, hotel.getName());
            ps.setString(2, hotel.getAddress());
            ps.setInt(3, hotel.getNumRooms());
            ps.setBigDecimal(4, hotel.getPricePerNight());
            ps.setString(5, hotel.getDescription());
            ps.setString(6, hotel.getImagePath());
            ps.setInt(7, hotel.getCategory().getStars());
            ps.setInt(8, hotel.getCity().getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert hotel and return id", e);
        }
        throw new RuntimeException("No id returned from insert_hotel_returning_id");
    }

    @Override
    public void update(Hotel hotel) {
        try (PreparedStatement ps = getConnection().prepareStatement("CALL update_hotel(?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, hotel.getId());
            ps.setString(2, hotel.getName());
            ps.setString(3, hotel.getAddress());
            ps.setInt(4, hotel.getNumRooms());
            ps.setBigDecimal(5, hotel.getPricePerNight());
            ps.setString(6, hotel.getDescription());
            ps.setString(7, hotel.getImagePath());
            ps.setInt(8, hotel.getCategory().getStars());
            ps.setInt(9, hotel.getCity().getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update hotel", e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (PreparedStatement ps = getConnection().prepareStatement("CALL delete_hotel(?)")) {
            ps.setInt(1, id);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete hotel", e);
        }
    }

    @Override
    public List<Hotel> findAll() {
        List<Hotel> hotels = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM get_all_hotels()");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hotels.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch hotels", e);
        }
        return hotels;
    }

    @Override
    public Optional<Hotel> findById(Integer id) {
        try (PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM get_hotel_by_id(?)")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch hotel by id", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Hotel> findByCity(Integer cityId) {
        List<Hotel> hotels = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM get_hotels_by_city(?)")) {
            ps.setInt(1, cityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hotels.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch hotels by city", e);
        }
        return hotels;
    }

    @Override
    public List<Hotel> findByCategory(Category category) {
        List<Hotel> hotels = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM get_hotels_by_category(?)")) {
            ps.setInt(1, category.getStars());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hotels.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch hotels by category", e);
        }
        return hotels;
    }

    @Override
    public List<Hotel> findByAmenity(Integer amenityId) {
        List<Hotel> hotels = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM get_hotels_by_amenity(?)")) {
            ps.setInt(1, amenityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hotels.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch hotels by amenity", e);
        }
        return hotels;
    }

    @Override
    public void linkAmenity(Integer hotelId, Integer amenityId) {
        try (PreparedStatement ps = getConnection().prepareStatement("CALL link_hotel_amenity(?, ?)")) {
            ps.setInt(1, hotelId);
            ps.setInt(2, amenityId);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to link amenity to hotel", e);
        }
    }

    @Override
    public void unlinkAmenity(Integer hotelId, Integer amenityId) {
        try (PreparedStatement ps = getConnection().prepareStatement("CALL unlink_hotel_amenity(?, ?)")) {
            ps.setInt(1, hotelId);
            ps.setInt(2, amenityId);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to unlink amenity from hotel", e);
        }
    }

    @Override
    public List<Amenity> findAmenities(Integer hotelId) {
        List<Amenity> amenities = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM get_amenities_by_hotel(?)")) {
            ps.setInt(1, hotelId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    amenities.add(new Amenity(rs.getInt("id"), rs.getString("name")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch amenities for hotel", e);
        }
        return amenities;
    }

    private Hotel mapRow(ResultSet rs) throws SQLException {
        City city = new City(
                rs.getInt("city_id"),
                rs.getString("city_name"),
                rs.getString("city_country")
        );
        Category category = Category.fromStars(rs.getInt("category"));
        return new Hotel(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getInt("num_rooms"),
                rs.getBigDecimal("price_per_night"),
                rs.getString("description"),
                rs.getString("image_path"),
                city,
                category
        );
    }
}