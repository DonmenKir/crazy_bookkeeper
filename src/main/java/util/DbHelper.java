package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import config.GameConfig;

/**
 * [Util] DbHelper - 奧術資料庫連線中心
 * 職責：作為記帳士法師通往 SQL 金庫的橋樑。
 * * 🛠️ 最終相容性修正說明：
 * 1. 同時提供 getDb() 與 getConn()，確保測試檔與所有 DAO 實作檔皆不報錯。
 * 2. 由於 dao.impl 層級目前指向 Tool.java，此類別建議作為 Tool.getConn() 的底層實作。
 * 3. 統一由內部處理 SQLException，回傳 Connection 物件或 null。
 */
public class DbHelper {

    // 靜態區塊：類別載入時立即喚醒資料庫驅動程式
    static {
        try {
            // 使用全域設定檔中的驅動路徑 (com.mysql.cj.jdbc.Driver)
            Class.forName(GameConfig.DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("❌ [系統錯誤] 找不到 MySQL 驅動程式，請確認是否已匯入 mysql-connector-java.jar");
            e.printStackTrace();
        }
    }

    /**
     * 技能：建立金庫通道 (getDb)
     * 對接：DbConnectionTest.java 專用
     * @return Connection 成功的連線物件；若失敗則回傳 null
     */
    public static Connection getDb() {
        try {
            // 從 GameConfig 取得統一的連線金鑰
            return DriverManager.getConnection(
                GameConfig.DB_URL, 
                GameConfig.DB_USER, 
                GameConfig.DB_PASSWORD
            );
        } catch (SQLException e) {
            System.err.println("!!! [連線失敗] 無法連通 SQL 領域，請檢查 GameConfig 密碼與 MySQL 服務狀態 !!!");
            System.err.println("詳細原因：" + e.getMessage());
            return null;
        }
    }

    /**
     * 技能：建立金庫通道 (getConn)
     * 對接說明：
     * 由於 dao.impl 中多數檔案（如 MemberDAOImpl）目前指向 Tool.java，
     * 建議在 Tool.java 中使用以下代碼進行轉發：
     * public static Connection getConn() { return DbHelper.getConn(); }
     */
    public static Connection getConn() {
        return getDb();
    }

    /**
     * 測試工具：直接在 Eclipse 執行此檔案可驗證連線是否暢通
     */
    public static void main(String[] args) {
        System.out.println(">>> 正在啟動奧術連線測試...");
        
        Connection conn = getDb();
        
        if (conn != null) {
            System.out.println("✅ [成功] 奧術金庫通道已順利開啟！");
            try {
                conn.close();
                System.out.println(">>> 通道已安全收納。");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("❌ [失敗] 無法開啟通道，請檢查控制台報錯訊息。");
        }
    }
}