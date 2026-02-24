package dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import dao.LootDAO;
import model.Loot;
import util.Tool;

/**
 * [DAO Impl] LootDAOImpl - 戰利品數據存取實作 (Class)
 * 職責：負責與資料庫 `loot` 表格進行實體互動，支援【黑暗魔法卷軸】與【傳說品級】的持久化。
 * 核心教學語法：PreparedStatement (預編譯語法), ResultSet Mapping (資料映射術), JDBC 資源管理。
 */
public class LootDAOImpl implements LootDAO {

    /**
     * 技能：【資產入庫 (INSERT INTO loot)】
     * 語法標註：PreparedStatement.executeUpdate()
     */
    @Override
    public void add(Loot loot) {
        String sql = "INSERT INTO loot (item_name, item_value, grade, is_illegal, member_id, create_time) " +
                     "VALUES (?, ?, ?, ?, ?, NOW())";
        
        try (Connection conn = Tool.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, loot.getItemName());
            pstmt.setInt(2, loot.getItemValue());
            pstmt.setString(3, loot.getGrade());     // 技能：【品級標記 (String)】
            pstmt.setBoolean(4, loot.isIllegal());  // 技能：【黑暗標籤 (Boolean to TinyInt)】
            pstmt.setInt(5, loot.getMemberId());
            
            pstmt.executeUpdate();
            System.out.println(">>> [資產入庫] 戰利品 [" + loot.getItemName() + "] 已刻印至王國資料庫。");
            
        } catch (SQLException e) {
            System.err.println("!!! [入庫失敗] 奧術連線中斷或 SQL 語法反噬。");
            e.printStackTrace();
        }
    }

    /**
     * 技能：【全域審計掃描 (SELECT * FROM loot)】
     * 用途：王國查帳服務 (KingdomAuditService) 專用，掃描所有存放在資料庫的非法物。
     */
    @Override
    public List<Loot> queryAll() {
        List<Loot> list = new ArrayList<>();
        String sql = "SELECT * FROM loot";
        
        try (Connection conn = Tool.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapRowToLoot(rs));
            }
            System.out.println(">>> [查帳掃描] 已從位面提取 " + list.size() + " 件資產進行稽核。");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 技能：【個人資產清查 (SELECT BY member_id)】
     */
    @Override
    public List<Loot> queryByMember(int memberId) {
        List<Loot> list = new ArrayList<>();
        String sql = "SELECT * FROM loot WHERE member_id = ? ORDER BY create_time DESC";
        
        try (Connection conn = Tool.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToLoot(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 技能：【時空屬性更迭 (UPDATE loot)】
     * 語法標註：用於【時空洗白】後同步資料庫狀態。
     */
    @Override
    public void update(Loot loot) {
        String sql = "UPDATE loot SET item_name=?, item_value=?, grade=?, is_illegal=? WHERE id=?";
        
        try (Connection conn = Tool.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, loot.getItemName());
            pstmt.setInt(2, loot.getItemValue());
            pstmt.setString(3, loot.getGrade());
            pstmt.setBoolean(4, loot.isIllegal());
            pstmt.setInt(5, loot.getId());
            
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                System.out.println(">>> [資產更新] ID " + loot.getId() + " 之屬性已完成【洗白/純化】更新。");
            }
            
        } catch (SQLException e) {
            System.err.println("!!! [更新失敗] 試圖修改資產屬性時遭到資料庫拒絕。");
        }
    }

    /**
     * 技能：【資產物理抹除 (DELETE FROM loot)】
     * 語法標註：用於【結算】或【王國沒收】後移除資料。
     */
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM loot WHERE id = ?";
        
        try (Connection conn = Tool.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 秘法助手：【資料映射術 (ResultSet to Model)】
     * 職責：將資料庫的 Row 轉換為 Java 物件實體。
     */
    private Loot mapRowToLoot(ResultSet rs) throws SQLException {
        Loot l = new Loot();
        l.setId(rs.getInt("id"));
        l.setItemName(rs.getString("item_name"));
        l.setItemValue(rs.getInt("item_value"));
        l.setGrade(rs.getString("grade"));      // 對接：Loot.setGrade()
        l.setIllegal(rs.getBoolean("is_illegal")); // 對接：Loot.setIllegal()
        l.setMemberId(rs.getInt("member_id"));
        
        // 技能：【時空格式轉換 (Timestamp to LocalDateTime)】
        Timestamp ts = rs.getTimestamp("create_time");
        if (ts != null) {
            l.setCreateTime(ts.toLocalDateTime());
        }
        return l;
    }
}