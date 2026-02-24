package dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.ShopItem;
import util.DbHelper;

/**
 * [DAO Impl] ShopDAOImpl - 商店數據存取實作
 * 🛠️ 修正筆記：
 * 1. 新增 resetAllStock()：確保每場遊戲結束後，庫存能恢復到預設值（如 5 份）。
 */
public class ShopDAOImpl {

    public List<ShopItem> queryAll() {
        List<ShopItem> list = new ArrayList<>();
        String sql = "SELECT * FROM shop_items";
        try (Connection conn = DbHelper.getDb();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ShopItem item = new ShopItem();
                item.setId(rs.getInt("id"));
                item.setItemName(rs.getString("item_name"));
                item.setPrice(rs.getInt("price"));
                item.setStock(rs.getInt("stock"));
                item.setDescription(rs.getString("description"));
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateStock(int itemId, int amount) {
        String sql = "UPDATE shop_items SET stock = stock + ? WHERE id = ?";
        try (Connection conn = DbHelper.getDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, amount);
            pstmt.setInt(2, itemId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 🛡️ 技能：【奧術補貨 (Reset All Stock)】
     * 任務：將商店所有商品的庫存重置為預設值。
     * 在 Roguelike 機制中，確保下一位記帳士或下一局遊戲有充足物資。
     */
    public void resetAllStock() {
        // 假設預設初始庫存均為 5
        String sql = "UPDATE shop_items SET stock = 5"; 
        try (Connection conn = DbHelper.getDb();
             Statement stmt = conn.createStatement()) {
            if (conn == null) return;
            stmt.executeUpdate(sql);
            System.out.println(">>> [資料庫] 奧術商店物資已重新補給完畢。");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}