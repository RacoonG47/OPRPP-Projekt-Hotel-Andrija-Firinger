package hr.algebra.hotel.dao.repo;

import hr.algebra.hotel.dao.ICityRepository;
import hr.algebra.hotel.model.City;
import hr.algebra.hotel.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CityRepository implements ICityRepository {

    private Connection getConnection() {
        return DatabaseUtil.getInstance().getConnection();
    }

    @Override
    public void insert(City city) {
        try (PreparedStatement ps = getConnection().prepareStatement("CALL insert_city(?, ?)")) {
            ps.setString(1, city.getName());
            ps.setString(2, city.getCountry());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert city", e);
        }
    }

    @Override
    public void update(City city) {
        try (PreparedStatement ps = getConnection().prepareStatement("CALL update_city(?, ?, ?)")) {
            ps.setInt(1, city.getId());
            ps.setString(2, city.getName());
            ps.setString(3, city.getCountry());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update city", e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (PreparedStatement ps = getConnection().prepareStatement("CALL delete_city(?)")) {
            ps.setInt(1, id);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete city", e);
        }
    }

    @Override
    public List<City> findAll() {
        List<City> cities = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM get_all_cities()");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cities.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch cities", e);
        }
        return cities;
    }

    @Override
    public Optional<City> findById(Integer id) {
        try (PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM get_city_by_id(?)")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch city by id", e);
        }
        return Optional.empty();
    }

    private City mapRow(ResultSet rs) throws SQLException {
        return new City(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("country")
        );
    }
}