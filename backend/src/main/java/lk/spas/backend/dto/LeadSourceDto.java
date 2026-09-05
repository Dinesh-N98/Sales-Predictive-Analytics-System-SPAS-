package lk.spas.backend.dto;

public class LeadSourceDto {

    private Integer id;
    private String sourceName;

    public LeadSourceDto() {
    }

    public LeadSourceDto(Integer id, String sourceName) {
        this.id = id;
        this.sourceName = sourceName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
}
