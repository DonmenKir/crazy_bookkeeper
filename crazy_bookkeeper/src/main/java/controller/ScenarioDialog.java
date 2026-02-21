package controller;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import model.Scenario;

/**
 * [Controller/UI] ScenarioDialog - 秘法試煉視窗 (Scenario Dialog)
 * 修正筆記：
 * 1. 移除按鈕所有自定義配色，回歸標準系統樣式以確保文字清晰可見。
 * 2. 使用標準 JDialog 配合 BorderLayout，提升在 WindowBuilder 中的相容性。
 * 3. 保留 Modal 特性，確保玩家在完成試煉前無法操作主指揮塔。
 */
public class ScenarioDialog extends JDialog {

    private boolean isCorrect = false;

    /**
     * 建構子：初始化試煉視窗
     * @param parent 父視窗 (GameMainUI)
     * @param scenario 題庫服務產生的題目物件
     */
    public ScenarioDialog(JFrame parent, Scenario scenario) {
        super(parent, "⚠️ 秘法突發檢定 ⚠️", true); // 設定為 Modal 視窗
        setSize(500, 450);
        setLocationRelativeTo(parent);
        
        // 使用標準面板與邊界
        JPanel contentPane = new JPanel(new BorderLayout(15, 15));
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPane.setBackground(new Color(250, 250, 250));
        setContentPane(contentPane);

        // --- 1. 頂部提示標題 ---
        JLabel lblHeader = new JLabel("⚡ 偵測到秘法擾動，請詠唱正確咒文！", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Microsoft JhengHei", Font.BOLD, 18));
        lblHeader.setForeground(Color.RED);
        contentPane.add(lblHeader, BorderLayout.NORTH);

        // --- 2. 情境描述區 ---
        JTextArea txtDescription = new JTextArea(scenario.getDescription());
        txtDescription.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 16));
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setEditable(false);
        txtDescription.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(txtDescription);
        scrollPane.setBorder(BorderFactory.createTitledBorder("當前遭遇情境 (Scenario)"));
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // --- 3. 選項按鈕區 (標準樣式) ---
        JPanel panelOptions = new JPanel(new GridLayout(4, 1, 5, 10));
        panelOptions.setOpaque(false);

        List<String> options = scenario.getOptions();
        for (String opt : options) {
            // 使用標準 JButton，無自定義背景/前景特效
            JButton btnOption = new JButton(opt);
            btnOption.setFont(new Font("Consolas", Font.BOLD, 15));
            
            // 點擊事件處理
            btnOption.addActionListener(e -> {
                isCorrect = scenario.checkAnswer(opt);
                if (isCorrect) {
                    JOptionPane.showMessageDialog(this, "✅ 詠唱成功！奧術壓力已獲得疏導。", "試煉通過", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "❌ 咒文失誤！引發魔力反噬。\n正確語法應為：" + scenario.getCorrectSyntax(), 
                        "試煉失敗", 
                        JOptionPane.ERROR_MESSAGE);
                }
                dispose(); // 關閉對話框
            });
            
            panelOptions.add(btnOption);
        }
        
        contentPane.add(panelOptions, BorderLayout.SOUTH);
    }

    /**
     * 技能：【結果探測】
     * 供 GameMainUI 檢查玩家是否答對，以決定壓力值的增減。
     */
    public boolean getResult() {
        return isCorrect;
    }
}