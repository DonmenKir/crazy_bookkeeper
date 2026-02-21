package model;

import java.io.Serializable;

/**
 * [Model] ShopItem - 奧術商店物資 (實體模型)
 * 具象化角色：【公會採購清單中的初始物資】
 * * * 秘法特性：
 * 1. 映射資料庫 `shop_items` 表格，包含初始庫存量控制。
 * 2. 實作 Serializable 介面，支援技能：【靈魂封印卷軸 (Serialization)】。
 */
public class ShopItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String itemName;
    private Integer price;
    
    /** * 初始數量 (Initial Stock) 
     * 技能對應：【物資配給制 (Inventory Management)】
     */
    private Integer stock; 
    
    /** * 效果說明 
     * 技能對應：【奧術解析 (Internal Documentation)】
     * 內容將包含如 "ProgressBar.setValue" 等 Java 語法對照。
     */
    private String description;

    public ShopItem() {}

    /**
     * 全參數建構子：用於從資料庫獲取完整物資資訊
     */
    public ShopItem(Integer id, String itemName, Integer price, Integer stock, String description) {
        this.id = id;
        this.itemName = itemName;
        this.price = price;
        this.stock = stock;
        this.description = description;
    }

    // --- Getter & Setter 區 (封裝特性 Encapsulation) ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 技能：【奧術投影 (toString)】
     * 用於除錯或日誌紀錄時顯示物資概況。
     */
    @Override
    public String toString() {
        return String.format("ShopItem [名稱=%s, 價格=%d G, 庫存=%d, 敘述=%s]", 
                itemName, price, stock, description);
    }
}