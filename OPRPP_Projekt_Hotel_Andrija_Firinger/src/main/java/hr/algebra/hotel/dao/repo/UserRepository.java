package hr.algebra.hotel.dao.repo;

import hr.algebra.hotel.dao.IUserRepository;
import hr.algebra.hotel.model.User;
import hr.algebra.hotel.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository implements IUserRepository {

    private Connection getConnection() {
        return DatabaseUtil.getInstance().getConnection();
    }

    @Override
    public void insert(User user) {
        try (PreparedStatement ps = getConnection().prepareStatement("CALL insert_user(?, ?, ?)")) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole().name());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert user", e);
        }
    }

    @Override
    public void update(User user) {
        try (PreparedStatement ps = getConnection().prepareStatement("CALL update_user(?, ?, ?, ?)")) {
            ps.setInt(1, user.getId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole().name());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (PreparedStatement ps = getConnection().prepareStatement("CALL delete_user(?)")) {
            ps.setInt(1, id);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM get_all_users()");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch users", e);
        }
        return users;
    }

    @Override
    public Optional<User> findById(Integer id) {
        try (PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM get_user_by_id(?)")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user by id", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try (PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM get_user_by_username(?)")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user by username", e);
        }
        return Optional.empty();
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                User.Role.valueOf(rs.getString("role"))
        );
    }
}