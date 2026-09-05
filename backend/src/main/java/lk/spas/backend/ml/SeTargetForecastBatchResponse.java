package lk.spas.backend.ml;

import jakarta.json.bind.annotation.JsonbProperty;
import java.util.List;

public class SeTargetForecastBatchResponse {

    @JsonbProperty("results")
    private List<SeTargetForecastResult> results;

    public SeTargetForecastBatchResponse() {
    }

    public List<SeTargetForecastResult> getResults() {
        return results;
    }

    public void setResults(List<SeTargetForecastResult> results) {
        this.results = results;
    }
}
