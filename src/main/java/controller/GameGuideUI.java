package controller;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import model.Member;

/**
 * [Controller/UI] GameGuideUI - 秘法契約導引介面
 * 職責：在進入指揮塔前，向記帳士說明遊戲規則與結束條件。
 * 設計：採用分頁式 (TabbedPane) 呈現大量資訊，並加入強制閱讀的確認機制。
 */
public class GameGuideUI extends JFrame {

    private Member currentMember;
    private JButton btnStart;
    private JCheckBox chkConfirm;

    public GameGuideUI(Member member) {
        this.currentMember = member;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("《Crazy Bookkeeper》 記帳士生存契約");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(new Color(245, 245, 250));
        setContentPane(contentPane);

        // --- 1. 標題區 ---
        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        
        JLabel lblTitle = new JLabel("歡迎來到秘法公會，" + currentMember.getNickname());
        lblTitle.setFont(new Font("Microsoft JhengHei", Font.BOLD, 22));
        lblTitle.setForeground(new Color(25, 25, 112));
        pnlHeader.add(lblTitle);
        contentPane.add(pnlHeader, BorderLayout.NORTH);

        // --- 2. 規則分頁區 ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        
        tabbedPane.addTab("📜 核心任務", createHtmlPanel(getCoreMissionText()));
        tabbedPane.addTab("⚖️ 查帳與黑帳", createHtmlPanel(getAuditRulesText()));
        tabbedPane.addTab("💀 敗北條件", createHtmlPanel(getGameOverText()));

        contentPane.add(tabbedPane, BorderLayout.CENTER);

        // --- 3. 底部簽署區 ---
        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setBorder(new EmptyBorder(15, 20, 15, 20));
        pnlFooter.setBackground(new Color(240, 240, 245));

        chkConfirm = new JCheckBox("我已詳細閱讀上述條款，並願承擔奧術崩潰的風險。");
        chkConfirm.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        chkConfirm.setOpaque(false);
        chkConfirm.addActionListener(e -> btnStart.setEnabled(chkConfirm.isSelected()));

        btnStart = new JButton("簽署契約並進入指揮塔 (Enter Game)");
        btnStart.setFont(new Font("Microsoft JhengHei", Font.BOLD, 16));
        btnStart.setEnabled(false); // 預設鎖定
        btnStart.addActionListener(e -> {
            new GameMainUI(currentMember).setVisible(true);
            dispose();
        });

        pnlFooter.add(chkConfirm, BorderLayout.NORTH);
        pnlFooter.add(Box.createVerticalStrut(10));
        pnlFooter.add(btnStart, BorderLayout.SOUTH);

        contentPane.add(pnlFooter, BorderLayout.SOUTH);
    }

    private JScrollPane createHtmlPanel(String htmlContent) {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setText(htmlContent);
        editorPane.setEditable(false);
        editorPane.setBackground(Color.WHITE);
        editorPane.setCaretPosition(0); // 捲動到頂部

        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setBorder(null);
        return scrollPane;
    }

    // --- HTML 文本生成區 ---

    private String getCoreMissionText() {
        return "<html><body style='font-family:Microsoft JhengHei; padding:15px; font-size:13px;'>"
                + "<h2 style='color:#4B0082;'>🎯 記帳士的核心職責</h2>"
                + "<ul>"
                + "<li><b>資產結算：</b>冒險者每 3 秒帶來一件戰利品。請使用 <b>[Accept]</b> 將其寫入 <b>官方帳本 (Database)</b>。</li>"
                + "<li><b>奧術壓力 (Stress)：</b>若處理速度太慢，壓力條會持續上升。使用 <b>購物車 (Cart)</b> 購買冷卻劑可降低壓力。</li>"
                + "<li><b>秘法試煉 (Trial)：</b>每 15 秒會觸發一次 Java 語法測驗。答對可釋放壓力，答錯將引發 <b>奧術反噬 (+3 壓力)</b>。</li>"
                + "</ul>"
                + "</body></html>";
    }

    private String getAuditRulesText() {
        return "<html><body style='font-family:Microsoft JhengHei; padding:15px; font-size:13px;'>"
                + "<h2 style='color:#B22222;'>🚨 王國查帳與黑帳機制</h2>"
                + "<p>公會中有時會出現標記為 <b>Illegal (違禁品)</b> 的黑暗卷軸。</p>"
                + "<ul>"
                + "<li><b>高風險高回報：</b>黑暗卷軸擁有 <b>1.5 倍</b> 的結算收益。</li>"
                + "<li><b>王國查帳 (Audit)：</b>查帳官每 30 秒突擊一次，包含 10 秒預警與 5 秒臨檢。若他在 <b>官方帳本 (Database)</b> 查到已結算的違禁紀錄，將強制追繳罰款並增加壓力！</li>"
                + "<li><b>現場沒收：</b>臨檢的 5 秒內，排隊中或手邊的 Illegal 違禁品會被<b>當場沒收</b>並驅離冒險者（不引發結束遊戲）。</li>"
                + "<li><b>幽影黑帳 (Shadow Ledger)：</b>利用 <b>HashMap</b> 暫存違禁品可安全躲避查帳。</li>"
                + "<li><b>時空洗白 (Launder)：</b>在黑帳中使用洗白術，可將物品轉為合法資產 (去除 Illegal 標籤)，安全入庫。</li>"
                + "</ul>"
                + "</body></html>";
    }

    private String getGameOverText() {
        return "<html><body style='font-family:Microsoft JhengHei; padding:15px; font-size:13px;'>"
                + "<h2 style='color:#FF4500;'>💀 萬象終局：敗北條件</h2>"
                + "<p>當下列任一情況發生時，遊戲強制結束，資產將被結算並歸零：</p>"
                + "<ol>"
                + "<li><b>奧術壓力爆表：</b>壓力值達到 <b>15 點 (MAX)</b>。</li>"
                + "<li><b>公會查封：</b>在查帳期間被發現大量違禁品，導致罰款後壓力爆表。</li>"
                + "<li><b>中途逃離：</b>在遊戲中點擊 [Exit] 或強制登出。</li>"
                + "</ol>"
                + "<hr>"
                + "<p style='color:gray;'><i>* 遊戲結束後，您的最終金幣與採購紀錄將被刻印在王國排行榜 (Excel) 上，隨後帳號資產將歸零重置 (Roguelike 制)。</i></p>"
                + "</body></html>";
    }
}