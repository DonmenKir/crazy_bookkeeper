package controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import model.Member;
import model.ShopItem;
import model.GuildStatus;
import dao.impl.ShopDAOImpl;
import dao.impl.GuildStatusDAOImpl;

/**
 * [Controller/UI] ShoppingCartUI - 奧術商店極簡介面
 * 🛠️ 修正重點：
 * 1. 即時同步：每次開啟視窗都會重新呼叫 DAO 查詢資料庫，確保 SQL 修改能立即反應。
 * 2. 極簡風格：統一白底灰字，按鈕尺寸固定，解決視覺擁擠與混亂。
 */
public class ShoppingCartUI extends JFrame {

    private Member currentMember;
    private GameMainUI mainUI;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblBalance;
    
    private ShopDAOImpl shopDao = new ShopDAOImpl();
    private GuildStatusDAOImpl statusDao = new GuildStatusDAOImpl();

    public ShoppingCartUI(Member member, GameMainUI mainUI) {
        this.currentMember = member;
        this.mainUI = mainUI;
        
        initializeUI();
        refreshShopData(); // 💡 確保初始化時直接從 DB 抓取最新價格
    }

    private void initializeUI() {
        setTitle("Crazy Bookkeeper - Arcane Shop");
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(Color.WHITE);
        setContentPane(contentPane);

        // --- 1. 頂部餘額顯示 ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(25, 30, 10, 30));
        
        JLabel lblTitle = new JLabel("奧術物資採購中心");
        lblTitle.setFont(new Font("Microsoft JhengHei", Font.BOLD, 20));
        lblTitle.setForeground(new Color(50, 50, 50));
        
        lblBalance = new JLabel("當前金幣：0 G");
        lblBalance.setFont(new Font("Microsoft JhengHei", Font.BOLD, 16));
        lblBalance.setForeground(new Color(0, 102, 51));
        
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(lblBalance, BorderLayout.EAST);
        contentPane.add(pnlHeader, BorderLayout.NORTH);

        // --- 2. 商品列表 (JTable) ---
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBackground(Color.WHITE);
        pnlTable.setBorder(new EmptyBorder(10, 30, 10, 30));

        String[] columns = {"ID", "物品名稱", "價格 (G)", "庫存", "功能描述"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        table = new JTable(tableModel);
        table.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.setSelectionBackground(new Color(245, 245, 250));
        table.setShowGrid(true);
        table.setGridColor(new Color(240, 240, 240));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(230, 230, 230), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        pnlTable.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(pnlTable, BorderLayout.CENTER);

        // --- 3. 底部動作列 ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlFooter.setBackground(Color.WHITE);
        pnlFooter.setBorder(new EmptyBorder(15, 30, 30, 30));

        JButton btnBuy = new JButton("確認採購");
        styleButton(btnBuy);
        btnBuy.addActionListener(e -> handlePurchase());

        JButton btnClose = new JButton("離開商店");
        styleButton(btnClose);
        btnClose.addActionListener(e -> dispose());

        pnlFooter.add(btnBuy);
        pnlFooter.add(btnClose);
        contentPane.add(pnlFooter, BorderLayout.SOUTH);
    }

    /**
     * 技能：【真理之眼】從資料庫刷新商店內容
     */
    private void refreshShopData() {
        // 1. 更新餘額
        GuildStatus status = statusDao.queryByMemberId(currentMember.getId());
        if (status != null) {
            lblBalance.setText("當前金幣：" + status.getCurrentGold() + " G");
        }

        // 2. 重新讀取 SQL 列表
        tableModel.setRowCount(0);
        List<ShopItem> items = shopDao.queryAll(); // 🛡️ 關鍵：呼叫 DAO 重新 SELECT
        for (ShopItem item : items) {
            tableModel.addRow(new Object[]{
                item.getId(),
                item.getItemName(),
                item.getPrice(),
                item.getStock(),
                item.getDescription()
            });
        }
    }

    private void handlePurchase() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "請先選擇一項奧術物資。");
            return;
        }

        int itemId = (int) tableModel.getValueAt(row, 0);
        String itemName = (String) tableModel.getValueAt(row, 1);
        int price = (int) tableModel.getValueAt(row, 2);
        int stock = (int) tableModel.getValueAt(row, 3);

        if (stock <= 0) {
            JOptionPane.showMessageDialog(this, "物資已耗盡，等待下次補貨。");
            return;
        }

        GuildStatus status = statusDao.queryByMemberId(currentMember.getId());
        if (status.getCurrentGold() < price) {
            JOptionPane.showMessageDialog(this, "金幣不足，無法簽署採購契約。");
            return;
        }

        // 執行扣款與扣庫存
        statusDao.updateGold(currentMember.getId(), -price);
        shopDao.updateStock(itemId, -1);
        
        // 紀錄到主介面日誌
        mainUI.appendLogWithSyntax("奧術採購", "購入了 [" + itemName + "]，金幣 - " + price);
        mainUI.recordPurchase(itemName);
        mainUI.updateGoldDisplay(); // 同步主介面金幣顯示
        
        // 如果買的是壓力冷卻劑，直接幫玩家減壓
        if (itemName.contains("冷卻") || itemName.contains("壓力")) {
            mainUI.updateStress(5); // 假設效果是減 5 壓力
        }

        refreshShopData(); // 💡 採購完畢後立刻刷新
        JOptionPane.showMessageDialog(this, "採購成功！物資已送達。");
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        btn.setPreferredSize(new Dimension(120, 38));
        btn.setBackground(new Color(252, 252, 252));
        btn.setForeground(new Color(70, 70, 70));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(225, 225, 225), 1),
            new EmptyBorder(5, 5, 5, 5)
        ));
    }
}