package service;

import java.util.ArrayList;
import java.util.List;
import model.Loot;

/**
 * [Service] QueueService - 奧術資產隊列服務 (Class)
 * 職責：管理待處理資產的緩衝區。
 * 🛠️ 最新修正：新增 hasIllegalLoot() 提供給王國查帳系統進行全域掃描。
 */
public class QueueService {

    // 隊列容器：存放待結算的資產
    private List<Loot> lootQueue = new ArrayList<>();

    /**
     * 技能：【資產入庫 (Add to Queue)】
     */
    public synchronized void addLoot(Loot loot) {
        lootQueue.add(loot);
        System.out.println(">>> [隊列共鳴] 成功將 [" + loot.getItemName() + "] 放入待處理序列。");
    }

    /**
     * 技能：【資產提取 (Get Next)】
     */
    public synchronized Loot getNextLoot() {
        if (lootQueue.isEmpty()) {
            return null;
        }
        return lootQueue.remove(0);
    }

    /**
     * 技能：【隊列深度探測 (Size Check)】
     */
    public synchronized int getQueueSize() {
        return lootQueue.size();
    }
    
    /**
     * 技能：【奧術超載提取 (Clear & Extract All)】
     */
    public synchronized List<Loot> clearAndExtractAll() {
        List<Loot> allLoots = new ArrayList<>(lootQueue);
        lootQueue.clear();
        return allLoots;
    }

    /**
     * 🛡️ 技能：【王國稽核掃描 (Stream.anyMatch)】
     * 任務：掃描隊列中是否含有「未被藏匿的非法黑暗卷軸」。
     */
    public synchronized boolean hasIllegalLoot() {
        return lootQueue.stream().anyMatch(Loot::isIllegal);
    }
}