package dao.impl;

import java.sql.*;
import model.Loot;
import util.Tool;

/**
 * [DAO Impl] ItemTemplateDAOImpl - 物品原型存取
 */
public class ItemTemplateDAOImpl {

    /**
     * 技能：【隨機掉落 (SELECT ORDER BY RAND)】
     */
    public Loot getRandomLootTemplate() {
        String sql = "SELECT * FROM item_templates ORDER BY RAND() LIMIT 1";
        try (Connection conn = Tool.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                Loot l = new Loot();
                l.setItemName(rs.getString("template_name"));
                l.setItemValue(rs.getInt("base_value"));
                l.setGrade(rs.getString("grade"));
                l.setIllegal(rs.getBoolean("is_illegal"));
                return l;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}