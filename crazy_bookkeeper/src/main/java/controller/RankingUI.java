package controller;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import model.Score;
import util.ExcelUtil;

/**
 * [Controller/View] RankingUI - 傳說排行榜 (Ranking Board)
 * 修正筆記：
 * 1. 採用標準 JTable 呈現 Excel 數據，確保在 WindowBuilder 中易於維護。
 * 2. 移除所有按鈕配色與特效，回歸系統原生樣式，解決字體模糊問題。
 * 3. 整合 ExcelUtil.loadAllScores()，實現數據的自動載入與排序顯示。
 */
public class RankingUI extends JFrame {

    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel tableModel;

    /**
     * 建構子：初始化排行榜
     */
    public RankingUI() {
        initializeUI();
        loadRankingData();
    }

    private void initializeUI() {
        setTitle("《Crazy Bookkeeper》 王國傳說排行榜");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(245, 245, 250));
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPane.setLayout(new BorderLayout(0, 15));
        setContentPane(contentPane);

        // --- 1. 標題區 ---
        JPanel panelNorth = new JPanel(new GridLayout(2, 1));
        panelNorth.setOpaque(false);

        JLabel lblHeader = new JLabel("📜 傳說記帳士卷軸", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Microsoft JhengHei", Font.BOLD, 28));
        lblHeader.setForeground(new Color(25, 25, 112));
        panelNorth.add(lblHeader);

        JLabel lblSub = new JLabel("記錄於王國史詩中的輝煌審計戰績", SwingConstants.CENTER);
        lblSub.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        lblSub.setForeground(Color.GRAY);
        panelNorth.add(lblSub);

        contentPane.add(panelNorth, BorderLayout.NORTH);

        // --- 2. 排行表格區 (使用 JTable) ---
        String[] columnNames = {"排名 (Rank)", "記帳士稱號", "最終金幣 (Gold)", "採購清單", "結算時間"};
        
        // 設定表格不可編輯
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        
        // 設定欄位寬度比例
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(350);
        table.getColumnModel().getColumn(4).setPreferredWidth(180);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("實體卷軸紀錄 (Ranking Data)"));
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // --- 3. 底部按鈕區 (標準樣式) ---
        JPanel panelSouth = new JPanel();
        panelSouth.setOpaque(false);
        panelSouth.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // 標準按鈕，確保文字清晰
        JButton btnRefresh = new JButton("🔄 重新掃描卷軸 (Refresh)");
        btnRefresh.setFont(new Font("Microsoft JhengHei", Font.BOLD, 16));
        btnRefresh.setPreferredSize(new Dimension(220, 45));
        btnRefresh.addActionListener(e -> loadRankingData());
        panelSouth.add(btnRefresh);

        JButton btnBack = new JButton("🚪 返回登入大門 (Back)");
        btnBack.setFont(new Font("Microsoft JhengHei", Font.BOLD, 16));
        btnBack.setPreferredSize(new Dimension(220, 45));
        btnBack.addActionListener(e -> {
            new LoginUI().setVisible(true);
            dispose();
        });
        panelSouth.add(btnBack);

        contentPane.add(panelSouth, BorderLayout.SOUTH);
    }

    /**
     * 技能：【史詩回溯 (Load Ranking)】
     * 從 Excel 檔案讀取數據並填入表格。
     */
    private void loadRankingData() {
        tableModel.setRowCount(0); // 清空舊顯示
        
        // 呼叫 ExcelUtil 讀取所有戰績
        List<Score> scores = ExcelUtil.loadAllScores();
        
        // 依照金幣進行降序排列 (由高到低)
        scores.sort((s1, s2) -> s2.getFinalGold().compareTo(s1.getFinalGold()));

        int rank = 1;
        for (Score s : scores) {
            Object[] rowData = {
                rank++,
                s.getNickname(),
                s.getFinalGold() + " G",
                s.getPurchasedItems(),
                s.getFormatTime()
            };
            tableModel.addRow(rowData);
        }
        
        if (scores.isEmpty()) {
            System.out.println(">>> [系統] 尚未偵測到任何傳說紀錄。");
        } else {
            System.out.println(">>> [系統] 已成功載入 " + scores.size() + " 筆傳說紀錄。");
        }
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
                new RankingUI().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}