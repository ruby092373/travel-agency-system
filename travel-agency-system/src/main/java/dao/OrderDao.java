package dao;

import java.util.List;

import model.Order;
import vo.ViewOrder;

public interface OrderDao {
	
	// 新增一筆訂單，並回傳帶有新ID的Order物件
    Order create(Order order);
    List<Order> findByCustomerId(int customerId);
    List<ViewOrder> findAllForView();
    void updateStatus(int orderId, String status);
    void delete(int orderId);

}
