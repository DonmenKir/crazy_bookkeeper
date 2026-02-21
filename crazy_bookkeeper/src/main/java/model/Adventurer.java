package model;

/**
 * [Model] Adventurer - 冒險者模型
 * 職責：代表出現在公會門口、帶著寶物請求結算的 NPC。
 */
public class Adventurer {
    private String name;
    private int stressImpact; // 帶來的壓力值

    public Adventurer(String name, int stressImpact) {
        this.name = name;
        this.stressImpact = stressImpact;
    }

    public String getName() { return name; }
    public int getStressImpact() { return stressImpact; }
}