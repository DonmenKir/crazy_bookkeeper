package service.impl;

import java.util.List;
import service.AccountantService;
import model.Loot;
import model.Member;
import dao.impl.LootDAOImpl;
import dao.impl.GuildStatusDAOImpl;
import exception.BookkeeperException;
import config.GameConfig;

/**
 * [Service Impl] AccountantServiceImpl - 會計結算核心實作
 * 🛠️ 終極機制修正：
 * 「Accept (簽署契約)」不再刪除紀錄，而是將戰利品【正式寫入資料庫 (官方帳本)】。
 * 這麼一來，如果記帳士大意地將違禁品寫入帳本，王國查帳時就能在資料庫中揪出不法所得！
 */
public class AccountantServiceImpl implements AccountantService {

    private Member currentMember;
    private LootDAOImpl lootDao = new LootDAOImpl();
    private GuildStatusDAOImpl statusDao = new GuildStatusDAOImpl();

    @Override
    public void setCurrentMember(Member member) {
        this.currentMember = member;
    }

    @Override
    public void processOneLoot(Loot loot) throws BookkeeperException {
        if (currentMember == null || currentMember.getId() == null) {
            throw new BookkeeperException("結算失敗：靈魂連結不穩定或未獲得資料庫編號，請嘗試重新登入。");
        }

        if (loot == null) {
            throw new BookkeeperException("結算失敗：待處理的資產已消散（Loot is null）。");
        }

        try {
            int baseValue = loot.getItemValue() != null ? loot.getItemValue() : 0;
            int finalValue = baseValue;

            if (loot.isIllegal()) {
                finalValue = (int) (baseValue * GameConfig.DARK_SCROLL_MULTIPLIER);
            }

            // 1. 執行資料庫連動 (更新金庫餘額)
            statusDao.updateGold(currentMember.getId(), finalValue);

            // 2. 🛡️ 核心修正：將此筆交易寫入官方帳本 (資料庫)，留下不可抹滅的紀錄
            loot.setMemberId(currentMember.getId());
            lootDao.add(loot); 

            System.out.println(">>> [結算成功] 記帳士 " + currentMember.getNickname() + 
                               " 完成資產轉化，獲得 " + finalValue + " G (已登錄官方帳本)。");

        } catch (Exception e) {
            e.printStackTrace();
            throw new BookkeeperException("結算過程發生魔力回衝：金庫同步失敗。原因：" + e.getMessage());
        }
    }

    @Override
    public void ultimateSettlement(List<Loot> loots) {
        // 奧術超載已從 UI 移除，此處保留空實作或原有邏輯即可
    }
}