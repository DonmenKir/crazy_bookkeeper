package model;

import java.io.Serializable;

/**
 * [Model] Score - 戰績實體 (Class)
 * 職責：封裝單場遊戲的最終結果，包含記帳士暱稱、金幣、採購清單與結算時間。
 */
public class Score implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nickname;
    private Integer finalGold;
    private String purchasedItems; // 採購明細 (例如: 冷卻劑x2, 卷軸x1)
    private String formatTime;

    public Score() {}

    public Score(String nickname, Integer finalGold, String purchasedItems, String formatTime) {
        this.nickname = nickname;
        this.finalGold = finalGold;
        this.purchasedItems = purchasedItems;
        this.formatTime = formatTime;
    }

    // --- Getter & Setter (封裝術 Encapsulation) ---

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public Integer getFinalGold() { return finalGold; }
    public void setFinalGold(Integer finalGold) { this.finalGold = finalGold; }

    public String getPurchasedItems() { return purchasedItems; }
    public void setPurchasedItems(String purchasedItems) { this.purchasedItems = purchasedItems; }

    public String getFormatTime() { return formatTime; }
    public void setFormatTime(String formatTime) { this.formatTime = formatTime; }
}