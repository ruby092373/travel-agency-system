package dao;

import java.util.List;

import model.ShoppingCartItem;

public interface ShoppingCartItemDao {
	
	void add(ShoppingCartItem item);
    void updateQuantity(int itemId, int newQuantity);
    void delete(int itemId);
    ShoppingCartItem findByCustomerIdAndProductId(int customerId, int productId);
    List<ShoppingCartItem> findByCustomerId(int customerId);
    void clearByCustomerId(int customerId);

}
