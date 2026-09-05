package lk.spas.backend.dto;

public class RejectionReasonDto {

    private Integer id;
    private String reasonName;

    public RejectionReasonDto() {
    }

    public RejectionReasonDto(Integer id, String reasonName) {
        this.id = id;
        this.reasonName = reasonName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReasonName() {
        return reasonName;
    }

    public void setReasonName(String reasonName) {
        this.reasonName = reasonName;
    }
}
