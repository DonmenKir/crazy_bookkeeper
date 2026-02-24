package model;

import java.io.Serializable;

/**
 * [Model] GuildStatus - 秘法金庫與核心狀態 (Entity)
 * 具象化角色：【公會魔力之核：能源中樞】
 * * 秘法特性：
 * 1. 貫徹【整數結算法典】：金幣與魔力皆為 Integer。
 * 2. 新增【魔力之核 (current_mana)】：用於施展高階魔法 (如：時空洗白)。
 * 3. 實作 Serializable：支援【靈魂封印卷軸】存檔。
 */
public class GuildStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer memberId;         // 靈魂綁定 (Foreign Key)
    private Integer currentGold;      // 純淨金幣 (Resource: Gold)
    private Integer currentMana;      // 乙太魔力 (Resource: Mana) - 新增欄位
    private Integer databasePressure; // 奧術壓力 (Risk: Pressure)

    public GuildStatus() {
        this.currentGold = 0;
        this.currentMana = 0;
        this.databasePressure = 0;
    }

    /**
     * 建構子：用於初始化新公會
     */
    public GuildStatus(Integer memberId, Integer currentGold, Integer currentMana) {
        this.memberId = memberId;
        this.currentGold = currentGold;
        this.currentMana = currentMana;
        this.databasePressure = 0;
    }

    // --- Getter & Setter 區 (封裝術) ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getCurrentGold() {
        return currentGold;
    }

    public void setCurrentGold(Integer currentGold) {
        this.currentGold = currentGold;
    }

    /**
     * 獲取當前魔力存量
     * @return 乙太魔力值 (Integer)
     */
    public Integer getCurrentMana() {
        return currentMana;
    }

    public void setCurrentMana(Integer currentMana) {
        this.currentMana = currentMana;
    }

    public Integer getDatabasePressure() {
        return databasePressure;
    }

    public void setDatabasePressure(Integer databasePressure) {
        this.databasePressure = databasePressure;
    }

    /**
     * 技能：【奧術投影 (toString)】
     */
    @Override
    public String toString() {
        return String.format("GuildStatus [ID=%d, 金幣=%d G, 魔力=%d M, 壓力=%d]", 
                memberId, currentGold, currentMana, databasePressure);
    }
}