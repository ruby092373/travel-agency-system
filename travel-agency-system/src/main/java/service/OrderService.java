package service;

import java.util.List;

import model.Order;
import vo.ViewOrder;

public interface OrderService {
	
	/**
     * 從購物車建立訂單。
     * 商業邏輯：計算總價 -> 建立訂單 -> 建立訂單項目 -> 清空購物車。
     * @return 成功則回傳建立的 Order 物件，若購物車為空則回傳 null。
     */
    Order createOrderFromCart(int customerId);

    /**
     * 獲取某位顧客的歷史訂單。
     * 商業邏輯：查詢所有訂單，並為每筆訂單載入其詳細項目。
     */
    List<Order> getOrderHistory(int customerId);

    List<ViewOrder> getAllOrdersForView();
    
    void updateOrderStatus(int orderId, String status);
    void deleteOrder(int orderId);
}
