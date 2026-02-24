package controller;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import model.Member;
import dao.impl.MemberDAOImpl;
import dao.impl.ShopDAOImpl;

/**
 * [Controller/UI] AdminDashboardUI - 大稽核官極簡管理中心 (最新整合版)
 * 職責：管理員專用的核心決策介面。
 * 🛠️ 設計更新：
 * 1. 極簡美學：純白背景、炭灰文字、1px 淺灰邊框。
 * 2. 視窗連動：跳轉視窗時自動 dispose()，防止視窗堆疊。
 * 3. 權限傳遞：持有 Member 物件並傳遞給 RankingUI。
 * 4. 佈局優化：固定按鈕尺寸，解決視覺不穩定問題。
 */
public class AdminDashboardUI extends JFrame {

    private Member adminMember;
    private MemberDAOImpl memberDao = new MemberDAOImpl();
    private ShopDAOImpl shopDao = new ShopDAOImpl();

    /**
     * 建構子 - 接收登入的管理員資料
     */
    public AdminDashboardUI(Member member) {
        this.adminMember = member;
        // 如果傳入 null (例如直接執行)，則初始化一個預設 Admin 物件
        if (this.adminMember == null) {
            this.adminMember = new Member();
            this.adminMember.setRole("admin");
            this.adminMember.setUsername("admin");
        }
        initializeUI();
    }

    /**
     * 預設建構子 - 解決編譯紅線
     */
    public AdminDashboardUI() {
        this(null);
    }

    private void initializeUI() {
        setTitle("Crazy Bookkeeper - Admin Center");
        setSize(480, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 主容器：純白色系
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(Color.WHITE);
        setContentPane(contentPane);

        // --- 1. 簡約標題區 ---
        JPanel pnlHeader = new JPanel(new GridBagLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setPreferredSize(new Dimension(480, 150));
        pnlHeader.setBorder(new MatteBorder(0, 0, 1, 0, new Color(235, 235, 235)));
        
        JLabel lblTitle = new JLabel("管理指揮中心");
        lblTitle.setFont(new Font("Microsoft JhengHei", Font.BOLD, 22));
        lblTitle.setForeground(new Color(40, 40, 40));
        pnlHeader.add(lblTitle);

        contentPane.add(pnlHeader, BorderLayout.NORTH);

        // --- 2. 功能按鈕區 ---
        JPanel pnlButtons = new JPanel();
        pnlButtons.setOpaque(false);
        pnlButtons.setBorder(new EmptyBorder(40, 70, 40, 70));
        pnlButtons.setLayout(new GridLayout(4, 1, 0, 25)); // 4個按鈕，間距 25

        // 按鈕 A: 修改祕法題庫
        JButton btnEditInsight = new JButton("修改祕法題庫");
        styleButton(btnEditInsight);
        btnEditInsight.addActionListener(e -> {
            new InsightBookEditorUI().setVisible(true);
            this.dispose(); // 跳轉後自毀
        });
        pnlButtons.add(btnEditInsight);

        // 按鈕 B: 傳說排行榜
        JButton btnRanking = new JButton("排行榜維護");
        styleButton(btnRanking);
        btnRanking.addActionListener(e -> {
            new RankingUI(adminMember).setVisible(true);
            this.dispose(); // 跳轉後自毀
        });
        pnlButtons.add(btnRanking);

        // 按鈕 C: 系統數據重置
        JButton btnReset = new JButton("系統數據重置");
        styleButton(btnReset);
        btnReset.addActionListener(this::handleSystemReset);
        pnlButtons.add(btnReset);

        // 按鈕 D: 安全登出
        JButton btnExit = new JButton("安全登出系統");
        styleButton(btnExit);
        btnExit.addActionListener(e -> {
            new LoginUI().setVisible(true);
            this.dispose(); // 返回後自毀
        });
        pnlButtons.add(btnExit);

        contentPane.add(pnlButtons, BorderLayout.CENTER);

        // --- 3. 底部資訊欄 ---
        JLabel lblFooter = new JLabel("System Management Module v2.2.1", SwingConstants.CENTER);
        lblFooter.setForeground(new Color(190, 190, 190));
        lblFooter.setFont(new Font("Arial", Font.PLAIN, 10));
        lblFooter.setBorder(new EmptyBorder(0, 0, 20, 0));
        contentPane.add(lblFooter, BorderLayout.SOUTH);
    }

    /**
     * 極簡風格按鈕：淺灰底、炭灰字、細邊框
     * 💡 核心修正：強制固定 PreferredSize 防止按鈕尺寸跳動
     */
    private void styleButton(JButton btn) {
        btn.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 16));
        btn.setBackground(new Color(252, 252, 252));
        btn.setForeground(new Color(60, 60, 60));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 鎖定尺寸 (寬 180, 高 45)
        btn.setPreferredSize(new Dimension(180, 45));
        
        // 設定極簡邊框
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(225, 225, 225), 1),
            new EmptyBorder(5, 5, 5, 5)
        ));

        // 交互回饋
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(245, 245, 245));
                btn.setBorder(new LineBorder(new Color(200, 200, 200), 1));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(252, 252, 252));
                btn.setBorder(new LineBorder(new Color(225, 225, 225), 1));
            }
        });
    }

    /**
     * 處理系統重置指令
     */
    private void handleSystemReset(ActionEvent e) {
        String input = JOptionPane.showInputDialog(this, "請輸入指令 (如 'ALL') 或成員 ID：");
        if (input == null || input.trim().isEmpty()) return;

        try {
            if ("ALL".equalsIgnoreCase(input)) {
                shopDao.resetAllStock();
                JOptionPane.showMessageDialog(this, "奧術商店庫存已全面補貨重置。");
            } else {
                int id = Integer.parseInt(input);
                int confirm = JOptionPane.showConfirmDialog(this, "確定要刪除 ID 為 " + id + " 的成員嗎？", "數據抹除確認", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    memberDao.deleteMember(id);
                    JOptionPane.showMessageDialog(this, "目標數據已從核心名冊中移除。");
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "輸入格式不正確，請輸入數字 ID 或 'ALL'。");
        }
    }

    public static void main(String[] args) {
        // 設定為系統原生風格，確保元件渲染正確
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        
        SwingUtilities.invokeLater(() -> new AdminDashboardUI().setVisible(true));
    }
}