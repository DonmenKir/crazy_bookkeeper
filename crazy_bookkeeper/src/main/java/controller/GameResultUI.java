package controller;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import model.Member;
import model.Score;
import util.ExcelUtil;

/**
 * [Controller/View] GameResultUI - 最終審計報告 (Game Result)
 * 修正筆記：
 * 1. 移除所有自定義按鈕特效，回歸標準系統樣式，確保文字清晰。
 * 2. 整合 ExcelUtil 持久化邏輯，在介面開啟時自動存檔。
 * 3. 使用分層佈局 (BorderLayout & GridLayout)，優化 WindowBuilder 相容性。
 */
public class GameResultUI extends JFrame {

    private JPanel contentPane;
    private Member member;
    private int finalGold;
    private String purchasedItems;

    /**
     * 建構子：接收遊戲結束時的最終數據
     * @param member 記帳士實體
     * @param finalGold 最終獲得金幣
     * @param purchases 遊戲過程中採購的清單敘述
     */
    public GameResultUI(Member member, int finalGold, String purchases) {
        this.member = member;
        this.finalGold = finalGold;
        this.purchasedItems = purchases;
        
        // 1. 執行技能：【數據刻印 (Save to Excel)】
        savePerformance();
        
        // 2. 初始化 UI
        initializeUI();
    }

    private void savePerformance() {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Score scoreRecord = new Score(member.getNickname(), finalGold, purchasedItems, currentTime);
        
        // 呼叫工具類別進行 Excel 寫入
        ExcelUtil.saveScore(scoreRecord);
    }

    private void initializeUI() {
        setTitle("《Crazy Bookkeeper》 最終財務審計報告");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(250, 250, 255));
        contentPane.setBorder(new EmptyBorder(25, 25, 25, 25));
        contentPane.setLayout(new BorderLayout(0, 20));
        setContentPane(contentPane);

        // --- 1. 頂部狀態區 ---
        JPanel panelNorth = new JPanel(new GridLayout(2, 1));
        panelNorth.setOpaque(false);
        
        JLabel lblHeader = new JLabel("審計結果：萬象終局", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Microsoft JhengHei", Font.BOLD, 28));
        lblHeader.setForeground(new Color(139, 0, 0)); // 暗紅色標題
        panelNorth.add(lblHeader);

        JLabel lblTime = new JLabel("結算時間：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")), SwingConstants.CENTER);
        lblTime.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 12));
        lblTime.setForeground(Color.GRAY);
        panelNorth.add(lblTime);
        
        contentPane.add(panelNorth, BorderLayout.NORTH);

        // --- 2. 數據呈現區 ---
        JPanel panelCenter = new JPanel();
        panelCenter.setOpaque(false);
        panelCenter.setLayout(new GridLayout(2, 1, 0, 15));

        // 金幣總額顯示
        JPanel panelGold = new JPanel(new BorderLayout());
        panelGold.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        panelGold.setBackground(Color.WHITE);
        
        JLabel lblGoldTitle = new JLabel("最終獲得金幣 (Total Gold)", SwingConstants.CENTER);
        lblGoldTitle.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        panelGold.add(lblGoldTitle, BorderLayout.NORTH);
        
        JLabel lblGoldValue = new JLabel(finalGold + " G", SwingConstants.CENTER);
        lblGoldValue.setFont(new Font("Serif", Font.BOLD, 48));
        lblGoldValue.setForeground(new Color(184, 134, 11)); // 黃金色
        panelGold.add(lblGoldValue, BorderLayout.CENTER);
        
        panelCenter.add(panelGold);

        // 採購明細顯示
        JTextArea txtPurchases = new JTextArea("採購資產明細：\n" + purchasedItems);
        txtPurchases.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        txtPurchases.setLineWrap(true);
        txtPurchases.setWrapStyleWord(true);
        txtPurchases.setEditable(false);
        
        JScrollPane scrollItems = new JScrollPane(txtPurchases);
        scrollItems.setBorder(BorderFactory.createTitledBorder("物資卷軸紀錄"));
        panelCenter.add(scrollItems);

        contentPane.add(panelCenter, BorderLayout.CENTER);

        // --- 3. 底部操作區 (標準按鈕) ---
        JPanel panelSouth = new JPanel();
        panelSouth.setOpaque(false);
        panelSouth.setLayout(new GridLayout(2, 1, 0, 10));

        // 標準按鈕，確保文字清晰
        JButton btnRanking = new JButton("檢視傳說排行榜 (View Rankings)");
        btnRanking.setFont(new Font("Microsoft JhengHei", Font.BOLD, 16));
        btnRanking.setPreferredSize(new Dimension(0, 45));
        btnRanking.addActionListener(e -> {
            new RankingUI().setVisible(true);
            dispose();
        });
        panelSouth.add(btnRanking);

        JButton btnBack = new JButton("解離時空連結 (Return to Login)");
        btnBack.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        btnBack.addActionListener(e -> {
            new LoginUI().setVisible(true);
            dispose();
        });
        panelSouth.add(btnBack);

        contentPane.add(panelSouth, BorderLayout.SOUTH);
    }

    /**
     * 測試預覽
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        EventQueue.invokeLater(() -> {
            try {
                Member m = new Member("test", "123", "測試記帳士");
                new GameResultUI(m, 12500, "奧術冷卻劑 x3, 魔法擴充口袋").setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}