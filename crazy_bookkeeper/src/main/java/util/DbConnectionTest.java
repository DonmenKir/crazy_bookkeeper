package util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * [Util] DbConnectionTest - 連線測試法陣
 * 修正：將呼叫端從 Tool 改為 DbHelper，並配合新的 getDb() 方法。
 */
public class DbConnectionTest {
    public static void main(String[] args) {
        System.out.println("=== 《Crazy Bookkeeper》 秘法連通測試開始 ===");
        
        // 修正：指向新的 DbHelper 工具
        Connection conn = DbHelper.getDb();
        
        if (conn != null) {
            System.out.println("✨ [施法成功] 魔力管道已建立！連線對象：" + conn.getClass().getName());
            try {
                conn.close(); 
                System.out.println(">>> 管道已安全收納 (Closed)。");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("❌ [施法失敗] 管道為空，請檢查 DbHelper 內的設定或 MySQL 服務是否啟動。");
        }
        
        System.out.println("=== 測試結束 ===");
    }
}