package vo;

import java.io.Serializable;

/**
 * [VO] CartItemVO - 奧術採購視圖物件 (View Object)
 * 職責：封裝商店物資在 UI 購物車中的顯示狀態。
 * * * 秘法設定：
 * 1. 實作 Serializable 介面，支援技能：【物件序列化封印 (Serialization)】。
 * 2. 透過建構子進行【資料格式化 (Data Formatting)】，確保 UI 風格統一。
 */
public class CartItemVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String productName;
    private Integer price;
    private String description;
    
    /**
     * 技能：【奧術資訊投影 (UI Display String)】
     * 格式：中文技能名稱 (對應 Java 語法) [價格 G]
     */
    private String displayInfo;

    public CartItemVO() {}

    /**
     * 技能：【視圖轉換術 (VO Construction)】
     * @param name 物品名稱
     * @param price 價格
     * @param syntax 該物品對應的 Java 語法 (例如: ProgressBar.setValue)
     */
    public CartItemVO(String name, int price, String syntax, String desc) {
        this.productName = name;
        this.price = price;
        this.description = desc;
        
        // 自動生成符合遊戲風格的顯示字串
        // 範例：奧術壓力冷卻劑 (ProgressBar.setValue) [200 G]
        this.displayInfo = String.format("%s (%s) [%d G]", name, syntax, price);
    }

    // --- Getter & Setter 區 (封裝特性 Encapsulation) ---

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayInfo() {
        return displayInfo;
    }

    public void setDisplayInfo(String displayInfo) {
        this.displayInfo = displayInfo;
    }

    @Override
    public String toString() {
        return displayInfo;
    }
}