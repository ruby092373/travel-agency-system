package service.impl;

import dao.CustomerDao;
import dao.EmployeeDao;
import dao.impl.CustomerDaoImpl;
import dao.impl.EmployeeDaoImpl;
import model.Customer;
import model.Employee;
import service.LoginService;
import util.PasswordUtil;

public class LoginServiceImpl implements LoginService {

    // Service 層持有 DAO 層的實例，來操作資料庫
    private CustomerDao customerDao = new CustomerDaoImpl();
    private EmployeeDao employeeDao = new EmployeeDaoImpl();

    @Override
    public Customer registerCustomer(Customer customer) {
        // 檢查使用者名稱是否已被使用
        if (customerDao.selectByUsername(customer.getUsername()) != null) {
            return null; // 表示帳號已存在
        }

        // 將明文密碼加密
        String hashedPassword = PasswordUtil.hashPassword(customer.getPassword());
        customer.setPassword(hashedPassword);

        // 呼叫 DAO 儲存新用戶
        return customerDao.add(customer);
    }

    @Override
    public Customer customerLogin(String username, String password) {
        // 1. 從資料庫中根據 username 找出使用者
        Customer customerFromDb = customerDao.selectByUsername(username);

        // 2. 如果使用者不存在，登入失敗
        if (customerFromDb == null) {
            return null;
        }
        
        // 3. 檢查從資料庫回傳的 Customer 物件是否有密碼
        if (customerFromDb.getPassword() == null) {
            System.err.println("嚴重錯誤：CustomerDaoImpl 未將密碼載入到 Customer 物件中！");
            return null;
        }

        // 4. 使用 PasswordUtil 比對使用者輸入的密碼和資料庫中加密過的密碼
        if (PasswordUtil.checkPassword(password, customerFromDb.getPassword())) {
            return customerFromDb; // 密碼正確，登入成功
        }

        return null; // 密碼錯誤，登入失敗
    }

    /**
     * 員工登入的核心邏輯。
     * @param username 使用者輸入的帳號
     * @param password 使用者輸入的密碼
     * @return 如果登入成功，返回 Employee 物件；否則返回 null
     */
    @Override
    public Employee employeeLogin(String username, String password) {

        // 步驟 1: 呼叫 DAO 層，根據帳號從資料庫查詢員工資料
        // 這裡會執行 EmployeeDaoImpl.java 中的 selectByUsername 方法
        Employee employeeFromDb = employeeDao.selectByUsername(username);

        // 步驟 2: 檢查 DAO 是否有回傳員工物件
        // 如果 employeeFromDb 是 null，代表資料庫裡沒有這個帳號
        if (employeeFromDb == null) {
            System.out.println("登入失敗：帳號 " + username + " 不存在。");
            return null;
        }

        // ============================ NullPointerException 的關鍵點 ============================
        //
        // 步驟 3: 檢查從 DAO 回傳的 Employee 物件是否包含密碼
        //
        // 如果 EmployeeDaoImpl.java 的 selectByUsername 方法沒有從資料庫的 'password' 欄位
        // 讀取值並設定到 Employee 物件中，那麼 employeeFromDb.getPassword() 就會是 null。
        // 這就是造成您看到 NullPointerException 的直接原因。
        //
        // =================================================================================
        if (employeeFromDb.getPassword() == null) {
            System.err.println("嚴重錯誤：EmployeeDaoImpl.java 未將密碼載入到 Employee 物件中！請檢查 selectByUsername 方法。");
            // 直接返回 null，避免程式崩潰
            return null;
        }

        // 步驟 4: 執行密碼比對
        // PasswordUtil.checkPassword 會比對使用者輸入的明文密碼 (password)
        // 和資料庫裡加密過的密碼 (employeeFromDb.getPassword())
        if (PasswordUtil.checkPassword(password, employeeFromDb.getPassword())) {
            System.out.println("登入成功！歡迎，" + employeeFromDb.getName() + "。");
            return employeeFromDb; // 密碼正確，返回員工物件，登入成功
        }

        System.out.println("登入失敗：密碼不正確。");
        return null; // 密碼錯誤，登入失敗
    }
}
