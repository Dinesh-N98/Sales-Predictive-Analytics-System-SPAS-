package lk.spas.backend.ml;

import jakarta.json.bind.annotation.JsonbProperty;
import java.util.List;

public class ActivityOutcomeBatchRequest {

    @JsonbProperty("items")
    private List<ActivityOutcomeRequestItem> items;

    public ActivityOutcomeBatchRequest() {
    }

    public ActivityOutcomeBatchRequest(List<ActivityOutcomeRequestItem> items) {
        this.items = items;
    }

    public List<ActivityOutcomeRequestItem> getItems() {
        return items;
    }

    public void setItems(List<ActivityOutcomeRequestItem> items) {
        this.items = items;
    }
}
