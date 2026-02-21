package service;

import java.util.List;
import model.Loot;
import model.Member;
import exception.BookkeeperException;

/**
 * [Service Interface] AccountantService - 會計結算服務契約
 * 職責：定義記帳士法師對資產進行「轉化」與「結算」的行為標準。
 * * 🛠️ 修正重點：
 * 確保 processOneLoot 宣告了 throws BookkeeperException，
 * 這樣才能與 GameMainUI 中的 try-catch 區塊完美對接。
 */
public interface AccountantService {
    
    /**
     * 技能：【靈魂連結】
     * 任務：將當前操作的服務與特定的記帳士法師進行綁定。
     * @param member 當前登入的記帳士實體
     */
    void setCurrentMember(Member member);
    
    /**
     * 技能：【單筆結算】
     * 任務：判定資產價值，處理黑暗卷軸的加成，並將數據寫入金庫。
     * @param loot 待結算的戰利品
     * @throws BookkeeperException 當結算邏輯違反法典時拋出奧術崩潰異常
     */
    void processOneLoot(Loot loot) throws BookkeeperException;
    
    /**
     * 技能：【奧術超載】
     * 任務：施展領域展開，使用 Stream API 瞬間清算所有緩衝隊列中的合法資產。
     * @param loots 待結算的資產清單
     */
    void ultimateSettlement(List<Loot> loots);
}