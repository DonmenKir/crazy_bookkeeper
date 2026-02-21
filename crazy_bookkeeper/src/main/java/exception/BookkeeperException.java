package exception;

/**
 * [Exception] BookkeeperException - 奧術崩潰異常
 * 職責：定義專屬於「秘法記帳士」系統的受檢異常 (Checked Exception)。
 * 當結算邏輯出錯（如：未登入、金庫溢位）時，會由系統主動拋出，
 * 並由 GameMainUI 的時空屏障 (try-catch) 進行攔截。
 */
public class BookkeeperException extends Exception {
    
    // 版本序列號，確保物件傳輸一致性
    private static final long serialVersionUID = 1L;

    /**
     * 建構子：接收錯誤訊息並刻印於異常中
     * @param message 具體的錯誤描述
     */
    public BookkeeperException(String message) {
        super(message);
    }

    /**
     * 複合建構子：接收訊息並保留原始錯誤原因
     * @param message 錯誤描述
     * @param cause 原始拋出的錯誤來源 (Throwable)
     */
    public BookkeeperException(String message, Throwable cause) {
        super(message, cause);
    }
}