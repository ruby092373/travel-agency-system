package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import dao.OrderDao;
import model.Order;
import util.DatabaseManager;
import vo.ViewOrder;

public class OrderDaoImpl implements OrderDao {

    @Override
    public Order create(Order order) {
    	String sql = "INSERT INTO `order` (customer_id, total_price) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, order.getCustomerId());
            ps.setInt(2, order.getTotalPrice());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    order.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return order;
    }

    @Override
    public List<Order> findByCustomerId(int customerId) {
        String sql = "SELECT * FROM `order` WHERE customer_id = ? ORDER BY order_date DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = new Order();
                    o.setId(rs.getInt("id"));
                    o.setCustomerId(rs.getInt("customer_id"));
                    o.setTotalPrice(rs.getInt("total_price"));
                    o.setOrderDate(rs.getTimestamp("order_date"));
                    o.setStatus(rs.getString("status")); // 讀取 status
                    orders.add(o);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

	@Override
	public List<ViewOrder> findAllForView() {
        String sql = "SELECT o.id, c.name AS customer_name, o.total_price, o.order_date, o.status " +
                     "FROM `order` o " +
                     "LEFT JOIN customer c ON o.customer_id = c.id " +
                     "ORDER BY o.order_date DESC";
        List<ViewOrder> orders = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                ViewOrder vo = new ViewOrder();
                vo.setOrderId(rs.getInt("id"));
                vo.setCustomerName(rs.getString("customer_name"));
                vo.setTotalPrice(rs.getInt("total_price"));
                vo.setOrderDate(rs.getTimestamp("order_date"));
                vo.setStatus(rs.getString("status"));
                orders.add(vo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

	@Override
	public void updateStatus(int orderId, String status) {
        String sql = "UPDATE `order` SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	@Override
	public void delete(int orderId) {
        String sql = "DELETE FROM `order` WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate(); // 因為 ON DELETE CASCADE，order_item 會自動被刪除
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
