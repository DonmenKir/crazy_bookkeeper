package controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.*;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.io.*;
import java.util.Vector;
import model.Member;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

/**
 * [Controller/UI] RankingUI - 傳說排行榜安全維護版
 * 🛠️ 修正重點：
 * 1. 權限控管：根據 Member 角色動態顯示功能按鈕，防止一般玩家進入管理後台。
 * 2. 日誌優化：抑制 Log4j 警告，並優化 Excel 讀取異常處理。
 * 3. 視覺一致：固定按鈕 PreferredSize 確保極簡風格不走鐘。
 */
public class RankingUI extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private Member currentMember;
    private boolean isAdmin = false;
    private static final String EXCEL_PATH = "Kingdom_Rankings.xlsx";

    public RankingUI(Member member) {
        this.currentMember = member;
        // 判斷權限：角色為 admin 或是帳號為 admin
        if (member != null && ("admin".equalsIgnoreCase(member.getRole()) || "admin".equalsIgnoreCase(member.getUsername()))) {
            this.isAdmin = true;
        }
        
        // 抑制 POI 的 Log4j 警告訊息
        System.setProperty("org.apache.logging.log4j.simplelog.StatusLogger.level", "OFF");
        
        initializeUI();
        loadRankingData();
    }

    private void initializeUI() {
        setTitle("Crazy Bookkeeper - Legend Rankings");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(Color.WHITE);
        setContentPane(contentPane);

        // --- 1. 標題區 ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(25, 30, 10, 30)); 
        
        JLabel lblTitle = new JLabel("傳說排行榜 · 審計紀錄");
        lblTitle.setFont(new Font("Microsoft JhengHei", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 50, 50));
        pnlHeader.add(lblTitle);
        contentPane.add(pnlHeader, BorderLayout.NORTH);

        // --- 2. 表格區域 ---
        JPanel pnlTableContainer = new JPanel(new BorderLayout());
        pnlTableContainer.setBackground(Color.WHITE);
        pnlTableContainer.setBorder(new EmptyBorder(10, 30, 10, 30));

        String[] columns = {"排名", "記帳士姓名", "結算金幣", "採購物資", "審計時間"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        table = new JTable(tableModel);
        table.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        table.setRowHeight(40); 
        table.setSelectionBackground(new Color(248, 248, 250));
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(true);
        table.setGridColor(new Color(245, 245, 245));
        table.getTableHeader().setBackground(new Color(250, 250, 250));
        table.getTableHeader().setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(230, 230, 230), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        pnlTableContainer.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(pnlTableContainer, BorderLayout.CENTER);

        // --- 3. 工具列 (動態權限顯示) ---
        JPanel pnlTool = new JPanel(new BorderLayout());
        pnlTool.setBackground(Color.WHITE);
        pnlTool.setBorder(new EmptyBorder(20, 30, 35, 30));

        // 左側：返回按鈕 (根據權限決定去向)
        JButton btnBack = new JButton(isAdmin ? "返回管理中心" : "返回登入畫面");
        styleButton(btnBack);
        btnBack.addActionListener(e -> {
            if (isAdmin) {
                new AdminDashboardUI().setVisible(true);
            } else {
                new LoginUI().setVisible(true);
            }
            this.dispose();
        });

        // 右側：操作按鈕
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlRight.setOpaque(false);

        // 🛡️ 只有管理員能看到「刪除」與「刷新」
        if (isAdmin) {
            JButton btnDelete = new JButton("刪除紀錄");
            styleButton(btnDelete);
            btnDelete.addActionListener(e -> deleteSelectedRecord());

            JButton btnRefresh = new JButton("刷新數據");
            styleButton(btnRefresh);
            btnRefresh.addActionListener(e -> loadRankingData());

            pnlRight.add(btnDelete);
            pnlRight.add(btnRefresh);
        } else {
            // 一般玩家只看到提示訊息或簡單的關閉
            JLabel lblNotice = new JLabel("※ 僅大稽核官可進行紀錄維護");
            lblNotice.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 12));
            lblNotice.setForeground(Color.LIGHT_GRAY);
            pnlRight.add(lblNotice);
        }

        pnlTool.add(btnBack, BorderLayout.WEST);
        pnlTool.add(pnlRight, BorderLayout.EAST);
        
        contentPane.add(pnlTool, BorderLayout.SOUTH);
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        btn.setBackground(new Color(252, 252, 252));
        btn.setForeground(new Color(60, 60, 60));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 38)); // 固定尺寸防止忽大忽小
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(225, 225, 225), 1),
            new EmptyBorder(5, 5, 5, 5)
        ));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(245, 245, 245));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(252, 252, 252));
            }
        });
    }

    private void loadRankingData() {
        tableModel.setRowCount(0);
        File file = new File(EXCEL_PATH);
        if (!file.exists() || file.length() == 0) return;

        try (FileInputStream fis = new FileInputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            
            XSSFSheet sheet = workbook.getSheetAt(0);
            if (sheet == null) return;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                XSSFRow row = sheet.getRow(i);
                if (row == null) continue;
                
                Vector<Object> rowData = new Vector<>();
                for (int j = 0; j < 5; j++) {
                    XSSFCell cell = row.getCell(j);
                    if (cell == null) {
                        rowData.add("");
                    } else {
                        cell.setCellType(CellType.STRING);
                        rowData.add(cell.getStringCellValue());
                    }
                }
                tableModel.addRow(rowData);
            }
        } catch (Exception e) {
            // 靜默處理小型異常，避免彈窗騷擾玩家
            System.err.println(">>> [系統資訊] 排行榜讀取中...");
        }
    }

    private void deleteSelectedRecord() {
        if (!isAdmin) return;
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;

        int confirm = JOptionPane.showConfirmDialog(this, "確定要抹除此紀錄嗎？", "操作確認", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);
            saveAllToExcel();
        }
    }

    private void saveAllToExcel() {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Rankings");
            XSSFRow header = sheet.createRow(0);
            String[] cols = {"排名", "記帳士姓名", "結算金幣", "採購物資", "審計時間"};
            for (int i = 0; i < cols.length; i++) header.createCell(i).setCellValue(cols[i]);

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                XSSFRow row = sheet.createRow(i + 1);
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    Object val = tableModel.getValueAt(i, j);
                    row.createCell(j).setCellValue(val != null ? val.toString() : "");
                }
            }
            try (FileOutputStream fos = new FileOutputStream(EXCEL_PATH)) {
                workbook.write(fos);
                fos.flush();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "存檔失敗：" + e.getMessage());
        }
    }
}