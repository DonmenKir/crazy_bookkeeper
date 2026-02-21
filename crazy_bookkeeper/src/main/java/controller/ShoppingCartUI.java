package controller;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

import model.Member;
import model.ShopItem;
import dao.impl.ShopDAOImpl;
import dao.impl.MemberDAOImpl;

/**
 * [Controller/View] ShoppingCartUI - 奧術推車 (Shop)
 * 修正筆記：
 * 1. 移除所有自定義按鈕顏色，使用標準系統樣式以確保文字清晰。
 * 2. 使用標準 JList 與 DefaultListModel 呈現商品清單。
 * 3. 透過傳入的 GameMainUI 實例進行壓力降壓與金幣同步。
 * 4. 新增採購記錄功能，將購買的物品回傳給主介面，以便正確結算顯示。
 */
public class ShoppingCartUI extends JFrame {

    private JPanel contentPane;
    private JList<String> listItems;
    private DefaultListModel<String> listModel;
    
    private Member member;
    private GameMainUI mainUI;
    
    private ShopDAOImpl shopDao = new ShopDAOImpl();
    private MemberDAOImpl memberDao = new MemberDAOImpl();
    private List<ShopItem> shopItemList;

    /**
     * 建構子：接收當前記帳士與主塔介面引用
     */
    public ShoppingCartUI(Member member, GameMainUI mainUI) {
        this.member = member;
        this.mainUI = mainUI;
        initializeUI();
        loadShopData();
    }

    private void initializeUI() {
        setTitle("奧術推車 - 資源採購中心");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 僅關閉此視窗
        setSize(500, 550);
        setLocationRelativeTo(mainUI);
        setResizable(false);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(245, 245, 250));
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPane.setLayout(new BorderLayout(0, 15));
        setContentPane(contentPane);

        // --- 1. 頂部標題 ---
        JLabel lblHeader = new JLabel("公會物資採購清單", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Microsoft JhengHei", Font.BOLD, 22));
        lblHeader.setForeground(new Color(72, 61, 139)); // 深板岩藍
        contentPane.add(lblHeader, BorderLayout.NORTH);

        // --- 2. 商品列表區 ---
        listModel = new DefaultListModel<>();
        listItems = new JList<>(listModel);
        listItems.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 15));
        listItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listItems.setFixedCellHeight(70); // 增加行高方便閱讀
        
        JScrollPane scrollPane = new JScrollPane(listItems);
        scrollPane.setBorder(BorderFactory.createTitledBorder("可用物資 (Available Items)"));
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // --- 3. 操作按鈕區 ---
        JPanel panelButtons = new JPanel();
        panelButtons.setOpaque(false);
        panelButtons.setLayout(new GridLayout(1, 2, 10, 0));

        // 標準按鈕，確保字體清晰
        JButton btnBuy = new JButton("確認採購 (Purchase)");
        btnBuy.setFont(new Font("Microsoft JhengHei", Font.BOLD, 16));
        btnBuy.addActionListener(this::handlePurchase);
        panelButtons.add(btnBuy);

        JButton btnClose = new JButton("離開推車 (Close)");
        btnClose.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 16));
        btnClose.addActionListener(e -> dispose());
        panelButtons.add(btnClose);

        contentPane.add(panelButtons, BorderLayout.SOUTH);
    }

    /**
     * 技能：【資產庫存讀取】
     */
    private void loadShopData() {
        listModel.clear();
        shopItemList = shopDao.queryAll();
        
        for (ShopItem item : shopItemList) {
            String displayText = String.format(
                "<html><b>%s</b> - 價格: <font color='blue'>%d G</font> (庫存: %d)<br><small>效果: %s</small></html>",
                item.getItemName(),
                item.getPrice(),
                item.getStock(),
                item.getDescription()
            );
            listModel.addElement(displayText);
        }
    }

    /**
     * 執行技能：【資源交換 (Purchase Logic)】
     */
    private void handlePurchase(ActionEvent e) {
        int selectedIndex = listItems.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ 請先選擇一項物資進行採購。", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ShopItem selectedItem = shopItemList.get(selectedIndex);

        // 1. 檢查金幣
        if (member.getTotalGold() < selectedItem.getPrice()) {
            JOptionPane.showMessageDialog(this, "❌ 你的金庫餘額不足以支付這項物資。", "資金短缺", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. 檢查庫存
        if (selectedItem.getStock() <= 0) {
            JOptionPane.showMessageDialog(this, "🛑 該物資已在公會中售罄。", "庫存告急", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. 扣款與更新 (持久化)
        try {
            // 扣錢 (負數代表支出)
            memberDao.updateGold(member.getId(), -selectedItem.getPrice());
            // 減庫存
            shopDao.updateStock(selectedItem.getId(), -1);
            
            // 同步記憶體數據
            member.setTotalGold(member.getTotalGold() - selectedItem.getPrice());

            // 4. 觸發物品效果 (針對冷卻劑)
            if (selectedItem.getItemName().contains("冷卻劑")) {
                mainUI.updateStress(3); // 降壓效果
            }

            // 5. 刷新 UI 與日誌
            mainUI.updateGoldDisplay();
            mainUI.appendLogWithSyntax("奧術口袋", "成功採購物資：" + selectedItem.getItemName());
            
            // 🛡️ 核心修正：將採購物資傳遞給主介面，儲存到 GameResultUI 顯示的購物清單裡
            mainUI.recordPurchase(selectedItem.getItemName());
            
            JOptionPane.showMessageDialog(this, "✅ 採購成功！物資已送達指揮塔。");
            loadShopData(); // 重新讀取庫存顯示
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "系統錯誤：無法完成交易。", "錯誤", JOptionPane.ERROR_MESSAGE);
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
                // 模擬測試數據
                Member m = new Member("test", "123", "測試記帳士");
                m.setId(1);
                m.setTotalGold(500);
                new ShoppingCartUI(m, null).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}