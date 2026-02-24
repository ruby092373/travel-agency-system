package service;

import java.sql.Date;
import java.util.List;

import model.ShoppingCartItem;

public interface ShoppingCartService {
	
	/**
     * 將產品加入購物車。
     * 商業邏輯：如果購物車已有該商品，則增加數量；否則新增品項。
     */
	void addToCart(int customerId, int productId, int quantity, java.util.Date selectedDate);

    /** 獲取某位顧客的購物車內容 */
    List<ShoppingCartItem> getCartItems(int customerId);

    /** 移除購物車中的一個品項 */
    void removeItem(int itemId);
}
