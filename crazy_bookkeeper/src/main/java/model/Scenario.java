package model;

import java.util.List;

/**
 * [Model] Scenario - 語法情境實體
 * 職責：封裝一道「秘法試煉」題目，包含情境敘述與選項。
 */
public class Scenario {
    private String description;    // 遊戲情境敘述 (中文)
    private String correctSyntax;  // 正確的 Java 語法
    private List<String> options;  // 混合了正確與錯誤的四個選項

    public Scenario(String description, String correctSyntax, List<String> options) {
        this.description = description;
        this.correctSyntax = correctSyntax;
        this.options = options;
    }

    public String getDescription() { return description; }
    public String getCorrectSyntax() { return correctSyntax; }
    public List<String> getOptions() { return options; }
    
    // 檢查答案是否正確
    public boolean checkAnswer(String selected) {
        return correctSyntax.equals(selected);
    }
}