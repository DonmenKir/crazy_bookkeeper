package vo;

import java.io.Serializable;

/**
 * [VO] MemberVO - 記帳士數據載體 (Class)
 * 實作靈魂通行證 (Serializable - Interface) 來自 java.io
 */
public class MemberVO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    private String username;
    private String password;
    private String nickname;
    private Integer totalGold;

    public MemberVO() {}

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public Integer getTotalGold() { return totalGold; }
    public void setTotalGold(Integer totalGold) { this.totalGold = totalGold; }
}