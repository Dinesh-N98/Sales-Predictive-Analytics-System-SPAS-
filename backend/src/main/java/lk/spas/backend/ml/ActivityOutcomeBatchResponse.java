package lk.spas.backend.ml;

import jakarta.json.bind.annotation.JsonbProperty;
import java.util.List;

public class ActivityOutcomeBatchResponse {

    @JsonbProperty("results")
    private List<ActivityOutcomeResult> results;

    public ActivityOutcomeBatchResponse() {
    }

    public List<ActivityOutcomeResult> getResults() {
        return results;
    }

    public void setResults(List<ActivityOutcomeResult> results) {
        this.results = results;
    }
}
