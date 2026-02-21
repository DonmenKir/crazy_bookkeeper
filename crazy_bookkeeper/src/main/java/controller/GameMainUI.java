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
import exception.BookkeeperException;
import config.GameConfig;

/**
 * [Controller/UI] GameMainUI - 奧術指揮塔
 * 🛠️ 終極版整合機制：
 * 1. 王國查帳狀態機 (10秒預警 + 5秒臨檢沒收 + 帳本溯源重罰)。
 * 2. 絕對鎖定 isGameOver 防禦多重結算視窗。
 * 3. 採購明細紀錄 purchaseHistory 傳遞。
 * 4. 萬象歸零 (Roguelike 制)：結算或逃跑登出時，金幣一律強制清零。
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
    
    private LootGeneratorService generatorService;
    
    private volatile boolean isGaming = true; 
    private boolean isGameOver = false; 
    private int currentStress = 0;
    
    private Timer gameLoop;       
    private Timer trialTimer;     
    private Timer auditTriggerTimer;  
    private Timer auditSequenceTimer; 
    
    private int auditState = 0; 
    private int auditTick = 0;  
    
    private Loot currentLoot;     
    
    // 🛡️ 紀錄該場遊戲的真實採購明細
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
        btnShop.addActionListener(e -> new ShoppingCartUI(currentMember, this).setVisible(true));
        pnlBtns.add(btnShop);

        JButton btnExit = new JButton("登出系統 (Logout)");
        btnExit.addActionListener(e -> handleExit());
        pnlBtns.add(btnExit);

        btnAccept.setEnabled(false);
        btnRefuse.setEnabled(false);
        btnStash.setEnabled(false);
    }

    private void startGameEngine() {
        generatorService = new LootGeneratorService(queueService);
        generatorService.start();
        
        appendLogWithSyntax("身外化身", "背景系統已掛載，開始監控冒險者動態。");

        // 1. 常規事件處理循環
        gameLoop = new Timer(GameConfig.SPAWN_RATE_MS, e -> {
            if (!isGaming || isGameOver || btnAccept.isEnabled()) return; 

            this.currentLoot = queueService.getNextLoot();

            if (this.currentLoot != null) {
                Adventurer adv = adventurerDao.getRandomAdventurer();
                if (adv != null) {
                    currentStress += adv.getStressImpact();
                    updateStressUI();
                    lblAdventurer.setText("👤 冒險者：" + adv.getName());
                    
                    String lootStr = "📦 物品：" + currentLoot.getItemName() + " (" + currentLoot.getItemValue() + "G)";
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

        // 2. 🛡️ 王國查帳觸發系統 (30秒週期)
        auditTriggerTimer = new Timer(30000, e -> {
            if (!isGaming || isGameOver || auditState != 0) return;
            
            auditState = 1; 
            auditTick = 10; 
            
            lblAuditWarning.setForeground(new Color(255, 140, 0)); 
            lblAuditWarning.setVisible(true);
            appendLogToPane("王國查帳", "遠方傳來馬蹄聲... 稽核官將在 10 秒後抵達公會！", new Color(204, 102, 0));
            
            auditSequenceTimer.start();
        });
        auditTriggerTimer.start();

        // 3. 🛡️ 查帳序列計時器
        auditSequenceTimer = new Timer(1000, e -> {
            if (!isGaming || isGameOver) return;

            if (auditState == 1) { 
                // --- 預警倒數 (10秒) ---
                lblAuditWarning.setText("🔔 稽核官正在前來公會的路上... 預計抵達：" + auditTick + " 秒");
                if (auditTick == 3) {
                    appendLogToPane("王國查帳", "稽核官已到達門口！(剩餘 3 秒)", new Color(204, 102, 0));
                }
                auditTick--;
                
                if (auditTick < 0) {
                    auditState = 2; 
                    auditTick = 5;  
                    lblAuditWarning.setForeground(Color.RED);
                    appendRedLog("查帳開始", "🚨 稽核官已進入公會！立刻翻閱官方帳本！");
                    
                    // 掃描 Database 官方帳本
                    List<Loot> officialLedger = lootDao.queryByMember(currentMember.getId());
                    int fine = 0;
                    int caughtCount = 0;
                    
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
                        appendRedLog("帳目異常", "稽核官在帳本查獲 " + caughtCount + " 筆洗錢紀錄！強制追繳 " + fine + " G 並加重 3 點壓力！");
                        if (currentStress >= GameConfig.MAX_PRESSURE) gameOver("因查緝罰款導致壓力過大，公會崩潰！");
                    } else {
                        appendLogToPane("帳本清白", "官方帳目無異常，稽核官開始現場臨檢...", new Color(0, 100, 0));
                    }
                }
                
            } else if (auditState == 2) { 
                // --- 現場臨檢 (5秒) ---
                lblAuditWarning.setText("🚨 稽核官現場監視中... 剩餘時間：" + auditTick + " 秒");
                
                if (currentLoot != null && currentLoot.isIllegal()) {
                    appendRedLog("當場沒收", "稽核官逮到違規冒險者！物品 [" + currentLoot.getItemName() + "] 被沒收並驅離！");
                    resetEvent(); 
                }
                
                if (queueService.hasIllegalLoot()) {
                    List<Loot> allQueue = queueService.clearAndExtractAll();
                    int confiscatedCount = 0;
                    for (Loot l : allQueue) {
                        if (l.isIllegal()) {
                            confiscatedCount++; 
                        } else {
                            queueService.addLoot(l); 
                        }
                    }
                    if (confiscatedCount > 0) {
                        appendRedLog("隊列臨檢", "稽核官在隊列中攔截了 " + confiscatedCount + " 名攜帶違禁品的冒險者並當場驅離！");
                    }
                }
                
                auditTick--;
                if (auditTick < 0) {
                    auditState = 0; 
                    lblAuditWarning.setVisible(false);
                    appendLogWithSyntax("查帳結束", "公會盤查結束，稽核官已離開。");
                    auditSequenceTimer.stop(); 
                }
            }
        });

        // 4. 秘法突發檢定
        trialTimer = new Timer(15000, e -> {
            if (!isGaming || isGameOver) return;
            Scenario scenario = scenarioService.getRandomScenario();
            ScenarioDialog dialog = new ScenarioDialog(this, scenario);
            dialog.setVisible(true); 
            
            if (isGameOver) return;

            if (dialog.getResult()) {
                updateStress(3); 
                appendLogWithSyntax("秘法檢定", "試煉通過：詠唱正確，壓力已緩解。");
            } else {
                currentStress = Math.min(GameConfig.MAX_PRESSURE, currentStress + 3);
                updateStressUI();
                appendRedLog("奧術反噬", "試煉失敗：語法錯誤引發魔力回衝！");
                if (currentStress >= GameConfig.MAX_PRESSURE) gameOver("試煉失敗导致的崩潰！");
            }
        });
        trialTimer.start();
    }

    public void appendLogWithSyntax(String skillName, String message) {
        appendLogToPane(skillName, message, Color.BLACK);
    }

    public void appendRedLog(String skillName, String message) {
        appendLogToPane(skillName, message, new Color(204, 0, 0)); 
    }

    private void appendLogToPane(String skillName, String message, Color color) {
        StyledDocument doc = txtLog.getStyledDocument();
        Style style = txtLog.addStyle("LogStyle", null);
        StyleConstants.setForeground(style, color);
        
        String time = java.time.LocalTime.now().toString().substring(0, 5);
        String formattedMsg = "[" + time + "] " + message + "\n";
        
        try {
            doc.insertString(doc.getLength(), formattedMsg, style);
            txtLog.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void recordPurchase(String itemName) {
        purchaseHistory.add(itemName);
    }

    public void updateGoldDisplay() {
        if (currentMember == null) return;
        GuildStatus status = statusDao.queryByMemberId(currentMember.getId());
        if (status != null) {
            lblGold.setText(status.getCurrentGold() + " G");
            currentMember.setTotalGold(status.getCurrentGold());
        }
    }

    public void updateStress(int val) {
        this.currentStress = Math.max(0, this.currentStress - val);
        updateStressUI();
    }

    private void updateStressUI() {
        prgPressure.setValue(currentStress);
        prgPressure.setString("壓力：" + currentStress + " / " + GameConfig.MAX_PRESSURE);
    }

    private void handleAccept(ActionEvent e) {
        if (currentLoot == null) return;
        try {
            accountantService.processOneLoot(currentLoot);
            appendLogWithSyntax("奧術口袋", "結算完成：" + currentLoot.getItemName() + " (+" + currentLoot.getItemValue() + "G)");
            updateGoldDisplay();
            resetEvent();
        } catch (BookkeeperException ex) {
            appendRedLog("奧術崩潰", "結算報錯：" + ex.getMessage());
            resetEvent();
        }
    }

    private void handleStash(ActionEvent e) {
        if (currentLoot != null && currentLoot.isIllegal()) {
            shadowService.stashLoot(currentLoot);
            appendLogWithSyntax("幽影黑帳", "已將違禁品 [" + currentLoot.getItemName() + "] 轉移至黑帳空間，避開查帳。");
            resetEvent();
        }
    }

    private void handleLaunder(ActionEvent e) {
        shadowService.launderMoney(); 
        List<Loot> cleanLoots = shadowService.extractAll(); 
        
        if(cleanLoots.isEmpty()) {
            appendLogWithSyntax("時空洗白", "黑帳中目前沒有待洗白的物品。");
            return;
        }
        
        for(Loot l : cleanLoots) {
            queueService.addLoot(l);
        }
        appendLogWithSyntax("時空洗白", "成功洗白 " + cleanLoots.size() + " 件資產！它們已重新排入結算隊列。");
    }

    private void resetEvent() {
        btnAccept.setEnabled(false);
        btnRefuse.setEnabled(false);
        btnStash.setEnabled(false);
        lblAdventurer.setText("🌀 觀測中...");
        lblLootInfo.setText("");
        currentLoot = null;
    }

    private void stopAllSystems() {
        isGaming = false;
        if (gameLoop != null) gameLoop.stop();
        if (trialTimer != null) trialTimer.stop();
        if (auditTriggerTimer != null) auditTriggerTimer.stop(); 
        if (auditSequenceTimer != null) auditSequenceTimer.stop();
        if (generatorService != null) generatorService.stopGenerator(); 
        System.out.println(">>> [系統] 所有指揮塔定時器與背景傳送門已斷開。");
    }

    private void handleExit() {
        if (isGameOver) return; 
        
        int confirm = JOptionPane.showConfirmDialog(this, "確定要登出並斷開奧術連結嗎？\n(注意：放棄結算將使當前累積金庫歸零！)", "離開確認", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            stopAllSystems();
            // 玩家中途逃跑，金幣強制歸零
            statusDao.resetGold(currentMember.getId());
            new LoginUI().setVisible(true);
            dispose();
        }
    }

    private void gameOver(String reason) {
        if (isGameOver) return;
        isGameOver = true; 

        stopAllSystems();
        JOptionPane.showMessageDialog(this, "「萬象終局」已至：\n" + reason, "GAME OVER", JOptionPane.ERROR_MESSAGE);
        
        // 紀錄最後一刻的數據傳遞給結算畫面
        int finalGold = currentMember.getTotalGold();
        String finalPurchases = purchaseHistory.isEmpty() ? "尚未採購任何物資" : String.join(", ", purchaseHistory);
        
        new GameResultUI(currentMember, finalGold, finalPurchases).setVisible(true);
        
        // 結算完畢後，強制將資料庫與記憶體中的金幣歸零 (Roguelike 特性)
        statusDao.resetGold(currentMember.getId());
        currentMember.setTotalGold(0);
        
        dispose();
    }
}