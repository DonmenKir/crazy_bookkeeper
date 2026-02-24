package service;

import model.Loot;
import model.Adventurer;
import service.WorldService;
import service.QueueService;
import config.GameConfig;
import java.util.Map;

/**
 * [Service] LootGeneratorService - 冒險者生成器 (執行緒)
 * 職責：模擬冒險者源源不絕地來到公會。
 * 🛠️ 修正筆記：
 * 1. 增加 volatile 關鍵字確保 isGaming 跨執行緒可見。
 * 2. 新增 stopGenerator() 方法，並透過 interrupt() 立即喚醒沉睡中的執行緒，徹底關閉傳送門。
 */
public class LootGeneratorService extends Thread {

    private QueueService queueService;
    private WorldService worldService = new WorldService();
    
    // 使用 volatile 確保主執行緒更改此值時，本執行緒能立刻看見
    private volatile boolean isGaming = true;

    public LootGeneratorService(QueueService queueService) {
        this.queueService = queueService;
        // 語法：設定為守護執行緒 (隨主程式結束而終止)
        this.setDaemon(true);
    }

    /**
     * 技能：【傳送門關閉 (Stop Thread)】
     * 供 GameMainUI 在登出時呼叫，強制中斷此背景執行緒。
     */
    public void stopGenerator() {
        this.isGaming = false;
        this.interrupt(); // 立即打斷 Thread.sleep()，引發 InterruptedException
    }

    /**
     * 技能：【因果循環 (Thread Run)】
     */
    @Override
    public void run() {
        System.out.println(">>> [系統] 冒險者傳送門已開啟 (Thread Start)。");

        while (isGaming) {
            try {
                // 1. 執行技能：【奧術冥想 (Thread.sleep)】
                Thread.sleep(GameConfig.SPAWN_RATE_MS);
                
                if (!isGaming) break; // 醒來後再次確認是否已結束遊戲

                // 2. 隨機生成事件
                Map<String, Object> event = worldService.nextEvent();
                Loot loot = (Loot) event.get("loot");

                if (loot != null) {
                    // 3. 執行技能：【資產入庫 (Add to Queue)】
                    queueService.addLoot(loot);
                }
            } catch (InterruptedException e) {
                // 捕捉到 interrupt()，優雅地結束執行緒
                System.out.println(">>> [系統] 傳送門接收到關閉指令 (Interrupted)。");
                break;
            }
        }
        System.out.println(">>> [系統] 冒險者傳送門已完全關閉 (Thread End)。");
    }
}