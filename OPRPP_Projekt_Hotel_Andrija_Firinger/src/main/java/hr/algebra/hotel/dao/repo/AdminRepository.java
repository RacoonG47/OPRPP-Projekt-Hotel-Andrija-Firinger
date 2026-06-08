package hr.algebra.hotel.dao.repo;

import hr.algebra.hotel.dao.IAdminRepository;
import hr.algebra.hotel.util.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminRepository implements IAdminRepository {

    @Override
    public void deleteAllData() {
        try (PreparedStatement ps = DatabaseUtil.getInstance()
                .getConnection().prepareStatement("CALL delete_all_data()")) {
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete all data", e);
        }
    }
}