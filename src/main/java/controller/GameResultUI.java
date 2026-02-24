package controller;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import model.Member;

/**
 * [Controller/UI] GameResultUI - 遊戲結算視窗
 * 職責：顯示當局遊戲的結算結果，並提供排行榜入口。
 * 🛠️ 修正重點：
 * 呼叫 RankingUI 時，傳入當前的 member 物件，解決參數不匹配導致的紅線問題。
 */
public class GameResultUI extends JFrame {

    private Member currentMember;
    private int finalGold;
    private String purchases;

    public GameResultUI(Member member, int gold, String items) {
        this.currentMember = member;
        this.finalGold = gold;
        this.purchases = items;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("《Crazy Bookkeeper》 審計終局報告");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(Color.WHITE);
        setContentPane(contentPane);

        // 標題
        JLabel lblTitle = new JLabel("記帳士審計結算", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Microsoft JhengHei", Font.BOLD, 24));
        lblTitle.setBorder(new EmptyBorder(30, 0, 10, 0));
        contentPane.add(lblTitle, BorderLayout.NORTH);

        // 數據內容
        JPanel pnlData = new JPanel();
        pnlData.setLayout(new BoxLayout(pnlData, BoxLayout.Y_AXIS));
        pnlData.setOpaque(false);
        pnlData.setBorder(new EmptyBorder(20, 50, 20, 50));

        addInfo(pnlData, "👤 記帳士姓名：", currentMember.getNickname());
        addInfo(pnlData, "💰 最終結算金幣：", finalGold + " G");
        
        JLabel lblItemTitle = new JLabel("📦 本局採購物資：");
        lblItemTitle.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        lblItemTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlData.add(lblItemTitle);

        JTextArea txtItems = new JTextArea(purchases);
        txtItems.setLineWrap(true);
        txtItems.setWrapStyleWord(true);
        txtItems.setEditable(false);
        txtItems.setBackground(new Color(245, 245, 245));
        txtItems.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(txtItems);
        scroll.setPreferredSize(new Dimension(300, 80));
        pnlData.add(Box.createVerticalStrut(5));
        pnlData.add(scroll);

        contentPane.add(pnlData, BorderLayout.CENTER);

        // 按鈕區
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 30));
        pnlBtns.setOpaque(false);

        JButton btnRanking = new JButton("檢視排行榜");
        styleButton(btnRanking);
        btnRanking.addActionListener(e -> {
            // 🛡️ 核心修正：傳入 currentMember 確保權限與語法正確
            new RankingUI(currentMember).setVisible(true);
            this.dispose();
        });

        JButton btnExit = new JButton("返回登入");
        styleButton(btnExit);
        btnExit.addActionListener(e -> {
            new LoginUI().setVisible(true);
            dispose();
        });

        pnlBtns.add(btnRanking);
        pnlBtns.add(btnExit);
        contentPane.add(pnlBtns, BorderLayout.SOUTH);
    }

    private void addInfo(JPanel pnl, String title, String val) {
        JLabel lbl = new JLabel(title + val);
        lbl.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 16));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnl.add(lbl);
        pnl.add(Box.createVerticalStrut(15));
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        btn.setPreferredSize(new Dimension(130, 35));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(new Color(220, 220, 220)));
    }
}