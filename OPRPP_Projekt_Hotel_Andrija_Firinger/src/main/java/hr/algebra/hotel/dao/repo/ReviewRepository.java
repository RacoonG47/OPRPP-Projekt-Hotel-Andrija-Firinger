package hr.algebra.hotel.dao.repo;

import hr.algebra.hotel.dao.IReviewRepository;
import hr.algebra.hotel.model.Hotel;
import hr.algebra.hotel.model.Review;
import hr.algebra.hotel.model.User;
import hr.algebra.hotel.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReviewRepository implements IReviewRepository {

    private Connection getConnection() {
        return DatabaseUtil.getInstance().getConnection();
    }

    @Override
    public void insert(Review review) {
        try (PreparedStatement ps = getConnection().prepareStatement("CALL insert_review(?, ?, ?, ?)")) {
            ps.setInt(1, review.getHotel().getId());
            ps.setInt(2, review.getUser().getId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert review", e);
        }
    }

    @Override
    public void update(Review review) {
        try (PreparedStatement ps = getConnection().prepareStatement("CALL update_review(?, ?, ?)")) {
            ps.setInt(1, review.getId());
            ps.setInt(2, review.getRating());
            ps.setString(3, review.getComment());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update review", e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (PreparedStatement ps = getConnection().prepareStatement("CALL delete_review(?)")) {
            ps.setInt(1, id);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete review", e);
        }
    }

    @Override
    public List<Review> findAll() {
        throw new UnsupportedOperationException("Use findByHotel instead");
    }

    @Override
    public Optional<Review> findById(Integer id) {
        try (PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM get_review_by_id(?)")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch review by id", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Review> findByHotel(Integer hotelId) {
        List<Review> reviews = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM get_reviews_by_hotel(?)")) {
            ps.setInt(1, hotelId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch reviews by hotel", e);
        }
        return reviews;
    }

    private Review mapRow(ResultSet rs) throws SQLException {
        Hotel hotel = new Hotel();
        hotel.setId(rs.getInt("hotel_id"));

        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));

        Timestamp ts = rs.getTimestamp("created_at");

        return new Review(
                rs.getInt("id"),
                hotel,
                user,
                rs.getInt("rating"),
                rs.getString("comment"),
                ts != null ? ts.toLocalDateTime() : null
        );
    }
}