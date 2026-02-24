package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import model.Scenario;
import util.InsightBook;

/**
 * [Service] ScenarioService - 秘法試煉生成器
 * 🛠️ 修正筆記：
 * 1. 增加 picked.length 判定 (防禦性編程)，防止 CSV 欄位不足時引發 ArrayIndexOutOfBoundsException。
 * 2. 即使 CSV 只有兩欄，遊戲也能正常運行並顯示 Unknown 提示。
 */
public class ScenarioService {

    private Random random = new Random();

    /**
     * 技能：【試煉生成 (Dynamic Generation)】
     */
    public Scenario getRandomScenario() {
        // 1. 從 InsightBook 獲取最新資料
        List<String[]> source = InsightBook.getScenarioData();
        List<String> allSyntax = InsightBook.getAllSyntaxList();

        // 🛡️ 錯誤處理：若 CSV 讀取失敗或檔案為空
        if (source == null || source.isEmpty()) {
            return new Scenario(
                "真理之書遺失...", 
                "null", 
                Collections.singletonList("null"), 
                "Unknown", 
                "None"
            );
        }

        // 2. 隨機抽選一條資料
        String[] picked = source.get(random.nextInt(source.size()));
        
        // 🛡️ 核心修正：安全提取欄位 (檢查陣列長度)
        // 索引 0: 描述, 1: 語法, 2: 類型, 3: 套件
        String description = (picked.length > 0) ? picked[0] : "無敘述";
        String correctSyntax = (picked.length > 1) ? picked[1] : "null";
        String type = (picked.length > 2) ? picked[2] : "Unknown";         
        String packagePath = (picked.length > 3) ? picked[3] : "None";

        // 4. 準備選項 (1對 3錯)
        List<String> options = new ArrayList<>();
        options.add(correctSyntax);

        // 從總庫中隨機挑選干擾項
        List<String> distractors = new ArrayList<>(allSyntax);
        distractors.remove(correctSyntax);
        Collections.shuffle(distractors);

        for (int i = 0; i < 3 && i < distractors.size(); i++) {
            options.add(distractors.get(i));
        }

        // 5. 打亂選項順序
        Collections.shuffle(options);

        // 6. 呼叫具備 5 個參數的建構子
        return new Scenario(description, correctSyntax, options, type, packagePath);
    }
}