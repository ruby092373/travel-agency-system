package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.OrderItemDao;
import model.OrderItem;
import util.DatabaseManager;

public class OrderItemDaoImpl implements OrderItemDao {

    @Override
    public void addBatch(List<OrderItem> items) {
        // 【修改】SQL 語句，加入 departure_date
        String sql = "INSERT INTO order_item (order_id, product_id, quantity, unit_price, departure_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            for (OrderItem item : items) {
                ps.setInt(1, item.getOrderId());
                ps.setInt(2, item.getProductId());
                ps.setInt(3, item.getQuantity());
                ps.setInt(4, item.getUnitPrice());
                // 【修改】將 java.util.Date 轉為 java.sql.Date
                ps.setDate(5, new java.sql.Date(item.getDepartureDate().getTime()));
                ps.addBatch();
            }
            ps.executeBatch();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<OrderItem> findByOrderId(int orderId) {
        String sql = "SELECT oi.*, p.name AS product_name " +
                     "FROM order_item oi " +
                     "JOIN product p ON oi.product_id = p.id " +
                     "WHERE oi.order_id = ?";
        List<OrderItem> items = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getInt("id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getInt("unit_price"));
                    // 【修改】讀取出發日期
                    item.setDepartureDate(rs.getDate("departure_date"));
                    item.setProductName(rs.getString("product_name"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
}
