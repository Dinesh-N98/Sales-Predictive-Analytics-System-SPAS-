package lk.spas.backend.dto;

public class ActivityTypeDto {

    private Integer id;
    private String activityName;

    public ActivityTypeDto() {
    }

    public ActivityTypeDto(Integer id, String activityName) {
        this.id = id;
        this.activityName = activityName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
}
