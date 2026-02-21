package service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import vo.CartItemVO;
import model.ShopItem;

/**
 * [Service] CartService - 奧術採購中心 (Class)
 * 職責：負責管理記帳士的【奧術資產·採購推車 (Shopping Cart)】。
 * 核心教學語法：ArrayList (集合), Stream API (過濾與計算), Lambda (表達式)。
 */
public class CartService {

    // 技能：【奧術採購推車 (ArrayList)】 - 存放暫時選購的物資視圖
    private List<CartItemVO> cart = new ArrayList<>();

    /**
     * 技能：【推車收納 (List.add)】
     * @param item 從商店選取的視圖物件
     */
    public void addItem(CartItemVO item) {
        cart.add(item);
        System.out.println(">>> [採購動作] 已將 " + item.getProductName() + " 加入奧術採購清單。");
    }

    /**
     * 技能：【清空推車 (List.clear)】
     */
    public void clearCart() {
        cart.clear();
    }

    /**
     * 技能：【總價占卜 (Stream.mapToInt.sum)】
     * 使用 Java Stream API 快速計算推車內所有物資的總金幣需求。
     * @return 總金幣消耗量
     */
    public int calculateTotal() {
        return cart.stream()
                   .mapToInt(CartItemVO::getPrice)
                   .sum();
    }

    /**
     * 技能：【批量結帳邏輯 (Business Logic)】
     * 判定玩家金幣是否足以支付目前的採購清單。
     * @param currentGold 玩家當前金庫餘額
     * @return 是否結帳成功
     */
    public boolean canAfford(int currentGold) {
        return currentGold >= calculateTotal();
    }

    /**
     * 技能：【採購清單轉化 (Stream.map.collect)】
     * 將購物車內的品項名稱轉化為字串，以便寫入 Excel 戰績表。
     * @return 格式化後的採購明細字串
     */
    public String getPurchaseManifest() {
        if (cart.isEmpty()) return "無採購紀錄";
        return cart.stream()
                   .map(CartItemVO::getProductName)
                   .collect(Collectors.joining(", "));
    }

    /**
     * 獲取目前推車內的所有品項
     * @return List of CartItemVO
     */
    public List<CartItemVO> getCartItems() {
        return new ArrayList<>(this.cart);
    }
}