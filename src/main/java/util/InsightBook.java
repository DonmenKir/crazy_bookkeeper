package util;

import java.io.*;
import java.util.*;

/**
 * [Util] InsightBook - 秘法真理索引
 * 🛠️ 修正筆記：
 * 1. 升級 CSV 解析算法，支援 Excel 轉義引號 ("") 處理。
 * 2. 修正欄位偵測邏輯，確保「類型」與「套件」能正確被讀取。
 */
public class InsightBook {

    private static final String CSV_PATH = "InsightBook.csv";
    private static final List<String[]> SCENARIO_DATA = new ArrayList<>();
    private static final List<String> ALL_SYNTAX_LIST = new ArrayList<>();

    static {
        loadFromCsv();
    }

    private static void loadFromCsv() {
        File file = new File(CSV_PATH);
        if (!file.exists()) {
            System.err.println("!!! [系統警告] 找不到 " + CSV_PATH);
            return;
        }

        // 使用 MS950 確保讀取 Excel 儲存的 Big5 編碼不亂碼
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "MS950"))) {
            String line;
            boolean isHeader = true;
            
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // 🛡️ 防禦：處理 UTF-8 BOM
                if (isHeader && line.startsWith("\uFEFF")) line = line.substring(1);
                if (isHeader) { isHeader = false; continue; }

                // 使用強韌的解析器
                List<String> parts = parseCsvLine(line);
                
                // 檢查是否至少有 4 個欄位 (描述, 語法, 類型, 套件)
                if (parts.size() >= 4) {
                    String[] data = new String[] {
                        parts.get(0), // 描述
                        parts.get(1), // 語法
                        parts.get(2), // 類型
                        parts.get(3)  // 套件
                    };
                    SCENARIO_DATA.add(data);
                    if (!ALL_SYNTAX_LIST.contains(data[1])) {
                        ALL_SYNTAX_LIST.add(data[1]);
                    }
                }
            }
            System.out.println(">>> [秘法載入成功] 已覺醒 " + SCENARIO_DATA.size() + " 條包含提示的真理。");
        } catch (Exception e) {
            System.err.println("!!! [載入異常] " + e.getMessage());
        }
    }

    /**
     * 🛡️ 進階 CSV 解析器 (State Machine 邏輯)
     * 支援：雙引號包覆、引號內含逗號、轉義雙引號 ("")
     */
    private static List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '\"') {
                // 處理轉義引號 "" (Excel 規則)
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    sb.append('\"');
                    i++; // 跳過下一個引號
                } else {
                    inQuotes = !inQuotes; // 切換引號狀態
                }
            } else if (c == ',' && !inQuotes) {
                result.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        result.add(sb.toString().trim());
        return result;
    }

    public static List<String[]> getScenarioData() {
        if (SCENARIO_DATA.isEmpty()) loadFromCsv();
        return SCENARIO_DATA;
    }

    public static List<String> getAllSyntaxList() {
        return ALL_SYNTAX_LIST;
    }
}