package util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * 一個簡單的工具，用來產生 jBCrypt 密碼雜湊。
 * 執行這個 main 方法，然後將控制台中輸出的雜湊值複製到資料庫。
 */
public class PasswordGenerator {

    public static void main(String[] args) {
        // 在這裡設定您想要加密的密碼
        String plainPassword = "admin";

        // 使用 BCrypt 產生密碼雜湊，工作因子設為 10
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));

        System.out.println("==============================================================================");
        System.out.println("請將以下產生的密碼雜湊，手動複製並更新到您的資料庫 'employee' 資料表中 'admin' 使用者的 'password' 欄位：");
        System.out.println();
        System.out.println(hashedPassword);
        System.out.println();
        System.out.println("==============================================================================");
    }
}
