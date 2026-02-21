package dao;

import model.Member;

/**
 * [Interface] MemberDAO - 記帳士契約介面
 * 職責：定義與記帳士靈魂資料相關的存取規範。
 */
public interface MemberDAO {
    // 技能：【契約驗證 (Login)】
    Member login(String username, String password);
    
    // 技能：【新任刻印 (Register)】
    void register(Member member);
    
    // 技能：【資產同步 (Update Gold)】
    void updateGold(int memberId, int gold);
    
    // 技能：【萬象歸零 (Delete)】- 大稽核官專用
    void deleteMember(int memberId);
}