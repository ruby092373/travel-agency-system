package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import dao.ProductDao;
import model.Product;
import util.DatabaseManager;

public class ProductDaoImpl implements ProductDao {

    @Override
    public void add(Product p) {
        String sql = "INSERT INTO product(name, price, description, region, duration_days) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setInt(2, p.getPrice());
            ps.setString(3, p.getDescription());
            ps.setString(4, p.getRegion());
            ps.setInt(5, p.getDurationDays());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Product p) {
        String sql = "UPDATE product SET name=?, price=?, description=?, region=?, duration_days=? WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setInt(2, p.getPrice());
            ps.setString(3, p.getDescription());
            ps.setString(4, p.getRegion());
            ps.setInt(5, p.getDurationDays());
            ps.setInt(6, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM product WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Product selectById(int id) {
        String sql = "SELECT * FROM product WHERE id = ?";
        Product p = null;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p = new Product();
                    p.setId(rs.getInt("id"));
                    p.setName(rs.getString("name"));
                    p.setPrice(rs.getInt("price"));
                    p.setDescription(rs.getString("description"));
                    p.setRegion(rs.getString("region"));
                    p.setDurationDays(rs.getInt("duration_days"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }

    @Override
    public List<Product> selectAll() {
        // 【修改】加入價格排序
        String sql = "SELECT * FROM product ORDER BY price ASC";
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setPrice(rs.getInt("price"));
                p.setDescription(rs.getString("description"));
                p.setRegion(rs.getString("region"));
                p.setDurationDays(rs.getInt("duration_days"));
                products.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public List<Product> search(String region, Integer durationDays) {
        StringBuilder sql = new StringBuilder("SELECT * FROM product WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (region != null && !region.isEmpty() && !"全部地區".equals(region)) {
            sql.append(" AND region = ?");
            params.add(region);
        }

        if (durationDays != null && durationDays > 0) {
            sql.append(" AND duration_days = ?");
            params.add(durationDays);
        }

        // 【修改】加入價格排序
        sql.append(" ORDER BY price ASC");

        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product();
                    p.setId(rs.getInt("id"));
                    p.setName(rs.getString("name"));
                    p.setPrice(rs.getInt("price"));
                    p.setDescription(rs.getString("description"));
                    p.setRegion(rs.getString("region"));
                    p.setDurationDays(rs.getInt("duration_days"));
                    products.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    @Override
    public List<Integer> getDistinctDurationDays() {
        String sql = "SELECT DISTINCT duration_days FROM product ORDER BY duration_days ASC";
        List<Integer> days = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                days.add(rs.getInt("duration_days"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return days;
    }
}
