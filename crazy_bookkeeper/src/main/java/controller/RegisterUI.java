package controller;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import model.Member;
import service.MemberService;

/**
 * [Controller/View] RegisterUI - 血脈刻印中心 (Registration)
 * 修正筆記：
 * 1. 視窗加大至 500x650，提升操作空間感。
 * 2. 按鈕回歸標準 Swing 樣式，解決字體與配色衝突導致的看不清楚問題。
 * 3. 優化 UI 元件間距，符合 WindowBuilder 拖拽規範。
 */
public class RegisterUI extends JFrame {

    private JPanel contentPane;
    private JTextField txtUser;
    private JTextField txtNickname;
    private JPasswordField txtPass;
    private JPasswordField txtConfirm;
    
    private MemberService memberService = new MemberService();

    public RegisterUI() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("《Crazy Bookkeeper》 記帳士血脈刻印");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(245, 245, 250)); // 淺灰色背景，確保輸入框清晰
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // --- 1. 標題區 ---
        JLabel lblHeader = new JLabel("新任記帳士註冊", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Microsoft JhengHei", Font.BOLD, 28));
        lblHeader.setForeground(new Color(25, 25, 112)); // 深藍色標題
        lblHeader.setBounds(0, 40, 500, 50);
        contentPane.add(lblHeader);

        JLabel lblSub = new JLabel("--- 於名冊刻印你的靈魂印記 ---", SwingConstants.CENTER);
        lblSub.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        lblSub.setForeground(Color.GRAY);
        lblSub.setBounds(0, 90, 500, 20);
        contentPane.add(lblSub);

        // --- 2. 輸入表單區 ---
        int labelX = 80;
        int inputX = 80;
        int inputWidth = 340;
        int inputHeight = 35;

        // 帳號
        JLabel lblUser = new JLabel("登入帳號 (Username):");
        lblUser.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        lblUser.setBounds(labelX, 140, 200, 20);
        contentPane.add(lblUser);

        txtUser = new JTextField();
        txtUser.setFont(new Font("Consolas", Font.PLAIN, 16));
        txtUser.setBounds(inputX, 165, inputWidth, inputHeight);
        contentPane.add(txtUser);

        // 暱稱
        JLabel lblNickname = new JLabel("記帳士稱號 (Nickname):");
        lblNickname.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        lblNickname.setBounds(labelX, 215, 200, 20);
        contentPane.add(lblNickname);

        txtNickname = new JTextField();
        txtNickname.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 16));
        txtNickname.setBounds(inputX, 240, inputWidth, inputHeight);
        contentPane.add(txtNickname);

        // 密碼
        JLabel lblPass = new JLabel("靈魂咒文 (Password):");
        lblPass.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        lblPass.setBounds(labelX, 290, 200, 20);
        contentPane.add(lblPass);

        txtPass = new JPasswordField();
        txtPass.setFont(new Font("Consolas", Font.PLAIN, 16));
        txtPass.setBounds(inputX, 315, inputWidth, inputHeight);
        contentPane.add(txtPass);

        // 確認密碼
        JLabel lblConfirm = new JLabel("再次確認咒文 (Confirm):");
        lblConfirm.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        lblConfirm.setBounds(labelX, 365, 200, 20);
        contentPane.add(lblConfirm);

        txtConfirm = new JPasswordField();
        txtConfirm.setFont(new Font("Consolas", Font.PLAIN, 16));
        txtConfirm.setBounds(inputX, 390, inputWidth, inputHeight);
        contentPane.add(txtConfirm);

        // --- 3. 按鈕操作區 ---
        // 使用標準樣式按鈕，確保字體清晰
        JButton btnRegister = new JButton("完成血脈刻印 (Register)");
        btnRegister.setFont(new Font("Microsoft JhengHei", Font.BOLD, 16));
        btnRegister.setBounds(inputX, 470, inputWidth, 50);
        btnRegister.addActionListener(this::handleRegister);
        contentPane.add(btnRegister);

        JButton btnBack = new JButton("返回登入大門 (Back)");
        btnBack.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        btnBack.setBounds(inputX, 535, inputWidth, 35);
        btnBack.addActionListener(e -> {
            new LoginUI().setVisible(true);
            dispose();
        });
        contentPane.add(btnBack);

        // 底部裝飾
        JLabel lblFooter = new JLabel("Kingdom Audit Department © 2026", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Arial", Font.ITALIC, 10));
        lblFooter.setForeground(Color.LIGHT_GRAY);
        lblFooter.setBounds(0, 590, 500, 20);
        contentPane.add(lblFooter);
    }

    /**
     * 執行技能：【血脈刻印 (Registration)】
     */
    private void handleRegister(ActionEvent e) {
        String username = txtUser.getText().trim();
        String nickname = txtNickname.getText().trim();
        String password = new String(txtPass.getPassword());
        String confirm = new String(txtConfirm.getPassword());

        // 1. 基礎驗證
        if (username.isEmpty() || nickname.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ 所有刻印欄位皆不可為空！", "刻印失敗", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. 密碼一致性驗證
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "❌ 兩次輸入的咒文不匹配，請重新確認。", "刻印失敗", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. 執行服務
        Member newMember = new Member(username, password, nickname);
        try {
            memberService.register(newMember);
            JOptionPane.showMessageDialog(this, "✅ 血脈刻印成功！請使用新身分進入公會。");
            new LoginUI().setVisible(true);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "🛑 帳號可能已存在於名冊中，或資料庫魔力管道不穩。", "系統崩潰", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // 設定系統預設 LookAndFeel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        EventQueue.invokeLater(() -> {
            try {
                RegisterUI frame = new RegisterUI();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}