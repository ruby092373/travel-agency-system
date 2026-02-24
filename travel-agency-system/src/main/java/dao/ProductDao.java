package dao;

import java.util.List;
import model.Product;

public interface ProductDao {

    void add(Product p);
    void update(Product p);
    void delete(int id);
    Product selectById(int id);
    List<Product> selectAll();

    /**
     * 【新功能】根據條件搜尋產品。
     * @param region 地區 (若為 null 或 "全部地區"，則忽略此條件)
     * @param durationDays 天數 (若為 null 或 0，則忽略此條件)
     * @return 符合條件的產品列表
     */
    List<Product> search(String region, Integer durationDays);
    
    //獲取資料庫中所有不重複的旅遊天數
    List<Integer> getDistinctDurationDays();
}
