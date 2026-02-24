# 奧術記帳士 (Crazy Bookkeeper) - 系統架構與設計文件
這是一款結合 **Java 核心語法學習** 與 **Roguelike 經營模擬** 的桌面應用程式。玩家扮演一名在秘法公會工作的記帳士，必須在處理物資、躲避王國查帳以及通過語法試煉之間取得平衡。

---

## 🏗️ 核心架構 (System Architecture)
本專案採用標準的 **MVC (Model-View-Controller)** 模式與 **DAO (Data Access Object)** 模式開發，確保邏輯、數據與介面的高度解耦。

### 1. 系統分層
* **View / Controller (Swing):** 負責渲染極簡主義介面並處理使用者輸入（如 `GameMainUI`, `AdminDashboardUI`）。
* **Service (Logic):** 處理複雜業務邏輯，如語法試煉生成 (`ScenarioService`)、物資生成佇列 (`QueueService`)。
* **DAO (Persistence):** 負責與實體資料庫及檔案進行交互（如 `MemberDAO`, `LootDAO`, `ShopDAO`）。
* **Model (Data):** 定義遊戲實體結構（如 `Loot`, `Member`, `Scenario`）。

### 2. 資料持久化方案
* **MySQL:** 儲存成員資料、官方帳本（Database）及商店物資。
* **Excel (Apache POI):** 儲存「傳說排行榜」戰績，支援跨局紀錄。
* **CSV (MS950):** 儲存「真理之書」題庫，支援管理員動態修改語法情境。

---

## 🎮 核心遊戲機制 (Game Mechanics)

### 💀 Roguelike 契約重置
* **單局制 (Per-run System):** 每當玩家「壓力爆表 (Game Over)」或「主動登出」，系統將觸發 **萬象歸零** 權能。
* **金幣歸零:** 當局累積的金幣將被清空（數據存回排行榜後重置）。
* **商店補貨:** 所有商店物資 (`shop_items`) 庫存將強制重置為初始值，確保下一局遊戲的平衡。

### ⚖️ 王國查帳系統 (Kingdom Audit)
**狀態機邏輯：**
1.  **預警期 (10s):** 畫面提示稽核官靠近，給予玩家藏匿違禁品的時間。
2.  **臨檢期 (5s):** 稽核官目擊現場。此時主畫面與排隊中的違禁品會被直接沒收。
* **官方帳本溯源:** 稽核官會掃描資料庫中的歷史紀錄。若查獲非法標記資產，將處以重罰並增加壓力值。
* **黑帳 (Shadow Ledger):** 玩家可將違禁品存入記憶體快取（而非 DB），此區間為查帳官的「權限盲區」，安全但需支付額外洗白成本。

### 🔮 秘法試煉 (Java Syntax Trial)
* **語法映射:** 遊戲將 Java 核心 API (`Collections`, `Stream`, `IO`, `Thread`) 轉化為秘法技能。
* **學習提示:** 試煉視窗會動態顯示語法的 **類型 (Type)** 與 **所屬套件 (Package)**，提升玩家對 Java 架構的熟悉度。

---

## 🛠️ 管理員控制台 (Admin Dashboard)
本系統為「大稽核官」提供了極簡主義設計的決策後台：

* **極簡美學:** 捨棄複雜色彩，採用白底、深灰字、細邊框設計，確保操作直覺。
* **題庫編輯器:** 內建 CSV 解析器，支援轉義雙引號，可直接在遊戲內修改本地 `InsightBook.csv`。
* **數據維護:** 具備權限識別功能，管理員可在排行榜中抹除錯誤紀錄或執行全局重置。

---

## 📁 專案目錄結構
```
src/
├── controller/         # UI 介面與事件控制 (GameMainUI, AdminDashboardUI, etc.)
├── dao/                # 數據存取介面
│   └── impl/           # 資料庫與檔案實作 (JDBC, Apache POI)
├── model/              # 實體類 (Loot, Member, ShopItem)
├── service/            # 核心邏輯 (試煉生成, 物資生成)
├── util/               # 工具類 (DbHelper, CSV Parser, InsightBook)
└── config/             # 遊戲數值設定 (GameConfig)

resources/ (或根目錄)
├── InsightBook.csv      # 語法題庫 (編碼: MS950)
└── Kingdom_Rankings.xlsx # 傳說排行榜
```

## 🚀 技術棧 (Tech Stack)
* Language: Java 11+
* UI Framework: Java Swing
* Database: MySQL 8.0

## 🚀 Library:
* Apache POI (Excel 處理)
* MySQL Connector (JDBC)
* Encoding: MS950 (Big5) / UTF-8
