package model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * [Model] Loot - 戰利品模型 (Class)
 * 職責：對應資料庫 `loot` 表格，攜帶資產的靈魂資料。
 * * [術語對照]
 * - 混亂物 (Illegal)：isIllegal = true (黑暗魔法卷軸)
 * - 品級 (Grade)：NORMAL / LEGENDARY
 */
public class Loot implements Serializable, Comparable<Loot> {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String itemName;
    private Integer itemValue;
    private String grade;       // 品級：NORMAL 或 LEGENDARY
    private boolean isIllegal;  // 黑暗魔法標籤
    private Integer memberId;
    private LocalDateTime createTime;

    public Loot() {}

    // --- 關鍵：Getter & Setter (封裝術) ---

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public Integer getItemValue() { return itemValue; }
    public void setItemValue(Integer itemValue) { this.itemValue = itemValue; }

    // 此處即為 DAO 報錯缺失的方法
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public boolean isIllegal() { return isIllegal; }
    public void setIllegal(boolean illegal) { isIllegal = illegal; }

    public Integer getMemberId() { return memberId; }
    public void setMemberId(Integer memberId) { this.memberId = memberId; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    /**
     * 技能：【優先權判定 (Comparable)】
     * 規則：傳說級與黑暗卷軸優先排在隊列前端。
     */
    @Override
    public int compareTo(Loot other) {
        if (this.isIllegal != other.isIllegal) {
            return this.isIllegal ? -1 : 1;
        }
        return other.itemValue.compareTo(this.itemValue);
    }
}