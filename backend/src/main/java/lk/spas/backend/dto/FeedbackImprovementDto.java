package lk.spas.backend.dto;

public class FeedbackImprovementDto {

    private Integer id;
    private String improvementName;

    public FeedbackImprovementDto() {
    }

    public FeedbackImprovementDto(Integer id, String improvementName) {
        this.id = id;
        this.improvementName = improvementName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImprovementName() {
        return improvementName;
    }

    public void setImprovementName(String improvementName) {
        this.improvementName = improvementName;
    }
}
