package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dao.EmployeeDao;
import model.Employee;
import util.DatabaseManager;

public class EmployeeDaoImpl implements EmployeeDao {

    @Override
    public Employee selectByUsername(String username) {
        String sql = "SELECT * FROM employee WHERE username = ?";
        Employee e = null;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    e = new Employee();
                    e.setId(rs.getInt("id"));
                    e.setName(rs.getString("name"));
                    e.setUsername(rs.getString("username"));
                    e.setPassword(rs.getString("password"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return e;
    }
}
