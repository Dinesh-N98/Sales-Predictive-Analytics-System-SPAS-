package lk.spas.backend.dto;

public class ClientTypeDto {

    private Integer id;
    private String typeName;

    public ClientTypeDto() {
    }

    public ClientTypeDto(Integer id, String typeName) {
        this.id = id;
        this.typeName = typeName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
