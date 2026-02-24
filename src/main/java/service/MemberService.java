package service;

import dao.MemberDAO;
import dao.impl.MemberDAOImpl;
import model.Member;

/**
 * [Service] MemberService - 記帳士邏輯服務 (Class)
 * 職責：處理記帳士的【身分驗證】與【註冊刻印】邏輯。
 * 核心教學語法：
 * 1. 【物件封裝 (Encapsulation)】：將數據與邏輯分離。
 * 2. 【分流判定 (Conditional Logic)】：區分一般記帳士與大稽核官。
 */
public class MemberService {
    
    // 技能：【契約守衛 (DAO Injection)】
    private MemberDAO memberDao = new MemberDAOImpl();

    /**
     * 技能：【靈魂驗證 (Member Login Logic)】
     * 任務：驗證帳密並判定其身分標籤。
     * @param username 帳號
     * @param password 密碼
     * @return 成功則回傳 Member 物件，失敗則回傳 null
     */
    public Member login(String username, String password) {
        System.out.println(">>> [邏輯處理] 正在進行【靈魂驗證 (Member.login)】...");
        
        // 呼叫 DAO 進行資料庫實體查證
        Member member = memberDao.login(username, password);
        
        if (member != null) {
            // 判定權限標籤
            if ("LEGENDARY".equals(member.getRole())) {
                System.out.println(">>> [權限確認] 偵測到【傳說級】大稽核官權限。");
            } else {
                System.out.println(">>> [權限確認] 偵測到【一般級】記帳士身分。");
            }
        } else {
            System.err.println("!!! [驗證失敗] 靈魂頻率不匹配，契約無效。");
        }
        
        return member;
    }

    /**
     * 技能：【血脈刻印邏輯 (Registration Logic)】
     * 任務：處理新任記帳士的入冊流程。
     * @param member 包含註冊資訊的成員物件
     */
    public void register(Member member) {
        // 可以在此加入額外的業務邏輯判定（例如：帳號長度校驗）
        if (member.getUsername() == null || member.getUsername().length() < 3) {
            System.err.println("!!! [刻印受阻] 帳號長度不足，無法寫入名冊。");
            return;
        }
        
        System.out.println(">>> [註冊處理] 正在為 " + member.getNickname() + " 進行血脈刻印...");
        memberDao.register(member);
    }
}