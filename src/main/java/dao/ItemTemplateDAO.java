package dao;

import model.Adventurer;
import model.Loot;
import java.util.List;

/**
 * [Interface] World DAOs - 世界原型數據存取
 * 職責：從資料庫提取冒險者與物品的原型。
 */

public interface ItemTemplateDAO {
    Loot getRandomLootTemplate();
}
