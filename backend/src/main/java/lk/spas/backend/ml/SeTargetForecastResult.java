package lk.spas.backend.ml;

import jakarta.json.bind.annotation.JsonbProperty;

public class SeTargetForecastResult {

    private int id;
    private int prediction;

    @JsonbProperty("probability_hit_target")
    private double probabilityHitTarget;

    public SeTargetForecastResult() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrediction() {
        return prediction;
    }

    public void setPrediction(int prediction) {
        this.prediction = prediction;
    }

    public double getProbabilityHitTarget() {
        return probabilityHitTarget;
    }

    public void setProbabilityHitTarget(double probabilityHitTarget) {
        this.probabilityHitTarget = probabilityHitTarget;
    }
}
