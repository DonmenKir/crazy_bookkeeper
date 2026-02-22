# 📜 Crazy Bookkeeper (秘法記帳士)

> 「記帳不只是算數字，更是在王國查帳官與黑市魔法師之間，玩一場在刀尖上跳舞的生存遊戲。」

《Crazy Bookkeeper》是一款結合了 **Roguelike 生存機制** 與 **奇幻公會經營** 的 Java 桌面應用程式 (Swing)。

本專案的特殊之處在於：遊戲中的每一個機制，都與 Java 核心語法（如執行緒、集合、Stream API、JDBC、Exception）有著深度的隱喻與掛鉤。這不僅是一款好玩的遊戲，更是一套專為 Java 學習者打造的「實戰架構演練場」。

---

## 🎮 遊戲背景與核心玩法

玩家將扮演一位與「時空祭壇」簽署契約的秘法記帳士，負責處理來自四面八方冒險者的戰利品。

### 核心生存機制

* **⏳ 冒險者洪流與奧術壓力 (Stress System)**
    冒險者會源源不絕地帶著物品來到公會（背景執行緒），每一次接待、每一場試煉失敗都會累積「奧術壓力」。當壓力值達到 **15 點** 時，記帳士心智崩潰，遊戲強制結束 (Game Over)。

* **⚖️ 官方帳本 vs. 黑暗魔法 (Risk & Reward)**
    * **合法結算 `[Accept]`**：將物品正式寫入資料庫（官方帳本）並獲得金幣。
    * **黑市誘惑**：若收納帶有 `Illegal` 標籤的黑暗卷軸，可獲得 **1.5 倍** 的暴利，但會留下「洗錢紀錄」。

* **🚨 王國查帳系統 (Audit State Machine)**
    * **預警與臨檢**：查帳官每 30 秒突擊一次，會有 10 秒橘色倒數預警，接著是 5 秒的紅色強制臨檢。
    * **當場沒收**：臨檢期間，若畫面上或隊列中出現違禁品，查帳官會當場沒收物品並強制驅離冒險者。
    * **帳本追溯（最致命）**：查帳官抵達瞬間會掃描你的「官方帳本 (Database)」。若查獲先前已 Accept 的違禁品紀錄，將面臨高額追繳罰款並加重壓力懲罰。

* **🕳️ 幽影黑帳與時空洗白 (Shadow Ledger)**
    * 遇到違禁品時，必須迅速點擊 **`[藏入黑帳]`**，將其轉移到官方查不到的記憶體空間 (`HashMap`)。
    * 待查帳官離開後，施展 **`[時空洗白]`**，將違禁品淨化為合法資產重新排隊結算。

* **💀 萬象歸零 (Roguelike Reset)**
    遊戲採單局結算制。一旦壓力爆表或中途登出，當局累積的金幣將會全數清零，確保每一局都是全新的挑戰。最終結果將連同「奧術商店採購明細」刻印至 Excel 傳說排行榜中。

---

## 🏗️ 系統架構 (Architecture)

本專案採用專業級的 **MVC + DAO (Data Access Object)** 混合架構。

```text
CrazyBookkeeper/
├── src/
│   ├── config/              # 全域設定法典 (GameConfig.java)
│   ├── controller/          # MVC - Controller (UI 邏輯與事件監聽)
│   │   ├── LoginUI.java     # 登入大門
│   │   ├── GameMainUI.java  # 指揮塔核心 (查帳狀態機、遊戲主迴圈)
│   │   ├── ShoppingCartUI.java # 商店採購
│   │   └── GameResultUI.java# 最終審計報告
│   ├── dao/                 # DAO - 資料庫存取介面
│   │   └── impl/            # DAO 實作層 (JDBC, PreparedStatement)
│   ├── exception/           # 自定義異常 (BookkeeperException)
│   ├── model/               # MVC - Model (資料實體 Entity)
│   ├── service/             # 業務邏輯層 (Business Logic)
│   │   ├── LootGeneratorService.java  # 背景執行緒：生成事件
│   │   ├── QueueService.java          # Thread-Safe 奧術隊列
│   │   ├── ShadowLedgerService.java   # 記憶體黑帳處理邏輯
│   │   └── impl/            # AccountantServiceImpl (核心結算)
│   ├── util/                # 工具類 (DbHelper, ExcelUtil, InsightBook)
│   └── vo/                  # View Object (視圖格式化物件)
└── sql/
    └── crazy_bookkeeper_final.sql # 完整的資料庫初始化腳本
```

---

## 🔮 遊戲機制與 Java 語法對照表 (Educational Mapping)

| 遊戲術語 (機制) | Java 語法/技術 | 技術職責與應用場景 |
| :--- | :--- | :--- |
| **奧術口袋 / 隊列** | `ArrayList<E>` | 動態接收背景不斷湧入的戰利品數據。 |
| **幽影黑帳** | `HashMap<K, V>` | 揮發性記憶體，儲存違禁品，資料庫 (DB) 無法追蹤。 |
| **時空洗白** | `String.replace()` | 透過字串替換改變物品屬性，將黑帳物品轉回合法。 |
| **身外化身** | `Thread / Runnable` | 背景執行緒獨立運作，持續生成冒險者。 |
| **絕對領域** | `synchronized` | 防止多執行緒發生資源爭奪 (Race Condition)。 |
| **官方帳本** | `JDBC / MySQL` | 透過 DAO 將交易紀錄持久化至資料庫。 |
| **防禦魔法陣** | `PreparedStatement` | 抵抗 SQL 注入攻擊，安全地操作數據。 |
| **奧術崩潰防護** | `try-catch / throw` | 攔截自定義異常，確保系統不因邏輯錯誤而崩潰。 |
| **戰績實體化** | `Apache POI` | 讀寫 Excel 檔案，進行檔案 I/O 持久化操作。 |

---

## 🚀 安裝與執行指南 (Installation)

1. **資料庫配置**：
   執行 `sql/crazy_bookkeeper_final.sql` 建立資料庫與初始化數據。
2. **金鑰設定**：
   於 `config/GameConfig.java` 修改你的 MySQL 帳號密碼。
3. **依賴庫匯入**：
   專案需匯入 `mysql-connector-j` 與 `Apache POI` 相關 JAR 包。
4. **啟動大門**：
   執行 `controller.LoginUI.main()`。
5. **管理員測試帳密**：`admin` / `admin123`。

> *"May your code be bug-free, and your ledgers be clean."*
