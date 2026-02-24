// 位於: service.impl
package service.impl;

import java.util.Date; // 【新】
import java.util.List;

import dao.ShoppingCartItemDao;
import dao.impl.ShoppingCartItemDaoImpl;
import model.ShoppingCartItem;
import service.ShoppingCartService;

public class ShoppingCartServiceImpl implements ShoppingCartService {

    private ShoppingCartItemDao cartDao = new ShoppingCartItemDaoImpl();

    @Override
    public void addToCart(int customerId, int productId, int quantity, Date departureDate) {
        // 為了簡化，我們假設不同出發日期的同一商品，是不同的購物車項目。
        // 所以這裡我們不再檢查 existingItem，而是直接新增。
        // 專業系統可能會做更複雜的判斷。
        
        ShoppingCartItem newItem = new ShoppingCartItem();
        newItem.setCustomerId(customerId);
        newItem.setProductId(productId);
        newItem.setQuantity(quantity);
        newItem.setDepartureDate(departureDate);
        cartDao.add(newItem);
    }

    @Override
    public List<ShoppingCartItem> getCartItems(int customerId) {
        return cartDao.findByCustomerId(customerId);
    }

    @Override
    public void removeItem(int itemId) {
        cartDao.delete(itemId);
    }
}
