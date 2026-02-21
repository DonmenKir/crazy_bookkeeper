package model;

import java.io.Serializable;

/**
 * [Model] Member - 記帳士靈魂實體 (Entity)
 * 職責：與資料庫 member 資料表 1:1 精確映射。
 * * 🔮 秘法特性：
 * 1. 實作 Serializable：支援【靈魂封印卷軸】存檔與讀取。
 * 2. 內建身分印記：區分一般記帳士與大稽核官。
 * 3. 邏輯偵測術：內建 isLegendary() 判定，保護指揮塔安全。
 */
public class Member implements Serializable {
    private static final long serialVersionUID = 1L;

    // --- 秘法身分常數 (Constants) ---
    /** 一般記帳士：負責日常指揮塔運作 */
    public static final String ROLE_NORMAL = "NORMAL";
    /** 傳說級大稽核官：具備管理員權限，可視察所有帳目 */
    public static final String ROLE_LEGENDARY = "LEGENDARY";

    // --- 資料庫欄位對應 (Database Columns) ---
    private Integer id;           // 靈魂序號 (Primary Key)
    private String username;      // 登入帳號
    private String password;      // 靈魂咒文 (密碼)
    private String nickname;      // 記帳士稱號
    private Integer totalGold;    // 累計總資產 (Total Gold)
    private String role;          // 身分印記 (NORMAL / LEGENDARY)

    /**
     * 預設建構子：用於反射機制與資料庫查詢
     */
    public Member() {
        this.totalGold = 0;
        this.role = ROLE_NORMAL;
    }

    /**
     * 修正建構子：對應 GameMainUI 與 LoginUI 初始化
     * 用於新任記帳士入冊。
     */
    public Member(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.totalGold = 0;
        this.role = ROLE_NORMAL; // 預設新入冊者為一般級
    }

    // --- 秘法判斷邏輯 (Logic Helpers) ---

    /**
     * 偵測是否具備【大稽核官】權限
     * 用於 LoginUI 的分流判定。
     */
    public boolean isLegendary() {
        return ROLE_LEGENDARY.equals(this.role);
    }

    // --- Getter & Setter 區 (封裝術) ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getTotalGold() {
        return totalGold;
    }

    public void setTotalGold(Integer totalGold) {
        this.totalGold = totalGold;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return String.format("Member [稱號=%s, 身分=%s, 金幣=%d]", nickname, role, totalGold);
    }
}