package service;

import java.util.List;
import model.Product;

public interface ProductService {

    void addProduct(Product p);

    void updateProduct(Product p);

    void deleteProduct(int id);

    Product getProductById(int id);

    List<Product> getAllProducts();

    /**
     * 根據條件搜尋產品。
     * @param region 地區
     * @param durationDays 天數
     * @return 符合條件的產品列表
     */
    List<Product> searchProducts(String region, Integer durationDays);
    
    List<Integer> getDistinctDurationDays();
}
