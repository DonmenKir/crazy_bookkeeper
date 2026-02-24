package service;

import dao.impl.AdventurerDAOImpl;
import dao.impl.ItemTemplateDAOImpl;
import model.Adventurer;
import model.Loot;
import java.util.HashMap;
import java.util.Map;

/**
 * [Service] WorldService - 世界運作引擎 (Class)
 * 職責：作為 Controller (UI) 與 DAO 之間的邏輯層。
 * 任務：負責協調不同的 DAO 實作，並封裝成一個完整的「冒險者抵達」事件。
 * * * 秘法對應：
 * 1. 關注點分離 (Separation of Concerns)：UI 不應知道 SQL。
 * 2. 隨機事件流：利用 DAO 的隨機查詢功能產生物資與人物。
 */
public class WorldService {
    
    // 技能：【召喚數據守衛 (DAO Injection)】
    // 我們透過 DAO 實作類別來獲取資料庫中的原型數據
    private AdventurerDAOImpl adventurerDao = new AdventurerDAOImpl();
    private ItemTemplateDAOImpl itemTemplateDao = new ItemTemplateDAOImpl();

    /**
     * 技能：【因果律生成 (nextEvent)】
     * 任務：從資料庫中隨機挑選一位冒險者與一個物品原型，組合成一個事件包。
     * 語法標註：Map<String, Object> (靈活的容器)
     * * @return 包含 "adventurer" (Adventurer) 與 "loot" (Loot) 的數據包
     */
    public Map<String, Object> nextEvent() {
        Map<String, Object> event = new HashMap<>();
        
        // 1. 執行技能：【隨機冒險者召集 (adventurerDao.getRandomAdventurer)】
        // 從資料庫 `adventurer_types` 表格隨機獲取一位 NPC
        Adventurer adv = adventurerDao.getRandomAdventurer();
        
        // 2. 執行技能：【隨機物品掉落 (itemTemplateDao.getRandomLootTemplate)】
        // 從資料庫 `item_templates` 表格隨機獲取一個物品原型
        Loot lootTemplate = itemTemplateDao.getRandomLootTemplate();
        
        // 3. 執行技能：【事件封裝 (Data Packaging)】
        // 將結果放入 Map 中供 UI (Controller) 層提取
        event.put("adventurer", adv);
        event.put("loot", lootTemplate);
        
        // 日誌紀錄 (用於 Console 追蹤事件流)
        if (adv != null && lootTemplate != null) {
            System.out.println(">>> [世界引擎] 因果編織完成：" + adv.getName() + " 攜帶了 [" + lootTemplate.getItemName() + "]");
        } else {
            System.err.println("!!! [引擎異狀] 無法生成完整事件，請檢查資料庫初始資料是否正確存入。");
        }
        
        return event;
    }
}