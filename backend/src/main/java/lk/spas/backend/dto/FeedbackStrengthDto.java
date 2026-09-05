package lk.spas.backend.dto;

public class FeedbackStrengthDto {

    private Integer id;
    private String strengthName;

    public FeedbackStrengthDto() {
    }

    public FeedbackStrengthDto(Integer id, String strengthName) {
        this.id = id;
        this.strengthName = strengthName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStrengthName() {
        return strengthName;
    }

    public void setStrengthName(String strengthName) {
        this.strengthName = strengthName;
    }
}
