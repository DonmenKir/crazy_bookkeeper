package util;

// 關鍵：確保匯入的是 POI 的 Font，而非 java.awt.Font
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font; 
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import model.Score;
import config.GameConfig;

/**
 * [Util] ExcelUtil - 王國財務印記工具 (修正版)
 * 職責：解決 Font 類別衝突，確保戰績能正確刻印。
 * 核心教學：類別路徑衝突處理 (Import Conflict Resolution)。
 */
public class ExcelUtil {

    /**
     * 技能：【戰績刻印 (Excel Export)】
     */
    public static void saveScore(Score newScore) {
        List<Score> scores = loadAllScores();
        scores.add(newScore);
        
        // 依照金幣降序排列
        scores.sort((s1, s2) -> s2.getFinalGold().compareTo(s1.getFinalGold()));

        // 使用 try-with-resources 確保流會被關閉
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("傳說排行榜");
            
            // 1. 建立標頭樣式 (Header Style)
            CellStyle headerStyle = workbook.createCellStyle();
            
            // --- 修正重點：明確使用 POI 的 Font ---
            Font headerFont = workbook.createFont(); 
            headerFont.setBold(true);               // 設定粗體
            headerFont.setFontHeightInPoints((short) 12); // 設定字號
            headerStyle.setFont(headerFont);        // 將字體注入樣式
            
            // 2. 建立標頭列
            Row headerRow = sheet.createRow(0);
            String[] headers = {"排名", "記帳士", "最終金幣", "採購清單", "結算時間"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 3. 寫入戰績數據
            for (int i = 0; i < scores.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Score s = scores.get(i);
                
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(s.getNickname());
                row.createCell(2).setCellValue(s.getFinalGold());
                row.createCell(3).setCellValue(s.getPurchasedItems());
                row.createCell(4).setCellValue(s.getFormatTime());
            }

            // 自動校準寬度
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 4. 實體化封印
            try (FileOutputStream fileOut = new FileOutputStream(GameConfig.EXCEL_RANKING_PATH)) {
                workbook.write(fileOut);
                System.out.println(">>> [實體化成功] 戰績卷軸已更新。");
            }

        } catch (IOException e) {
            System.err.println("!!! [奧術反噬] Excel 檔案可能正被開啟中，無法寫入。");
        }
    }

    /**
     * 技能：【史詩回溯 (Excel Import)】
     */
    public static List<Score> loadAllScores() {
        List<Score> list = new ArrayList<>();
        File file = new File(GameConfig.EXCEL_RANKING_PATH);
        if (!file.exists()) return list;

        try (InputStream is = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Score s = new Score();
                s.setNickname(row.getCell(1).getStringCellValue());
                s.setFinalGold((int) row.getCell(2).getNumericCellValue());
                s.setPurchasedItems(row.getCell(3).getStringCellValue());
                s.setFormatTime(row.getCell(4).getStringCellValue());
                list.add(s);
            }
        } catch (Exception e) {
            System.err.println(">>> [回溯警告] 讀取 Excel 時發生未預期干擾。");
        }
        return list;
    }
}