package dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import dao.ShopDAO;
import model.ShopItem;
import util.Tool;

/**
 * [DAO Impl] ShopDAOImpl - 奧術商店數據存取實作 (Class)
 * 職責：負責與 `shop_items` 表格進行互動，處理公會物資的供應與庫存變動。
 * 核心教學語法：JDBC 查詢與更新、ArrayList 集合運用、SQL 參數綁定。
 */
public class ShopDAOImpl implements ShopDAO {

    /**
     * 技能：【奧術物資全域掃描 (SELECT * FROM shop_items)】
     * 術語對應：ResultSet.next() 與 ArrayList.add()
     * 任務：從商店位面提取所有可用物資清單。
     */
    @Override
    public List<ShopItem> queryAll() {
        List<ShopItem> list = new ArrayList<>();
        String sql = "SELECT * FROM shop_items";
        
        try (Connection conn = Tool.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                // 執行技能：【物資實體映射 (Entity Mapping)】
                ShopItem item = new ShopItem();
                item.setId(rs.getInt("id"));
                item.setItemName(rs.getString("item_name"));
                item.setPrice(rs.getInt("price"));
                item.setStock(rs.getInt("stock")); // 讀取當前庫存量
                item.setDescription(rs.getString("description")); // 包含 Java 語法註記
                
                list.add(item);
            }
            System.out.println(">>> [商店掃描] 已提取 " + list.size() + " 項奧術物資清單。");
            
        } catch (SQLException e) {
            System.err.println("!!! [掃描反噬] 無法讀取商店物資，請確認 SQL 表格 `shop_items` 是否存在。");
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 技能：【指定物資鑑定 (SELECT BY ID)】
     */
    @Override
    public ShopItem queryById(int id) {
        String sql = "SELECT * FROM shop_items WHERE id = ?";
        try (Connection conn = Tool.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new ShopItem(
                        rs.getInt("id"),
                        rs.getString("item_name"),
                        rs.getInt("price"),
                        rs.getInt("stock"),
                        rs.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("!!! [鑑定失敗] 目標物資 ID " + id + " 於維度中消失。");
        }
        return null;
    }

    /**
     * 技能：【庫存配給變更 (UPDATE shop_items SET stock = stock + ?)】
     * 術語對應：PreparedStatement.executeUpdate()
     * 任務：結帳時扣除庫存（傳入負值）或進貨時增加庫存。
     * @param itemId 物資 ID
     * @param amount 變動數量 (例如: -1 代表扣除一件)
     */
    @Override
    public void updateStock(int itemId, int amount) {
        String sql = "UPDATE shop_items SET stock = stock + ? WHERE id = ?";
        
        try (Connection conn = Tool.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, amount); // 傳入 -1 執行扣除
            pstmt.setInt(2, itemId);
            
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                System.out.println(">>> [庫存同步] 物資 ID " + itemId + " 數量已變動 " + amount + " (executeUpdate)。");
            }
            
        } catch (SQLException e) {
            System.err.println("!!! [更新失敗] 庫存同步過程遭到資料庫拒絕。");
            e.printStackTrace();
        }
    }
}