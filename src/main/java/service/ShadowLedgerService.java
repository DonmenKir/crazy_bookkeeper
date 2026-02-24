package service;

import java.util.HashMap;
import java.util.Map;
import model.Loot;
import java.util.List;
import java.util.ArrayList;

/**
 * [Service] ShadowLedgerService - 幽影黑帳中心 (Class)
 * 角色職責：負責管理不願公開的非法帳目 (HashMap 實作)。
 * 🛠️ 最新修正：新增 extractAll()，允許將洗白後的資產重新提取至現實世界。
 */
public class ShadowLedgerService {

    // 使用【幽影緩衝區 (HashMap)】存放在記憶體中，DB 查不到
    private Map<Integer, Loot> secretVault = new HashMap<>();

    /**
     * 技能：【暫存幽影】 (Map.put)
     */
    public void stashLoot(Loot loot) {
        // 若 ID 為空，則賦予一個臨時的虛擬 ID，避免 HashMap 覆蓋
        int stashId = loot.getId() != null ? loot.getId() : (int)(Math.random() * 10000);
        secretVault.put(stashId, loot);
    }

    /**
     * 技能：【術名偽裝】 (String.replace)
     * 將黑帳內的物品「洗白」
     */
    public void launderMoney() {
        secretVault.values().forEach(loot -> {
            String oldName = loot.getItemName();
            String newName = oldName.replace("混亂的", "合格的").replace("深淵的", "王國的").replace("黑市", "特許");
            loot.setItemName(newName);
            loot.setIllegal(false); // 洗除黑暗印記
            System.out.println(">>> [洗白成功] " + oldName + " -> " + newName);
        });
    }

    /**
     * 技能：【黑帳提取】 (Map.values)
     * 將洗白後的資產全數取出，並清空黑帳空間。
     */
    public List<Loot> extractAll() {
        List<Loot> launderedLoots = new ArrayList<>(secretVault.values());
        secretVault.clear();
        return launderedLoots;
    }
}