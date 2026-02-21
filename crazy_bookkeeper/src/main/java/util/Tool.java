package util;

import java.sql.Connection;

import config.GameConfig;

/**
 * [Util] Tool - 系統通用工具
 * 修正：將原本報錯的常數直接引用自 GameConfig，消除紅線並統一設定。
 */
public class Tool {

    // 修正：不再自己定義，而是從 GameConfig 獲取秘法印記
    public static final String DB_DRIVER = GameConfig.DB_DRIVER;
    public static final String DB_PASSWORD = GameConfig.DB_PASSWORD;

    /**
     * 其他工具方法 (例如字串處理或加密) 可放在此處
     */
    
    public static Connection getConn() {
        return DbHelper.getConn(); // 統一導向核心引擎
    }
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
}