package dao.impl;

import java.sql.*;
import model.Adventurer;
import util.Tool;

/**
 * [DAO Impl] AdventurerDAOImpl - 冒險者原型存取
 */
public class AdventurerDAOImpl {
    
    /**
     * 技能：【隨機召募 (SELECT ORDER BY RAND)】
     */
    public Adventurer getRandomAdventurer() {
        String sql = "SELECT * FROM adventurer_types ORDER BY RAND() LIMIT 1";
        try (Connection conn = Tool.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return new Adventurer(
                    rs.getString("type_name"),
                    rs.getInt("stress_gain")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}