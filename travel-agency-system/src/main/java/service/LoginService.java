// 位於: service
package service;

import model.Customer;
import model.Employee;

public interface LoginService {

    /**
     * 顧客註冊。
     * <p>
     * 商業邏輯：
     * 1. 檢查使用者名稱是否已被使用。
     * 2. 將明文密碼加密。
     * 3. 儲存新用戶。
     * @param customer 包含註冊資訊的 Customer 物件
     * @return 註冊成功則回傳完整的 Customer 物件 (包含新 ID)，若使用者名稱已存在則回傳 null
     */
    Customer registerCustomer(Customer customer);

    /**
     * 顧客登入。
     * @param username 使用者名稱
     * @param password 原始密碼
     * @return 登入成功則回傳該 Customer 物件，失敗則回傳 null
     */
    Customer customerLogin(String username, String password);

    /**
     * 員工登入。
     * @param username 使用者名稱
     * @param password 原始密碼
     * @return 登入成功則回傳該 Employee 物件，失敗則回傳 null
     */
    Employee employeeLogin(String username, String password);
}
