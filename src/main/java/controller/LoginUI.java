package controller;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import model.Member;
import service.MemberService;

public class LoginUI extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;
    private MemberService memberService = new MemberService();

    public LoginUI() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("《Crazy Bookkeeper》 契約驗證大門");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        // 背景保持深色，但按鈕會是標準樣式
        mainPanel.setBackground(new Color(240, 240, 245)); 
        setContentPane(mainPanel);

        // 1. 標題區
        JLabel lblTitle = new JLabel("CRAZY BOOKKEEPER", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Serif", Font.BOLD, 28));
        lblTitle.setForeground(new Color(25, 25, 112));
        lblTitle.setBounds(0, 30, 450, 40);
        mainPanel.add(lblTitle);
        
        JLabel lblSub = new JLabel("--- 秘法與金錢的交界處 ---", SwingConstants.CENTER);
        lblSub.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        lblSub.setForeground(Color.GRAY);
        lblSub.setBounds(0, 70, 450, 20);
        mainPanel.add(lblSub);

        // 2. 輸入欄位
        JLabel lblUser = new JLabel("帳號 (Username):");
        lblUser.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        lblUser.setBounds(50, 130, 200, 20);
        mainPanel.add(lblUser);

        txtUser = new JTextField();
        txtUser.setFont(new Font("Consolas", Font.PLAIN, 16));
        txtUser.setBounds(50, 155, 330, 35);
        mainPanel.add(txtUser);

        JLabel lblPass = new JLabel("密碼 (Password):");
        lblPass.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        lblPass.setBounds(50, 210, 200, 20);
        mainPanel.add(lblPass);

        txtPass = new JPasswordField();
        txtPass.setFont(new Font("Consolas", Font.PLAIN, 16));
        txtPass.setBounds(50, 235, 330, 35);
        mainPanel.add(txtPass);

        // 3. 按鈕區 (標準 Swing 按鈕)
        JButton btnLogin = new JButton("執行靈魂驗證 (Login)");
        btnLogin.setFont(new Font("Microsoft JhengHei", Font.BOLD, 16));
        btnLogin.setBounds(50, 310, 330, 45);
        btnLogin.addActionListener(this::handleLogin);
        mainPanel.add(btnLogin);

        JButton btnRegister = new JButton("註冊新記帳士 (Register)");
        btnRegister.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        btnRegister.setBounds(50, 370, 330, 35);
        btnRegister.addActionListener(e -> {
            new RegisterUI().setVisible(true);
            this.dispose();
        });
        mainPanel.add(btnRegister);
    }

    private void handleLogin(ActionEvent e) {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());

        Member member = memberService.login(user, pass);

        if (member != null) {
            if (member.isLegendary()) {
                new AdminDashboardUI().setVisible(true);
            } else {
                new GameGuideUI(member).setVisible(true);
            }
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "驗證失敗：帳號或密碼錯誤。", "錯誤", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // 設定為系統預設風格，讓按鈕看起來像 Windows/Mac 原生按鈕
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}