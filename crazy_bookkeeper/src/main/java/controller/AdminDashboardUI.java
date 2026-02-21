package controller;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import model.Member;
import model.Score;
import util.ExcelUtil;
import config.GameConfig;

/**
 * [Controller/View] AdminDashboardUI - 大稽核官監控中心 (Admin Dashboard)
 * 修正筆記：
 * 1. 視窗尺寸優化為 1000x650，提供寬敞的表格閱讀空間。
 * 2. 移除所有導致文字模糊的按鈕配色，回歸 Java 11 標準系統樣式。
 * 3. 整合 ExcelUtil，實現排行榜數據的即時讀取與物理刪除功能。
 */
public class AdminDashboardUI extends JFrame {

    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel tableModel;

    /**
     * 建構子：初始化稽核中心介面
     */
    public AdminDashboardUI() {
        initializeUI();
        refreshTableData();
    }

    private void initializeUI() {
        setTitle("《Crazy Bookkeeper》 至高維度：大稽核官監控中心");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(240, 240, 245));
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPane.setLayout(new BorderLayout(0, 20));
        setContentPane(contentPane);

        // --- 1. 頂部標題區 ---
        JPanel panelHeader = new JPanel(new GridLayout(2, 1));
        panelHeader.setOpaque(false);
        
        JLabel lblTitle = new JLabel("王國財務卷軸紀錄 (Audit Leaderboard)", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Microsoft JhengHei", Font.BOLD, 26));
        lblTitle.setForeground(new Color(25, 25, 112));
        panelHeader.add(lblTitle);

        JLabel lblSub = new JLabel("管理員權限：檢視、備份與歸零所有記帳數據", SwingConstants.CENTER);
        lblSub.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        lblSub.setForeground(Color.GRAY);
        panelHeader.add(lblSub);
        
        contentPane.add(panelHeader, BorderLayout.NORTH);

        // --- 2. 資料表格區 ---
        String[] columnNames = {"排名", "記帳士稱號", "最終金幣 (Gold)", "採購明細", "結算時間"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 禁止直接修改表格內容
            }
        };
        
        table = new JTable(tableModel);
        table.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("實體戰績卷軸 (Excel Data)"));
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // --- 3. 底部按鈕區 ---
        JPanel panelFooter = new JPanel();
        panelFooter.setOpaque(false);
        panelFooter.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 10));

        // 標準按鈕，確保文字清晰
        JButton btnRefresh = new JButton("🔄 刷新卷軸 (Refresh)");
        btnRefresh.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        btnRefresh.setPreferredSize(new Dimension(160, 40));
        btnRefresh.addActionListener(e -> refreshTableData());
        panelFooter.add(btnRefresh);

        JButton btnDelete = new JButton("🔥 萬象歸零 (Delete All)");
        btnDelete.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        btnDelete.setPreferredSize(new Dimension(160, 40));
        btnDelete.addActionListener(this::handleClearData);
        panelFooter.add(btnDelete);

        JButton btnLogout = new JButton("🚪 登出系統 (Logout)");
        btnLogout.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        btnLogout.setPreferredSize(new Dimension(120, 40));
        btnLogout.addActionListener(e -> {
            new LoginUI().setVisible(true);
            dispose();
        });
        panelFooter.add(btnLogout);

        contentPane.add(panelFooter, BorderLayout.SOUTH);
    }

    /**
     * 技能：【全域掃描 (Refresh Data)】
     */
    private void refreshTableData() {
        tableModel.setRowCount(0); // 清空舊顯示
        List<Score> scores = ExcelUtil.loadAllScores();
        
        int rank = 1;
        for (Score s : scores) {
            Object[] row = {
                rank++,
                s.getNickname(),
                s.getFinalGold() + " G",
                s.getPurchasedItems(),
                s.getFormatTime()
            };
            tableModel.addRow(row);
        }
    }

    /**
     * 執行技能：【萬象歸零 (Delete Excel File)】
     */
    private void handleClearData(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "⚠️ 注意：此操作將物理刪除 '" + GameConfig.EXCEL_RANKING_PATH + "' 檔案。\n這會導致所有排行榜紀錄灰飛煙滅。確定執行？",
            "權限警告",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            File file = new File(GameConfig.EXCEL_RANKING_PATH);
            if (file.exists() && file.delete()) {
                JOptionPane.showMessageDialog(this, "✅ 檔案已成功歸零。");
                refreshTableData();
            } else if (!file.exists()) {
                JOptionPane.showMessageDialog(this, "💡 卷軸檔案本來就不存在，無需歸零。");
            } else {
                JOptionPane.showMessageDialog(this, "❌ 歸零失敗：檔案可能正在被其他程式讀取中。", "錯誤", JOptionPane.ERROR_MESSAGE);
            }
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
                new AdminDashboardUI().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}