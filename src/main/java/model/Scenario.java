package model;

import java.util.List;

public class Scenario {
    private String description;
    private String correctSyntax;
    private List<String> options;
    private String type;         // 🛡️ 新增：語法類型
    private String packagePath;  // 🛡️ 新增：所屬套件

    public Scenario(String description, String correctSyntax, List<String> options, String type, String packagePath) {
        this.description = description;
        this.correctSyntax = correctSyntax;
        this.options = options;
        this.type = type;
        this.packagePath = packagePath;
    }

    // Getters
    public String getDescription() { return description; }
    public String getCorrectSyntax() { return correctSyntax; }
    public List<String> getOptions() { return options; }
    public String getType() { return type; }
    public String getPackagePath() { return packagePath; }
}