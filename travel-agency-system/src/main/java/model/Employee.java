package model;

/**
 * 員工模型 (Model)，對應資料庫中的 employee 資料表
 *
 * 這是一個 JavaBean，包含：
 * 1. 私有 (private) 屬性
 * 2. 公開 (public) 的 getter 和 setter 方法
 * 3. 一個無參數的建構子 (預設)
 */
public class Employee {

    // --- 屬性 (Fields) ---
    // 這些屬性的名稱和類型應與資料庫的欄位對應
    
    private Integer id;
    private String name;
    private String username;
    private String password; // 確保這個屬性存在！
    private String email;
    private String phone;

    // --- 建構子 (Constructors) ---
    // 一個空的建構子是 JavaBean 的標準
    public Employee() {
    }

    // --- Getter 和 Setter 方法 ---
    // 這些方法讓其他類別 (如 DAO) 可以存取和設定私有屬性

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // ▼▼▼ 這是最關鍵的部分 ▼▼▼
    public String getPassword() {
        return password; // 確保這裡返回的是 password 屬性
    }

    public void setPassword(String password) {
        // 確保這裡將傳入的 password 賦值給 this.password
        this.password = password; 
    }
    // ▲▲▲ 這是最關鍵的部分 ▲▲▲

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
