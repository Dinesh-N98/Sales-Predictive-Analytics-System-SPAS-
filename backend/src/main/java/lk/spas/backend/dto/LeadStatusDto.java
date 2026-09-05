package lk.spas.backend.dto;

public class LeadStatusDto {

    private Integer id;
    private String statusName;

    public LeadStatusDto() {
    }

    public LeadStatusDto(Integer id, String statusName) {
        this.id = id;
        this.statusName = statusName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
