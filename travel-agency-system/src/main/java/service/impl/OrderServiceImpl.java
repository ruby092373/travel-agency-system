package service.impl;

import java.util.ArrayList;
import java.util.List;

import dao.OrderDao;
import dao.OrderItemDao;
import dao.ShoppingCartItemDao;
import dao.impl.OrderDaoImpl;
import dao.impl.OrderItemDaoImpl;
import dao.impl.ShoppingCartItemDaoImpl;
import model.Order;
import model.OrderItem;
import model.ShoppingCartItem;
import service.OrderService;
import vo.ViewOrder;

public class OrderServiceImpl implements OrderService {
    
    private OrderDao orderDao = new OrderDaoImpl();
    private OrderItemDao orderItemDao = new OrderItemDaoImpl();
    private ShoppingCartItemDao cartDao = new ShoppingCartItemDaoImpl();

    @Override
    public Order createOrderFromCart(int customerId) {
        // 1. 獲取購物車所有內容
        List<ShoppingCartItem> cartItems = cartDao.findByCustomerId(customerId);
        
        if (cartItems.isEmpty()) {
            return null; // 購物車是空的，無法結帳
        }
        
        // 2. 計算總金額
        int totalPrice = 0;
        for (ShoppingCartItem item : cartItems) {
            totalPrice += item.getProduct().getPrice() * item.getQuantity();
        }
        
        // 3. 建立主訂單 (Order)
        Order newOrder = new Order();
        newOrder.setCustomerId(customerId);
        newOrder.setTotalPrice(totalPrice);
        
        // ** 在真實應用中，以下操作應包裹在一個資料庫"交易(Transaction)"中，確保資料一致性 **
        
        // 寫入資料庫並獲取含ID的Order物件
        Order createdOrder = orderDao.create(newOrder);
        if (createdOrder == null) {
            return null; // 訂單建立失敗
        }
        
        // 4. 準備訂單項目 (OrderItem)
        List<OrderItem> orderItems = new ArrayList<>();
        for (ShoppingCartItem cartItem : cartItems) {
        	OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(createdOrder.getId());
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getProduct().getPrice());
            // 【修改】將出發日期從購物車項目傳遞到訂單項目
            orderItem.setDepartureDate(cartItem.getDepartureDate());
            orderItems.add(orderItem);
        }
        
        // 5. 批次寫入訂單項目
        orderItemDao.addBatch(orderItems);
        
        // 6. 清空購物車
        cartDao.clearByCustomerId(customerId);
        
        return createdOrder;
    }

    @Override
    public List<Order> getOrderHistory(int customerId) {
        // 1. 查詢該顧客的所有主訂單
        List<Order> orders = orderDao.findByCustomerId(customerId);
        
        // 2. 為每一筆訂單，查詢其包含的訂單項目
        for (Order order : orders) {
            List<OrderItem> items = orderItemDao.findByOrderId(order.getId());
            order.setOrderItems(items);
        }
        
        return orders;
    }

	@Override
	public List<ViewOrder> getAllOrdersForView() {
		// 1. 查詢所有主訂單（已包含顧客姓名）
        List<ViewOrder> orders = orderDao.findAllForView();
        
        // 2. 為每一筆訂單，查詢其包含的訂單項目
        for (ViewOrder order : orders) {
            List<OrderItem> items = orderItemDao.findByOrderId(order.getOrderId());
            order.setOrderItems(items);
        }
        
        return orders;
    }

	@Override
	public void updateOrderStatus(int orderId, String status) {
		orderDao.updateStatus(orderId, status);
		
	}

	@Override
	public void deleteOrder(int orderId) {
		orderDao.delete(orderId);
		
	}
}
