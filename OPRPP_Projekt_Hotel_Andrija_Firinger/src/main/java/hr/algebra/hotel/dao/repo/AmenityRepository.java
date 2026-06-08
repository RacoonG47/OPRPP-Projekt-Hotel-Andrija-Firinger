package hr.algebra.hotel.dao.repo;

import hr.algebra.hotel.dao.IAmenityRepository;
import hr.algebra.hotel.model.Amenity;
import hr.algebra.hotel.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AmenityRepository implements IAmenityRepository {

    private Connection getConnection() {
        return DatabaseUtil.getInstance().getConnection();
    }

    @Override
    public void insert(Amenity amenity) {
        try (PreparedStatement ps = getConnection().prepareStatement("CALL insert_amenity(?)")) {
            ps.setString(1, amenity.getName());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert amenity", e);
        }
    }

    @Override
    public void update(Amenity amenity) {
        try (PreparedStatement ps = getConnection().prepareStatement("CALL update_amenity(?, ?)")) {
            ps.setInt(1, amenity.getId());
            ps.setString(2, amenity.getName());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update amenity", e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (PreparedStatement ps = getConnection().prepareStatement("CALL delete_amenity(?)")) {
            ps.setInt(1, id);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete amenity", e);
        }
    }

    @Override
    public List<Amenity> findAll() {
        List<Amenity> amenities = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM get_all_amenities()");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                amenities.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch amenities", e);
        }
        return amenities;
    }

    @Override
    public Optional<Amenity> findById(Integer id) {
        try (PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM get_amenity_by_id(?)")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch amenity by id", e);
        }
        return Optional.empty();
    }

    private Amenity mapRow(ResultSet rs) throws SQLException {
        return new Amenity(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}