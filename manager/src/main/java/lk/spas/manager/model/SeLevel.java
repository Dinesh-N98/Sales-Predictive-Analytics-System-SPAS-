package lk.spas.manager.model;

public class SeLevel {
    private int id;
    private String levelName;

    public SeLevel() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { this.levelName = levelName; }

    @Override
    public String toString() {
        return levelName;
    }
}
