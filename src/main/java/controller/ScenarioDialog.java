package controller;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import model.Scenario;

/**
 * [View/Controller] ScenarioDialog - 秘法試煉對話框
 * 🛠️ 最新升級：在題目下方加入「秘法來源提示 (Type & Package)」。
 */
public class ScenarioDialog extends JDialog {
    private boolean result = false;

    public ScenarioDialog(JFrame parent, Scenario scenario) {
        super(parent, "✨ 秘法試煉：語法檢定", true);
        setSize(550, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // 1. 題目區域
        JPanel pnlQuestion = new JPanel(new GridLayout(2, 1));
        pnlQuestion.setBackground(new Color(25, 25, 112));
        pnlQuestion.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblText = new JLabel("<html><div style='text-align: center; color: white;'>" + scenario.getDescription() + "</div></html>");
        lblText.setFont(new Font("Microsoft JhengHei", Font.BOLD, 18));
        lblText.setHorizontalAlignment(SwingConstants.CENTER);
        pnlQuestion.add(lblText);

        // 🛡️ 新增：秘法提示標籤 (Type & Package)
        JLabel lblHint = new JLabel("💡 秘法提示｜類型：" + scenario.getType() + " ｜ 溯源套件：" + scenario.getPackagePath());
        lblHint.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 13));
        lblHint.setForeground(new Color(173, 216, 230)); // 淺藍色提示文字
        lblHint.setHorizontalAlignment(SwingConstants.CENTER);
        pnlQuestion.add(lblHint);

        add(pnlQuestion, BorderLayout.NORTH);

        // 2. 選項區域
        JPanel pnlOptions = new JPanel(new GridLayout(2, 2, 10, 10));
        pnlOptions.setBorder(new EmptyBorder(20, 20, 20, 20));

        for (String option : scenario.getOptions()) {
            JButton btn = new JButton(option);
            btn.setFont(new Font("Consolas", Font.PLAIN, 14));
            btn.addActionListener(e -> {
                if (option.equals(scenario.getCorrectSyntax())) {
                    result = true;
                    JOptionPane.showMessageDialog(this, "✅ 咒文解析正確！奧術壓力已緩解。");
                } else {
                    result = false;
                    JOptionPane.showMessageDialog(this, "❌ 咒文出錯！正確答案為：" + scenario.getCorrectSyntax(), "奧術反噬", JOptionPane.ERROR_MESSAGE);
                }
                dispose();
            });
            pnlOptions.add(btn);
        }
        add(pnlOptions, BorderLayout.CENTER);
    }

    public boolean getResult() { return result; }
}