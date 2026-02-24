package controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * [Controller/UI] InsightBookEditorUI - 祕法題庫極簡編輯器
 * 職責：管理員編輯本地 CSV 題庫的介面。
 * 🛠️ 設計更新：
 * 1. 導航功能：新增「返回」按鈕並自動關閉當前視窗。
 * 2. 極簡風格：白底、灰字、細線，解決擁擠感。
 * 3. 編碼相容：持續支援 MS950 (Excel) 編碼。
 */
public class InsightBookEditorUI extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private static final String CSV_PATH = "InsightBook.csv";
    private static final String CHARSET = "MS950"; 

    public InsightBookEditorUI() {
        initializeUI();
        loadCsv();
    }

    private void initializeUI() {
        setTitle("Crazy Bookkeeper - InsightBook Editor");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // 主容器：純淨白
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(Color.WHITE);
        setContentPane(contentPane);

        // --- 1. 標題與空間感 ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(20, 25, 10, 25)); // 增加四周邊距
        
        JLabel lblTitle = new JLabel("祕法題庫編輯中心");
        lblTitle.setFont(new Font("Microsoft JhengHei", Font.BOLD, 20));
        lblTitle.setForeground(new Color(60, 60, 60));
        pnlHeader.add(lblTitle);
        contentPane.add(pnlHeader, BorderLayout.NORTH);

        // --- 2. 表格區域 (加入內邊距減少擁擠感) ---
        JPanel pnlTableContainer = new JPanel(new BorderLayout());
        pnlTableContainer.setBackground(Color.WHITE);
        pnlTableContainer.setBorder(new EmptyBorder(10, 25, 10, 25));

        String[] columns = {"情境描述", "正確語法", "類型(Type)", "所屬套件 (Package)"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        table.setRowHeight(35); // 增加行高讓視覺更舒服
        table.setSelectionBackground(new Color(245, 245, 250));
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(true);
        table.setGridColor(new Color(240, 240, 240));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(230, 230, 230), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        pnlTableContainer.add(scrollPane, BorderLayout.CENTER);
        
        contentPane.add(pnlTableContainer, BorderLayout.CENTER);

        // --- 3. 極簡工具列 ---
        JPanel pnlTool = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlTool.setBackground(Color.WHITE);
        pnlTool.setBorder(new EmptyBorder(15, 25, 30, 25));

        // 按鈕 A: 返回 (最左邊)
        JButton btnBack = new JButton("返回管理中心");
        styleButton(btnBack);
        btnBack.addActionListener(e -> {
            new AdminDashboardUI().setVisible(true);
            this.dispose(); // 💡 導航後關閉
        });

        // 按鈕 B: 新增
        JButton btnAdd = new JButton("新增一行");
        styleButton(btnAdd);
        btnAdd.addActionListener(e -> tableModel.addRow(new String[]{"", "", "Class", "java.util"}));

        // 按鈕 C: 刪除
        JButton btnDelete = new JButton("刪除選中行");
        styleButton(btnDelete);
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) tableModel.removeRow(row);
        });

        // 按鈕 D: 儲存
        JButton btnSave = new JButton("儲存變更");
        styleButton(btnSave);
        btnSave.addActionListener(e -> saveCsv());

        // 組裝按鈕 (由左至右為：返回 ... 新增、刪除、儲存)
        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlLeft.setOpaque(false);
        pnlLeft.add(btnBack);

        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlRight.setOpaque(false);
        pnlRight.add(btnAdd);
        pnlRight.add(btnDelete);
        pnlRight.add(btnSave);

        JPanel pnlBottomWrapper = new JPanel(new BorderLayout());
        pnlBottomWrapper.setOpaque(false);
        pnlBottomWrapper.add(pnlLeft, BorderLayout.WEST);
        pnlBottomWrapper.add(pnlRight, BorderLayout.EAST);
        
        pnlTool.add(pnlBottomWrapper);
        contentPane.add(pnlTool, BorderLayout.SOUTH);
    }

    /**
     * 極簡按鈕樣式：與 AdminDashboardUI 統一
     */
    private void styleButton(JButton btn) {
        btn.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        btn.setBackground(new Color(252, 252, 252));
        btn.setForeground(new Color(70, 70, 70));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(225, 225, 225), 1),
            new EmptyBorder(8, 15, 8, 15)
        ));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(245, 245, 245));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(252, 252, 252));
            }
        });
    }

    private void loadCsv() {
        File file = new File(CSV_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), Charset.forName(CHARSET)))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; }
                if (line.trim().isEmpty()) continue;
                List<String> parts = parseCsvLine(line);
                if (parts.size() >= 4) {
                    tableModel.addRow(new Object[]{parts.get(0), parts.get(1), parts.get(2), parts.get(3)});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveCsv() {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(CSV_PATH), Charset.forName(CHARSET)))) {
            bw.write("情境描述,正確語法,類型(Type),所屬套件 (Package)");
            bw.newLine();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                StringBuilder line = new StringBuilder();
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    String val = String.valueOf(tableModel.getValueAt(i, j));
                    line.append(formatField(val));
                    if (j < tableModel.getColumnCount() - 1) line.append(",");
                }
                bw.write(line.toString());
                bw.newLine();
            }
            JOptionPane.showMessageDialog(this, "題庫已成功保存至本地。");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "儲存失敗：" + e.getMessage());
        }
    }

    private String formatField(String val) {
        if (val.contains(",") || val.contains("\"")) {
            val = val.replace("\"", "\"\"");
            return "\"" + val + "\"";
        }
        return val;
    }

    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    sb.append('\"'); i++;
                } else inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(sb.toString().trim());
                sb.setLength(0);
            } else sb.append(c);
        }
        result.add(sb.toString().trim());
        return result;
    }
}