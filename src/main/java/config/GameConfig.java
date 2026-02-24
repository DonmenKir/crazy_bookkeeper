package config;

/**
 * [Config] GameConfig - 奧術全域設定
 * 修正：補齊 DB_DRIVER 並統一命名為 DB_PASSWORD 以解決 Tool.java 的紅線錯誤。
 */
public class GameConfig {

    // --- 遊戲平衡參數 ---
    public static final int MAX_PRESSURE = 15;
    public static final int SPAWN_RATE_MS = 3000;
    public static final double DARK_SCROLL_MULTIPLIER = 1.5;

    // --- 資料庫連線金鑰 (在此統一管理) ---
    // 補齊：Tool.java 需要的驅動程式字串
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    public static final String DB_URL = "jdbc:mysql://localhost:3306/crazy_bookkeeper"
            + "?useUnicode=true"
            + "&characterEncoding=UTF-8"
            + "&serverTimezone=Asia/Taipei"
            + "&useSSL=false"
            + "&allowPublicKeyRetrieval=true";
            
    public static final String DB_USER = "root";
    
    // 修正：從 DB_PASS 改為 DB_PASSWORD 以對應你的舊有程式碼
    public static final String DB_PASSWORD = "1234"; 

    // --- 檔案路徑 ---
    public static final String EXCEL_RANKING_PATH = "Kingdom_Rankings.xlsx";
}