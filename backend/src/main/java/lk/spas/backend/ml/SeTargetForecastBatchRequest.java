package lk.spas.backend.ml;

import jakarta.json.bind.annotation.JsonbProperty;
import java.util.List;

public class SeTargetForecastBatchRequest {

    @JsonbProperty("items")
    private List<SeTargetForecastRequestItem> items;

    public SeTargetForecastBatchRequest() {
    }

    public SeTargetForecastBatchRequest(List<SeTargetForecastRequestItem> items) {
        this.items = items;
    }

    public List<SeTargetForecastRequestItem> getItems() {
        return items;
    }

    public void setItems(List<SeTargetForecastRequestItem> items) {
        this.items = items;
    }
}
