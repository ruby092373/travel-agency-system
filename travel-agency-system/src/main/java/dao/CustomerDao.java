package dao;

import model.Customer;

public interface CustomerDao {

    /**
     * 新增一名顧客 (通常用於註冊)。
     * @param customer 包含顧客資料的物件
     * @return 新增成功後，回傳包含新 ID 的顧客物件；失敗則回傳 null
     */
    Customer add(Customer customer);

    /**
     * 透過使用者名稱查詢顧客。
     * @param username 顧客的使用者名稱
     * @return 找到則回傳 Customer 物件；找不到則回傳 null
     */
    Customer selectByUsername(String username);
}
