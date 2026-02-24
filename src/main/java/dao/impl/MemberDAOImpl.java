package dao.impl;

import java.sql.*;
import config.GameConfig;
import dao.MemberDAO;
import model.Member;
import util.Tool;

/**
 * [DAO Impl] MemberDAOImpl - 記帳士數據存取實作 (Class)
 * 職責：透過 JDBC 進行資料庫實體操作，實現【雙身分登入】與【資料持久化】。
 * 核心教學語法：PreparedStatement (預編譯語法), ResultSet (結果集匯整)。
 */
public class MemberDAOImpl implements MemberDAO {

    /**
     * 技能：【契約驗證 (SELECT FROM member)】
     * 術語對應：PreparedStatement.executeQuery()
     */
    @Override
    public Member login(String username, String password) {
        String sql = "SELECT * FROM member WHERE username = ? AND password = ?";
        
        // 使用 try-with-resources 確保資源自動釋放 (AutoCloseable)
        try (Connection conn = Tool.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Member m = new Member();
                    // 執行技能：【靈魂封裝 (Data Mapping)】
                    m.setId(rs.getInt("id"));
                    m.setUsername(rs.getString("username"));
                    m.setPassword(rs.getString("password"));
                    m.setNickname(rs.getString("nickname"));
                    m.setRole(rs.getString("role")); // 區分 NORMAL 或 LEGENDARY
                    m.setTotalGold(rs.getInt("total_gold"));
                    
                    System.out.println(">>> [契約成立] 偵測到身分標籤：" + m.getRole() + " (ResultSet.getString)");
                    return m;
                }
            }
        } catch (SQLException e) {
            System.err.println("!!! [奧術崩潰] 登入驗證時發生異常 (SQLException) !!!");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 技能：【新任刻印 (INSERT INTO member)】
     * 術語對應：PreparedStatement.executeUpdate()
     */
    @Override
    public void register(Member m) {
        String sql = "INSERT INTO member (username, password, nickname, role, total_gold) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = Tool.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, m.getUsername());
            pstmt.setString(2, m.getPassword());
            pstmt.setString(3, m.getNickname());
            pstmt.setString(4, Member.ROLE_NORMAL); // 預設新任皆為一般級
            pstmt.setInt(5, 0);
            
            pstmt.executeUpdate();
            System.out.println(">>> [刻印完成] 新任記帳士 " + m.getNickname() + " 已加入名冊。");
            
        } catch (SQLException e) {
            System.err.println("!!! [刻印失敗] 帳號可能已存在或連線中斷。");
        }
    }

    /**
     * 技能：【資產同步 (UPDATE total_gold)】
     */
    @Override
    public void updateGold(int memberId, int gold) {
        String sql = "UPDATE member SET total_gold = total_gold + ? WHERE id = ?";
        
        try (Connection conn = Tool.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, gold);
            pstmt.setInt(2, memberId);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 技能：【萬象歸零 (DELETE FROM member)】
     * 職責：大稽核官專屬權能，從根源抹除記帳士的存在。
     */
    @Override
    public void deleteMember(int memberId) {
        String sql = "DELETE FROM member WHERE id = ?";
        
        try (Connection conn = Tool.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, memberId);
            int affected = pstmt.executeUpdate();
            
            if (affected > 0) {
                System.out.println(">>> [萬象歸零] 目標 ID " + memberId + " 已從因果律中抹除 (executeUpdate)。");
            }
            
        } catch (SQLException e) {
            System.err.println("!!! [抹除失敗] 目標具備強大的命運抗性 (Foreign Key Constraint)。");
        }
    }
}