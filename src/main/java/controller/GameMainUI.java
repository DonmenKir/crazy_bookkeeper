package controller;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

import model.Member;
import model.Loot;
import model.Adventurer;
import model.GuildStatus;
import model.Scenario;
import service.QueueService;
import service.AccountantService;
import service.impl.AccountantServiceImpl;
import service.LootGeneratorService;
import service.WorldService;
import service.ScenarioService;
import service.ShadowLedgerService;
import dao.impl.GuildStatusDAOImpl;
import dao.impl.AdventurerDAOImpl;
import dao.impl.LootDAOImpl;
import dao.impl.ShopDAOImpl;
import exception.BookkeeperException;
import config.GameConfig;

/**
 * [Controller/UI] GameMainUI - 奧術指揮塔
 * 🛠️ 最新修正：
 * 1. 視窗連動：新增 shopWindow 引用，確保遊戲結束時商店會主動關閉。
 * 2. 查帳隱私：嚴格限制查帳官權限。查帳官僅能掃描「官方帳本 (DB)」與「現場目擊」，不可查閱「黑帳 (ShadowLedger)」。
 */
public class GameMainUI extends JFrame {

    private Member currentMember;
    private QueueService queueService = new QueueService();
    private AccountantService accountantService = new AccountantServiceImpl();
    private ScenarioService scenarioService = new ScenarioService();
    private ShadowLedgerService shadowService = new ShadowLedgerService();
    private GuildStatusDAOImpl statusDao = new GuildStatusDAOImpl();
    private AdventurerDAOImpl adventurerDao = new AdventurerDAOImpl();
    private LootDAOImpl lootDao = new LootDAOImpl(); 
    private ShopDAOImpl shopDao = new ShopDAOImpl();
    
    // 🛡️ 新增：紀錄當前開啟的商店視窗
    private ShoppingCartUI shopWindow;
    
    private LootGeneratorService generatorService;
    private volatile boolean isGaming = true; 
    private boolean isGameOver = false; 
    private int currentStress = 0;
    
    private Timer gameLoop, trialTimer, auditTriggerTimer, auditSequenceTimer; 
    private int auditState = 0; 
    private int auditTick = 0;  
    private Loot currentLoot;     
    private List<String> purchaseHistory = new ArrayList<>();

    private JPanel contentPane;
    private JLabel lblGold, lblAdventurer, lblLootInfo, lblAuditWarning;
    private JProgressBar prgPressure;
    private JTextPane txtLog; 
    private JButton btnAccept, btnRefuse, btnStash, btnLaunder, btnShop;

    public GameMainUI(Member member) {
        this.currentMember = member;
        accountantService.setCurrentMember(member);
        initializeUI();
        updateGoldDisplay(); 
        startGameEngine();
    }

    private void initializeUI() {
        setTitle("《Crazy Bookkeeper》 奧術指揮塔 - 記帳士：" + currentMember.getNickname());
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });
        setLocationRelativeTo(null);
        
        contentPane = new JPanel();
        contentPane.setBackground(new Color(245, 245, 250));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // --- (中略：介面初始化代碼與之前相同) ---
        JPanel panelHeader = new JPanel();
        panelHeader.setBackground(Color.WHITE);
        panelHeader.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        panelHeader.setBounds(10, 10, 1064, 80);
        contentPane.add(panelHeader);
        panelHeader.setLayout(null);

        JLabel lblName = new JLabel("✨ 當前記帳士：" + currentMember.getNickname());
        lblName.setFont(new Font("Microsoft JhengHei", Font.BOLD, 18));
        lblName.setBounds(20, 25, 400, 30);
        panelHeader.add(lblName);

        lblGold = new JLabel("0 G"); 
        lblGold.setHorizontalAlignment(SwingConstants.RIGHT);
        lblGold.setForeground(new Color(0, 120, 0));
        lblGold.setFont(new Font("Serif", Font.BOLD, 32));
        lblGold.setBounds(600, 20, 440, 40);
        panelHeader.add(lblGold);

        JLabel lblPTitle = new JLabel("⚡ 奧術壓力值 (Stress):");
        lblPTitle.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        lblPTitle.setBounds(20, 110, 200, 20);
        contentPane.add(lblPTitle);

        prgPressure = new JProgressBar(0, GameConfig.MAX_PRESSURE);
        prgPressure.setBounds(20, 135, 650, 25);
        prgPressure.setStringPainted(true);
        contentPane.add(prgPressure);

        lblAuditWarning = new JLabel("", SwingConstants.CENTER);
        lblAuditWarning.setFont(new Font("Microsoft JhengHei", Font.BOLD, 16));
        lblAuditWarning.setBounds(20, 162, 650, 20);
        lblAuditWarning.setVisible(false); 
        contentPane.add(lblAuditWarning);

        txtLog = new JTextPane();
        txtLog.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        txtLog.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setBounds(20, 185, 650, 375);
        scrollLog.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(), "📜 指揮塔動態 (Activity Feed)"));
        contentPane.add(scrollLog);

        lblAdventurer = new JLabel("🌀 觀測中...", SwingConstants.CENTER);
        lblAdventurer.setFont(new Font("Microsoft JhengHei", Font.BOLD, 18));
        lblAdventurer.setBounds(20, 580, 320, 40);
        contentPane.add(lblAdventurer);

        lblLootInfo = new JLabel("", SwingConstants.CENTER);
        lblLootInfo.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 16));
        lblLootInfo.setBounds(350, 580, 320, 40);
        contentPane.add(lblLootInfo);

        JPanel pnlBtns = new JPanel(new GridLayout(6, 1, 0, 15));
        pnlBtns.setOpaque(false);
        pnlBtns.setBounds(690, 110, 384, 560);
        contentPane.add(pnlBtns);

        btnAccept = new JButton("簽署契約 (Accept)");
        btnAccept.addActionListener(this::handleAccept);
        pnlBtns.add(btnAccept);

        btnRefuse = new JButton("拒絕收納 (Refuse)");
        btnRefuse.addActionListener(e -> resetEvent());
        pnlBtns.add(btnRefuse);

        btnStash = new JButton("藏入黑帳 (Stash Illegal)");
        btnStash.setForeground(new Color(139, 0, 0));
        btnStash.addActionListener(this::handleStash);
        pnlBtns.add(btnStash);

        btnLaunder = new JButton("時空洗白 (Launder)");
        btnLaunder.addActionListener(this::handleLaunder);
        pnlBtns.add(btnLaunder);

        btnShop = new JButton("奧術商店 (Shop)");
        btnShop.addActionListener(this::openShop); // 🛡️ 修改為專門的開商店方法
        pnlBtns.add(btnShop);

        JButton btnExit = new JButton("登出系統 (Logout)");
        btnExit.addActionListener(e -> handleExit());
        pnlBtns.add(btnExit);

        btnAccept.setEnabled(false);
        btnRefuse.setEnabled(false);
        btnStash.setEnabled(false);
    }

    private void openShop(ActionEvent e) {
        if (shopWindow == null || !shopWindow.isVisible()) {
            shopWindow = new ShoppingCartUI(currentMember, this);
            shopWindow.setVisible(true);
        } else {
            shopWindow.toFront();
        }
    }

    private void startGameEngine() {
        generatorService = new LootGeneratorService(queueService);
        generatorService.start();
        
        gameLoop = new Timer(GameConfig.SPAWN_RATE_MS, e -> {
            if (!isGaming || isGameOver || btnAccept.isEnabled()) return; 
            this.currentLoot = queueService.getNextLoot();
            if (this.currentLoot != null) {
                Adventurer adv = adventurerDao.getRandomAdventurer();
                if (adv != null) {
                    currentStress += adv.getStressImpact();
                    updateStressUI();
                    lblAdventurer.setText("👤 冒險者：" + adv.getName());
                    String lootStr = "📦 物品：" + currentLoot.getItemName();
                    if (currentLoot.isIllegal()) lootStr = "⚠️ [違禁品] " + lootStr;
                    lblLootInfo.setText(lootStr);
                    btnAccept.setEnabled(true);
                    btnRefuse.setEnabled(true);
                    btnStash.setEnabled(currentLoot.isIllegal()); 
                    if (currentStress >= GameConfig.MAX_PRESSURE) gameOver("壓力爆表，公會崩潰！");
                }
            }
        });
        gameLoop.start();

        // 🛡️ 王國查帳序列：嚴格定義查帳範圍
        auditTriggerTimer = new Timer(30000, e -> {
            if (!isGaming || isGameOver || auditState != 0) return;
            auditState = 1; auditTick = 10;
            lblAuditWarning.setVisible(true);
            appendLogToPane("王國查帳", "稽核官將在 10 秒後抵達公會！", new Color(204, 102, 0));
            auditSequenceTimer.start();
        });
        auditTriggerTimer.start();

        auditSequenceTimer = new Timer(1000, e -> {
            if (!isGaming || isGameOver) return;
            if (auditState == 1) { 
                lblAuditWarning.setText("🔔 稽核官預計抵達：" + auditTick + " 秒");
                auditTick--;
                if (auditTick < 0) {
                    auditState = 2; auditTick = 5;  
                    appendRedLog("查帳開始", "🚨 稽核官已進入公會！開始查閱「官方帳本」！");
                    
                    // 🛡️ 正確權限：查帳官只會查閱 Database (lootDao)
                    // 絕不會呼叫 shadowService.queryAll()，因此黑帳是安全的。
                    List<Loot> officialLedger = lootDao.queryByMember(currentMember.getId());
                    int fine = 0; int caughtCount = 0;
                    for (Loot l : officialLedger) {
                        if (l.isIllegal()) {
                            caughtCount++;
                            fine += (int)(l.getItemValue() * GameConfig.DARK_SCROLL_MULTIPLIER);
                            lootDao.delete(l.getId()); 
                        }
                    }
                    if (caughtCount > 0) {
                        statusDao.updateGold(currentMember.getId(), -fine);
                        updateGoldDisplay();
                        currentStress = Math.min(GameConfig.MAX_PRESSURE, currentStress + 3);
                        updateStressUI();
                        appendRedLog("帳目異常", "查獲已結算的非法紀錄！罰款 " + fine + " G。");
                        if (currentStress >= GameConfig.MAX_PRESSURE) gameOver("罰款重壓導致崩潰！");
                    }
                }
            } else if (auditState == 2) { 
                lblAuditWarning.setText("🚨 稽核官監視中... 剩餘：" + auditTick + " 秒");
                
                // 🛡️ 現場臨檢：只查畫面上目擊到的與排隊中的
                if (currentLoot != null && currentLoot.isIllegal()) {
                    appendRedLog("當場沒收", "稽核官目擊到違禁品 [" + currentLoot.getItemName() + "]，已沒收並驅離！");
                    resetEvent(); 
                }
                
                if (queueService.hasIllegalLoot()) {
                    List<Loot> q = queueService.clearAndExtractAll();
                    for (Loot l : q) { if (!l.isIllegal()) queueService.addLoot(l); }
                    appendRedLog("隊列臨檢", "稽核官清理了目送範圍內的違禁品！");
                }
                
                auditTick--;
                if (auditTick < 0) {
                    auditState = 0; lblAuditWarning.setVisible(false);
                    appendLogWithSyntax("查帳結束", "稽核官已離開。");
                    auditSequenceTimer.stop(); 
                }
            }
        });

        trialTimer = new Timer(15000, e -> {
            if (!isGaming || isGameOver) return;
            Scenario scenario = scenarioService.getRandomScenario();
            ScenarioDialog dialog = new ScenarioDialog(this, scenario);
            dialog.setVisible(true); 
            if (isGameOver) return;
            if (dialog.getResult()) {
                updateStress(3); appendLogWithSyntax("試煉通過", "奧術壓力減緩。");
            } else {
                currentStress = Math.min(GameConfig.MAX_PRESSURE, currentStress + 3);
                updateStressUI(); appendRedLog("試煉失敗", "魔力回衝，壓力增加！");
                if (currentStress >= GameConfig.MAX_PRESSURE) gameOver("試煉失敗導致崩潰！");
            }
        });
        trialTimer.start();
    }

    public void appendLogWithSyntax(String skillName, String message) { appendLogToPane(skillName, message, Color.BLACK); }
    public void appendRedLog(String skillName, String message) { appendLogToPane(skillName, message, new Color(204, 0, 0)); }
    private void appendLogToPane(String skillName, String message, Color color) {
        StyledDocument doc = txtLog.getStyledDocument();
        Style style = txtLog.addStyle("LogStyle", null);
        StyleConstants.setForeground(style, color);
        try { doc.insertString(doc.getLength(), "[" + java.time.LocalTime.now().toString().substring(0, 5) + "] " + message + "\n", style); txtLog.setCaretPosition(doc.getLength()); } catch (Exception e) {}
    }
    
    public void recordPurchase(String itemName) { purchaseHistory.add(itemName); }
    public void updateGoldDisplay() {
        GuildStatus s = statusDao.queryByMemberId(currentMember.getId());
        if (s != null) { lblGold.setText(s.getCurrentGold() + " G"); currentMember.setTotalGold(s.getCurrentGold()); }
    }
    public void updateStress(int val) { this.currentStress = Math.max(0, this.currentStress - val); updateStressUI(); }
    private void updateStressUI() { prgPressure.setValue(currentStress); prgPressure.setString("壓力：" + currentStress + " / " + GameConfig.MAX_PRESSURE); }

    private void handleAccept(ActionEvent e) {
        if (currentLoot == null) return;
        try { accountantService.processOneLoot(currentLoot); updateGoldDisplay(); resetEvent(); } catch (BookkeeperException ex) { appendRedLog("奧術崩潰", ex.getMessage()); resetEvent(); }
    }

    private void handleStash(ActionEvent e) {
        if (currentLoot != null && currentLoot.isIllegal()) {
            // 🛡️ 執行 Stash：將物品從 UI 移至 shadowService (記憶體)
            // 因為 shadowService 不與 Database 同步，且查帳官不查 ShadowService，所以這絕對安全。
            shadowService.stashLoot(currentLoot);
            appendLogWithSyntax("黑帳隱藏", "物品 [" + currentLoot.getItemName() + "] 已藏入黑帳，查帳官無法發現。");
            resetEvent();
        }
    }

    private void handleLaunder(ActionEvent e) {
        shadowService.launderMoney(); 
        List<Loot> clean = shadowService.extractAll(); 
        for(Loot l : clean) queueService.addLoot(l);
        appendLogWithSyntax("洗白成功", "已將黑帳中的資產洗白並重新排隊。");
    }

    private void resetEvent() {
        btnAccept.setEnabled(false); btnRefuse.setEnabled(false); btnStash.setEnabled(false);
        lblAdventurer.setText("🌀 觀測中..."); lblLootInfo.setText(""); currentLoot = null;
    }

    private void stopAllSystems() {
        isGaming = false;
        if (gameLoop != null) gameLoop.stop();
        if (trialTimer != null) trialTimer.stop();
        if (auditTriggerTimer != null) auditTriggerTimer.stop(); 
        if (auditSequenceTimer != null) auditSequenceTimer.stop();
        if (generatorService != null) generatorService.stopGenerator(); 
        
        // 🛡️ 核心修正 1：遊戲結束時，如果商店視窗開著，強制關閉它
        if (shopWindow != null) {
            shopWindow.dispose();
            shopWindow = null;
        }
    }

    private void handleExit() {
        if (isGameOver) return; 
        int confirm = JOptionPane.showConfirmDialog(this, "確定要登出嗎？金幣將會清零且商店補貨！", "離開確認", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            stopAllSystems();
            statusDao.resetGold(currentMember.getId());
            shopDao.resetAllStock();
            new LoginUI().setVisible(true);
            dispose();
        }
    }

    private void gameOver(String reason) {
        if (isGameOver) return;
        isGameOver = true; 
        stopAllSystems();
        JOptionPane.showMessageDialog(this, "「萬象終局」：\n" + reason, "GAME OVER", JOptionPane.ERROR_MESSAGE);
        
        new GameResultUI(currentMember, currentMember.getTotalGold(), String.join(", ", purchaseHistory)).setVisible(true);
        
        statusDao.resetGold(currentMember.getId());
        shopDao.resetAllStock();
        currentMember.setTotalGold(0);
        dispose();
    }
}