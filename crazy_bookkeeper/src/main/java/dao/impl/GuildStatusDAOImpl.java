package dao.impl;

import java.sql.*;
import model.GuildStatus;
import util.DbHelper;

/**
 * [DAO Impl] GuildStatusDAOImpl - 金庫狀態數據存取實作
 * 職責：管理記帳士的金庫資產。
 * 🛠️ 修正筆記：
 * 1. 新增 resetGold() 技能，用於遊戲結束時將金幣歸零 (單局結算制)。
 */
public class GuildStatusDAOImpl {

    public void updateGold(int memberId, int amount) {
        String sql = "UPDATE member SET total_gold = total_gold + ? WHERE id = ?";
        try (Connection conn = DbHelper.getDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) return;
            pstmt.setInt(1, amount);
            pstmt.setInt(2, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public GuildStatus queryByMemberId(int memberId) {
        String sql = "SELECT total_gold FROM member WHERE id = ?";
        try (Connection conn = DbHelper.getDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) return null;
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    GuildStatus status = new GuildStatus();
                    status.setMemberId(memberId);
                    status.setCurrentGold(rs.getInt("total_gold"));
                    return status;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getCurrentGold(int memberId) {
        String sql = "SELECT total_gold FROM member WHERE id = ?";
        try (Connection conn = DbHelper.getDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) return 0;
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_gold");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 🛡️ 技能：【萬象歸零 (Reset Gold)】
     * 任務：在結算或中途登出後，將金幣強制重置為 0，確保每次上哨都是全新開始。
     */
    public void resetGold(int memberId) {
        String sql = "UPDATE member SET total_gold = 0 WHERE id = ?";
        try (Connection conn = DbHelper.getDb();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) return;
            pstmt.setInt(1, memberId);
            pstmt.executeUpdate();
            System.out.println(">>> [資料庫] 記帳士 ID " + memberId + " 的金庫已歸零。");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}