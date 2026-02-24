package dao;

import java.util.List;

import model.OrderItem;

public interface OrderItemDao {
	
	// 批次新增訂單項目
    void addBatch(List<OrderItem> items);
    
    List<OrderItem> findByOrderId(int orderId);

}
