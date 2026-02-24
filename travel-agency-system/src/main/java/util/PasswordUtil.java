// 位於: tool
package util; // <-- 已修改為您的 package 名稱

import org.mindrot.jbcrypt.BCrypt;

/**
 * 密碼處理工具類
 * 使用 BCrypt 演算法進行密碼的加密和驗證。
 */
public class PasswordUtil {

    /**
     * 將明文密碼加密成 BCrypt 雜湊值。
     */
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    /**
     * 驗證使用者輸入的密碼是否與資料庫中儲存的雜湊值相符。
     */
    public static boolean checkPassword(String plainPassword, String hashedPasswordFromDb) {
        try {
            // BCrypt 內部會自動從 hashedPasswordFromDb 中解析出 salt 並進行比對
            return BCrypt.checkpw(plainPassword, hashedPasswordFromDb);
        } catch (IllegalArgumentException e) {
            // 如果資料庫中的密碼格式不正確，會拋出例外
            // 在這裡記錄錯誤日誌，並返回 false
            System.err.println("錯誤：資料庫中的密碼雜湊格式不正確。");
            return false;
        }
    }
}
