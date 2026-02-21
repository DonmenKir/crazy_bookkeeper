package service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import model.Scenario;

/**
 * [Service] ScenarioService - 秘法試煉生成器
 * 職責：從「語法情境設計書」中隨機生成題目，並混淆選項。
 */
public class ScenarioService {

    // 全部可能的 Java 語法庫 (用於生成干擾項)
    private static final List<String> ALL_SYNTAX = Arrays.asList(
        "new ArrayList<>()", "hashset.add(item)", "new PriorityQueue<>(cmp)", "new HashMap<>()",
        "Arrays.sort(list)", "list.stream()", ".filter(t -> ...)", ".map(t -> ...)", 
        ".forEach(t -> ...)", "new FileWriter(path)", "oos.writeObject(obj)", "new HSSFWorkbook()",
        "class T extends Thread", "synchronized(this)", "thread.join()", "DriverManager.getConnection()",
        "conn.prepareStatement(sql)", "rs.next()", "str.trim()", "str.split(\",\")", 
        "LocalDateTime.now()", "try { ... } catch", "throw new Exception()"
    );

    // 題庫資料結構：{ "情境描述", "正確語法" }
    private static final String[][] DATA_SOURCE = {
        // --- 第一系：秩序與資產管理 ---
        {"冒險者提交了大量戰利品，數量多變，需準備容器收納。", "new ArrayList<>()"},
        {"偵測到重複的資產殘影，需施展結界進行排他性驗證。", "hashset.add(item)"},
        {"戰利品湧入過快，必須讓高威脅物品優先進入序列。", "new PriorityQueue<>(cmp)"},
        {"需要根據「冒險者姓名」快速查找其個人檔案 (Key-Value)。", "new HashMap<>()"},
        {"對一堆雜亂的武器進行瞬間整隊 (Sorting)。", "Arrays.sort(list)"},
        {"名稱前後帶有空白雜質，需施展淨化術。", "str.trim()"},
        {"收到逗號串接的長條咒文，需執行空間切割。", "str.split(\",\")"},
        {"戰利品具備保鮮期，需刻印當前時間。", "LocalDateTime.now()"},

        // --- 第二系：領域展開 ---
        {"面對整袋資產，開啟魔力流準備全自動化審核。", "list.stream()"},
        {"啟動濾網，自動篩除所有不符合法規的混亂物品。", ".filter(t -> ...)"},
        {"施展轉化法陣，將流中的資產數值進行變更。", ".map(t -> ...)"},
        {"審核完畢，將所有金幣一次性灌入金庫 (終端操作)。", ".forEach(t -> ...)"},

        // --- 第三系：並行與同步 ---
        {"指揮塔必須一邊接收物資一邊計算，需召喚分身。", "class T extends Thread"},
        {"多位會計師同時存取金庫，需展開絕對領域防止錯誤。", "synchronized(this)"},
        {"大結算日，主程序必須等待所有分身回報完畢。", "thread.join()"},

        // --- 第五系：異常與防禦 ---
        {"偵測到奧術崩潰 (Exception)，啟動屏障防禦。", "try { ... } catch"},
        {"發現受詛咒的帳目，必須主動警告系統 (中斷流程)。", "throw new Exception()"},
        {"建立通往王國中央金庫的魔力管道。", "DriverManager.getConnection()"},
        {"為防禦語法注入攻擊，使用符文預製查詢。", "conn.prepareStatement(sql)"},
        {"翻開查詢回來的預言之書，逐行檢視帳目。", "rs.next()"}
    };

    private Random random = new Random();

    /**
     * 技能：【試煉生成】
     * 隨機挑選一個情境，並生成 4 個選項 (1 對 3 錯)。
     */
    public Scenario getRandomScenario() {
        // 1. 隨機選題
        int index = random.nextInt(DATA_SOURCE.length);
        String description = DATA_SOURCE[index][0];
        String correctSyntax = DATA_SOURCE[index][1];

        // 2. 生成干擾項
        List<String> options = new ArrayList<>();
        options.add(correctSyntax); // 加入正確答案

        while (options.size() < 4) {
            String wrong = ALL_SYNTAX.get(random.nextInt(ALL_SYNTAX.size()));
            if (!options.contains(wrong)) {
                options.add(wrong);
            }
        }

        // 3. 打亂選項順序
        Collections.shuffle(options);

        return new Scenario(description, correctSyntax, options);
    }
}