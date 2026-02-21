package controller;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import model.Member;

/**
 * [Controller/View] GameGuideUI - 記帳士生存契約 (Game Guide)
 * 修正筆記：
 * 1. 配合最新版本的遊戲機制，全面更新 HTML 導引說明。
 * 2. 強調「壓力爆表」為唯一的敗北條件。
 * 3. 清楚說明「王國查帳」的預警、沒收與官方帳本罰款機制。
 */
public class GameGuideUI extends JFrame {

    private JPanel contentPane;
    private Member currentMember;

    /**
     * 建構子：接收登入成功的記帳士靈魂
     */
    public GameGuideUI(Member member) {
        this.currentMember = member;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("《Crazy Bookkeeper》 記帳士生存契約");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 750);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(245, 245, 250));
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPane.setLayout(new BorderLayout(0, 15));
        setContentPane(contentPane);

        // --- 1. 標題區 ---
        JLabel lblTitle = new JLabel("記帳士生存契約 (Audit Rules)", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Microsoft JhengHei", Font.BOLD, 24));
        lblTitle.setForeground(new Color(25, 25, 112));
        contentPane.add(lblTitle, BorderLayout.NORTH);

        // --- 2. 內容展示區 (使用 HTML 格式化) ---
        JEditorPane guidePane = new JEditorPane();
        guidePane.setContentType("text/html");
        guidePane.setEditable(false);
        guidePane.setBackground(Color.WHITE);
        
        // 更新為符合最新邏輯的遊戲說明
        String guideHtml = "<html><body style='font-family:Microsoft JhengHei; padding: 15px; font-size: 13px;'>"
                + "<h2 style='color: #2E8B57;'>📜 核心經營機制</h2>"
                + "<ul>"
                + "<li><b>冒險者湧入：</b>每 3 秒會有一位冒險者抵達指揮塔，他們身上可能帶有各種戰利品。</li>"
                + "<li><b>合法結算：</b>點擊 [Accept] 可將資產登錄至「官方帳本」並賺取金幣；不想要的物品可點擊 [Refuse]。</li>"
                + "<li><b>幽影黑帳：</b>遇到帶有 Illegal 的黑暗卷軸，務必點擊 [藏入黑帳] 避風頭，等到安全時再透過 [時空洗白] 轉為合法物品。</li>"
                + "</ul>"
                + "<h2 style='color: #B22222;'>🚨 王國查帳系統</h2>"
                + "<ul>"
                + "<li><b>預警與臨檢：</b>查帳官抵達前會有 10 秒倒數預警。臨檢的 5 秒內，排隊中或手邊的 Illegal 違禁品會被<b>當場沒收</b>並驅離冒險者。</li>"
                + "<li><b>帳本追溯：</b>若你貪圖暴利，直接將 Illegal 物品 [Accept] 寫入官方帳本，查帳官抵達瞬間會查閱紀錄，強制追繳高額罰款並<b>增加 3 點壓力</b>！</li>"
                + "</ul>"
                + "<h2 style='color: #4B0082;'>💀 萬象終局 (唯一敗北條件)</h2>"
                + "<ul>"
                + "<li><b>奧術壓力爆表：</b>當公會的壓力條達到 <b>15 點</b>，記帳士心智崩潰，遊戲直接結束。</li>"
                + "<li><b>壓力來源：</b>冒險者自帶的基礎壓力、每 15 秒【秘法試煉】答錯的反噬 (+3)，以及【官方帳本】被查獲洗錢紀錄的懲罰 (+3)。</li>"
                + "</ul>"
                + "</body></html>";
        
        guidePane.setText(guideHtml);
        
        // 修正：捲動條回到頂部，避免字太多直接捲到底部
        guidePane.setCaretPosition(0); 

        JScrollPane scrollPane = new JScrollPane(guidePane);
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // --- 3. 操作區 ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(1, 1, 0, 0));
        
        // 標準按鈕，確保文字清晰
        JButton btnStart = new JButton("我已熟讀真理，簽署契約 (Enter Command Tower)");
        btnStart.setFont(new Font("Microsoft JhengHei", Font.BOLD, 18));
        btnStart.setPreferredSize(new Dimension(0, 60));
        btnStart.addActionListener(this::handleStartGame);
        
        buttonPanel.add(btnStart);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * 執行技能：【命運連結 (Enter Game)】
     */
    private void handleStartGame(ActionEvent e) {
        // 確保 Member 資料能正確傳遞到主遊戲畫面
        if (currentMember != null) {
            new GameMainUI(currentMember).setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "靈魂連結遺失，請重新登入。", "錯誤", JOptionPane.ERROR_MESSAGE);
            new LoginUI().setVisible(true);
            this.dispose();
        }
    }

    /**
     * 測試預覽用 Main 方法
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        EventQueue.invokeLater(() -> {
            try {
                // 模擬測試資料
                Member testMember = new Member("test", "123", "測試員");
                new GameGuideUI(testMember).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}