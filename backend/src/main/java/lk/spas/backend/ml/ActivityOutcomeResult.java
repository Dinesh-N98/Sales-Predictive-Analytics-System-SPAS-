package lk.spas.backend.ml;

import java.util.Map;

public class ActivityOutcomeResult {

    private int id;
    private String prediction;
    private Map<String, Double> probabilities;

    public ActivityOutcomeResult() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    public Map<String, Double> getProbabilities() {
        return probabilities;
    }

    public void setProbabilities(Map<String, Double> probabilities) {
        this.probabilities = probabilities;
    }
}
