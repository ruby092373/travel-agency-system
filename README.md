# ✈️ Travel Agency Booking System (旅行社訂購與管理系統)

這是一個使用 **Java Swing** 與 **MySQL** 開發的桌面應用程式。專案採用標準的 **MVC (Model-View-Controller)** 架構與 **DAO (Data Access Object)** 設計模式，旨在模擬真實旅行社的線上訂購流程與後台管理機制。

## ✨ 核心功能 (Features)

本系統分為「顧客前台」與「員工後台」兩大模組：

### 🛒 顧客前台 (Customer Portal)
*   **帳號註冊與登入**：使用 BCrypt 演算法進行密碼加密，保障資訊安全。
*   **行程瀏覽與篩選**：支援依「地區」及「天數」動態篩選行程，並按價格自動排序。
*   **智慧購物車**：
    *   整合 `JDatePicker` 提供防呆日曆選擇器（限制選擇未來一年內的日期）。
    *   支援單選/複選行程加入購物車，自動計算人數與總金額。
*   **訂單結帳與歷史查詢**：一鍵結帳並清空購物車，顧客可隨時查看歷史訂單及詳細出發日期。

### ⚙️ 員工後台 (Admin Dashboard)
*   **產品管理 (CRUD)**：管理員可進行旅遊行程的「新增」、「查詢」、「修改」與「刪除」。
*   **訂單總覽與狀態追蹤**：
    *   使用 `LEFT JOIN` 確保所有訂單完整呈現。
    *   支援查看每筆訂單的詳細商品項目與出發日期。
    *   **狀態管理**：可將訂單狀態更新為「處理中」、「已付款」、「已完成」或「已取消」。
    *   **訂單刪除**：具備安全提示的級聯刪除功能。

---

## 🛠️ 技術堆疊 (Tech Stack)

*   **程式語言**: Java (JDK 11)
*   **UI 框架**: Java Swing (搭配 WindowBuilder 進行 UI 設計)
*   **資料庫**: MySQL (版本 8.0+)
*   **專案管理**: Maven
*   **核心架構**: MVC (Model-View-Controller), DAO (Data Access Object)
*   **關鍵第三方函式庫 (Dependencies)**:
    *   `mysql-connector-java`: JDBC 資料庫驅動。
    *   `HikariCP`: 高效能資料庫連線池，提升併發處理能力與穩定性。
    *   `jbcrypt`: 業界標準的密碼雜湊加密工具。
    *   `jdatepicker`: 優化 UI 體驗的日曆選擇元件。
    *   `slf4j-simple`: 日誌記錄實作。

---

## 🚀 系統架構 (Architecture)

專案嚴格遵循分層架構，確保程式碼的高內聚與低耦合：

*   **`model`**: 定義與資料庫資料表對應的實體類別 (POJO)。
*   **`vo`**: 視圖物件 (Value Object)，處理複雜 JOIN 查詢後的資料封裝 (如 `ViewOrder`)。
*   **`dao` & `dao.impl`**: 封裝所有 SQL 語句與 JDBC 操作，提供介面供 Service 層呼叫。
*   **`service` & `service.impl`**: 處理核心商業邏輯 (如：結帳時的金額計算、密碼加密驗證)。
*   **`controller`**: 包含所有 UI 介面，透過 `SwingWorker` 處理背景非同步任務，避免 UI 凍結。
*   **`util`**: 包含資料庫連線池設定 (`DatabaseManager`) 與日期格式化工具。

---

## 💻 如何在本地端執行 (How to Run)

1.  **準備資料庫**:
    *   確保您的本機已安裝 MySQL。
    *   建立一個名為 `travel_agency_db` 的資料庫。
    *   執行專案根目錄下的 `database_schema.sql` (需自行匯出，見下方說明) 來建立資料表與預設資料。
2.  **設定資料庫連線**:
    *   開啟專案中的 `src/main/java/util/DatabaseManager.java`。
    *   將 `config.setUsername("您的帳號")` 與 `config.setPassword("您的密碼")` 修改為您本機的 MySQL 設定。
3.  **編譯與執行**:
    *   將專案匯入 Eclipse (選擇 Import -> Existing Maven Projects)。
    *   找到 `controller.LoginUI.java`，右鍵點擊選擇 `Run As` -> `Java Application`。
    *   **測試帳號**：
        *   員工管理員：帳號 `admin` / 密碼 `admin`
        *   顧客：請透過系統的註冊功能自行建立。
