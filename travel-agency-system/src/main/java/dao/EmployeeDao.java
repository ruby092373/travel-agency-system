package dao;

import model.Employee;

public interface EmployeeDao {

    /**
     * 透過使用者名稱查詢員工。
     * @param username 員工的使用者名稱
     * @return 找到則回傳 Employee 物件；找不到則回傳 null
     */
    Employee selectByUsername(String username);
}
