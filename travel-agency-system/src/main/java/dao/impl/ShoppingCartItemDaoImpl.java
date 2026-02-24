package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.ShoppingCartItemDao;
import model.Product;
import model.ShoppingCartItem;
import util.DatabaseManager;

public class ShoppingCartItemDaoImpl implements ShoppingCartItemDao {

    @Override
    public void add(ShoppingCartItem item) {
        // 【修改】SQL 語句，加入 departure_date
        String sql = "INSERT INTO shopping_cart_item (customer_id, product_id, quantity, departure_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, item.getCustomerId());
            ps.setInt(2, item.getProductId());
            ps.setInt(3, item.getQuantity());
            // 【修改】將 java.util.Date 轉為 java.sql.Date
            ps.setDate(4, new java.sql.Date(item.getDepartureDate().getTime()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateQuantity(int itemId, int newQuantity) {
        String sql = "UPDATE shopping_cart_item SET quantity = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, itemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public ShoppingCartItem findByCustomerIdAndProductId(int customerId, int productId) {
        // 為了簡化，此處暫不考慮出發日期。若需考慮，應將 departure_date 也加入查詢條件。
        String sql = "SELECT * FROM shopping_cart_item WHERE customer_id = ? AND product_id = ?";
        ShoppingCartItem item = null;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    item = new ShoppingCartItem();
                    item.setId(rs.getInt("id"));
                    item.setCustomerId(rs.getInt("customer_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setDepartureDate(rs.getDate("departure_date"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public List<ShoppingCartItem> findByCustomerId(int customerId) {
        String sql = "SELECT sci.*, p.name, p.price, p.description, p.region, p.duration_days " +
                     "FROM shopping_cart_item sci " +
                     "JOIN product p ON sci.product_id = p.id " +
                     "WHERE sci.customer_id = ?";
        List<ShoppingCartItem> items = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ShoppingCartItem item = new ShoppingCartItem();
                    item.setId(rs.getInt("id"));
                    item.setCustomerId(rs.getInt("customer_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    // 【修改】讀取出發日期
                    item.setDepartureDate(rs.getDate("departure_date"));

                    Product p = new Product();
                    p.setId(rs.getInt("product_id"));
                    p.setName(rs.getString("name"));
                    p.setPrice(rs.getInt("price"));
                    p.setDescription(rs.getString("description"));
                    p.setRegion(rs.getString("region"));
                    p.setDurationDays(rs.getInt("duration_days"));
                    
                    item.setProduct(p);
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    @Override
    public void delete(int itemId) {
        String sql = "DELETE FROM shopping_cart_item WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearByCustomerId(int customerId) {
        String sql = "DELETE FROM shopping_cart_item WHERE customer_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
