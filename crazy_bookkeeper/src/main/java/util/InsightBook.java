package util;

import java.util.HashMap;
import java.util.Map;

/**
 * [Util] InsightBook - 秘法真理索引 (真理之書)
 * 職責：管理所有「已覺醒秘法」與 Java 語法的映射關係。
 * 核心教學：將遊戲術語轉化為 Package、Class 與 Method。
 */
public class InsightBook {

    // 格式：技能名稱 -> 語法詳細說明
    private static final Map<String, String> GRIMOIRE = new HashMap<>();

    static {
        // --- 第一系：秩序與資產管理 (Collections) ---
        GRIMOIRE.put("奧術口袋", "ArrayList (Class) - java.util | add(E) / get(i) : 動態調整大小的容器。");
        GRIMOIRE.put("禁忌結界", "HashSet (Class) - java.util | add(E) : 確保容器內沒有重複元素。");
        GRIMOIRE.put("優先序列", "PriorityQueue (Class) - java.util | poll() / offer() : 根據權重自動排序。");
        GRIMOIRE.put("靈魂索引", "HashMap (Class) - java.util | put(K,V) / get(K) : 鍵值對應快速查找。");
        GRIMOIRE.put("淨化術", "trim() - java.lang.String | 移除字串首尾空白雜質。");
        GRIMOIRE.put("空間切割", "split(regex) - java.lang.String | 依據符號切割字串為陣列。");
        GRIMOIRE.put("時之砂", "LocalDateTime (Class) - java.time | 獲取當前時空的時間戳記。");

        // --- 第二系：領域展開 (Stream API) ---
        GRIMOIRE.put("魔力流", "Stream (Interface) - java.util.stream | stream() : 開啟流動化資料處理。");
        GRIMOIRE.put("神聖濾網", "filter (Predicate) - java.util.function | 過濾不法或低價資產。");
        GRIMOIRE.put("轉化法陣", "map (Function) - java.util.function | 將物件轉化為另一種數據型態。");
        GRIMOIRE.put("萬物歸一", "forEach (Consumer) - java.util.stream | 對所有元素執行最終結算動作。");

        // --- 第三系：並行與共鳴 (Threads & I/O) ---
        GRIMOIRE.put("身外化身", "Thread (Class) - java.lang | start() : 創造獨立運行的次級分身。");
        GRIMOIRE.put("絕對領域", "synchronized (Keyword) | 鎖定臨界資源，防止時空重疊(Race Condition)。");
        GRIMOIRE.put("奧術冥想", "sleep(ms) - java.lang.Thread | 暫停執行緒，等待魔力冷卻。");
        GRIMOIRE.put("靈魂封印", "Serializable (Interface) - java.io | 標記物件使其可轉化為位元流存檔。");

        // --- 第五系：連線與異常防禦 (JDBC & Exception) ---
        GRIMOIRE.put("魔力管道", "Connection (Interface) - java.sql | 建立與資料庫金庫的通訊。");
        GRIMOIRE.put("符文預製", "PreparedStatement (Interface) - java.sql | 預編譯語法，防禦 SQL 注入攻擊。");
        GRIMOIRE.put("預言之書", "ResultSet (Interface) - java.sql | 儲存查詢結果，逐行讀取真理內容。");
        GRIMOIRE.put("奧術崩潰", "Exception (Class) - java.lang | 代表程式執行中的意外邏輯錯誤。");
        GRIMOIRE.put("時空屏障", "try-catch (Block) | 攔截異常，防止指揮塔崩潰(Game Over)。");
    }

    /**
     * 技能：【真理檢索】
     * 任務：提供 UI 日誌或試煉視窗所需的語法知識。
     */
    public static String getInsight(String skillName) {
        return GRIMOIRE.getOrDefault(skillName, "未知領域 (Unknown) - 此技能尚未登錄於語法法典中。");
    }
}