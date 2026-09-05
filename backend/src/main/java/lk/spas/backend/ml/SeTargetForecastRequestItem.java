package lk.spas.backend.ml;

import jakarta.json.bind.annotation.JsonbProperty;

public class SeTargetForecastRequestItem {

    private int id;

    @JsonbProperty("avg_activity_per_day")
    private double avgActivityPerDay;

    @JsonbProperty("avg_followup_count")
    private double avgFollowupCount;

    @JsonbProperty("avg_duration_minutes")
    private double avgDurationMinutes;

    @JsonbProperty("sold_rate")
    private double soldRate;

    @JsonbProperty("se_level_name")
    private String seLevelName;

    public SeTargetForecastRequestItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAvgActivityPerDay() {
        return avgActivityPerDay;
    }

    public void setAvgActivityPerDay(double avgActivityPerDay) {
        this.avgActivityPerDay = avgActivityPerDay;
    }

    public double getAvgFollowupCount() {
        return avgFollowupCount;
    }

    public void setAvgFollowupCount(double avgFollowupCount) {
        this.avgFollowupCount = avgFollowupCount;
    }

    public double getAvgDurationMinutes() {
        return avgDurationMinutes;
    }

    public void setAvgDurationMinutes(double avgDurationMinutes) {
        this.avgDurationMinutes = avgDurationMinutes;
    }

    public double getSoldRate() {
        return soldRate;
    }

    public void setSoldRate(double soldRate) {
        this.soldRate = soldRate;
    }

    public String getSeLevelName() {
        return seLevelName;
    }

    public void setSeLevelName(String seLevelName) {
        this.seLevelName = seLevelName;
    }
}
