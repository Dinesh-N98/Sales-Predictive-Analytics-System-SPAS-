package lk.spas.backend.dto;

public class FinancialLevelDto {

    private Integer id;
    private String levelName;

    public FinancialLevelDto() {
    }

    public FinancialLevelDto(Integer id, String levelName) {
        this.id = id;
        this.levelName = levelName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }
}
