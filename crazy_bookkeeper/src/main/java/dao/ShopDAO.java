package dao;

import java.util.List;
import model.ShopItem;

/**
 * [Interface] ShopDAO - 奧術商店數據存取介面
 * 職責：定義與商店物資、庫存相關的存取規範。
 */
public interface ShopDAO {
    // 技能：【物資清單查詢 (Query All)】
    List<ShopItem> queryAll();
    
    // 技能：【特定物資解析 (Query by ID)】
    ShopItem queryById(int id);
    
    // 技能：【庫存配給更新 (Update Stock)】
    void updateStock(int itemId, int amount);
}