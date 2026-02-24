package dao;

import java.util.List;
import model.Loot;

/**
 * [Interface] LootDAO - 戰利品數據存取介面
 * 職責：定義與資產（戰利品）相關的 CRUD 存取規範。
 */
public interface LootDAO {
    // 技能：【資產入庫 (Create)】
    void add(Loot loot);
    
    // 技能：【全域掃描 (Read All)】 - 王國查帳時使用
    List<Loot> queryAll();
    
    // 技能：【個人帳目查詢 (Read by Member)】
    List<Loot> queryByMember(int memberId);
    
    // 技能：【資產屬性變更 (Update)】 - 洗白時使用
    void update(Loot loot);
    
    // 技能：【資產抹除 (Delete)】 - 結算或沒收時使用
    void delete(int id);
}